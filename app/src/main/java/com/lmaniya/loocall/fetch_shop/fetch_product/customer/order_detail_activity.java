package com.lmaniya.loocall.fetch_shop.fetch_product.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class order_detail_activity extends AppCompatActivity {

    TextView product_name,Discount_persent,final_price,cancel_order,delete_order,order_status,order_date,attribute,bill,shopname;
    ImageView Product_image;
    ProgressDialog progressDialog;
    String FinalJSonObject,quantity,unit;
    FloatingActionButton add_card;
    HttpParse httpParse = new HttpParse();
    String ParseResult,delivery_status="shipped";
    public static String fetching_url = "https://lmaniya.com.np/hunt_new/product/customer_order_detail.php";
    HashMap<String, String> ResultHash = new HashMap<>();
    String Name_value, quantity_value, image_value, fetch_value, Bill_number,final_price_value,order_id,Product_name,product_image,product_price,status_order,order_date_value,user_status="Cancel By Customer",s_n;

    // check connection
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean internetConnected=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_activity);


        product_name = findViewById(R.id.product_title);
        Product_image=findViewById(R.id.product_image);
        order_date=findViewById(R.id.product_discount);
        Discount_persent=findViewById(R.id.product_discount);
        final_price=findViewById(R.id.product_price);
        relativeLayout=findViewById(R.id.order_detail_layout);
        cancel_order=findViewById(R.id.cancel);
        delete_order=findViewById(R.id.delete);
        attribute=findViewById(R.id.product_attribute);
        order_status=findViewById(R.id.order_status);
        shopname=findViewById(R.id.shop_name);
        setTitle("My order detail");
        quantity=getIntent().getStringExtra("Quantity");
        attribute.setText(quantity);
        swipeRefreshLayout=findViewById(R.id.swipe);
        bill=findViewById(R.id.bill_number);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpWebCall(fetch_value);
            }
        });

        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(order_detail_activity.this, cancel_order_activity.class);
                intent.putExtra("Product_name",Product_name);
                intent.putExtra("Product_image",product_image);
                intent.putExtra("product_price",product_price);
                intent.putExtra("order_id",order_id);
                intent.putExtra("Wt_unit",unit);
                intent.putExtra("Quantity",quantity);
                startActivity(intent);

            }
        });
        delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(order_detail_activity.this, delete_user_order.class);
                intent.putExtra("Product_name",Product_name);
                intent.putExtra("Product_image",product_image);
                intent.putExtra("product_price",product_price);
                intent.putExtra("order_id",order_id);
                intent.putExtra("Wt_unit",unit);
                intent.putExtra("Quantity",quantity);
                startActivity(intent);

            }
        });


        fetch_value = getIntent().getStringExtra("Product_id");
        order_id = getIntent().getStringExtra("order_id");
        Product_name = getIntent().getStringExtra("Product_name");
        product_image = getIntent().getStringExtra("Product_image");
        product_price = getIntent().getStringExtra("product_price");
        s_n=getIntent().getStringExtra("shop_name");
        shopname.setText(s_n);





        HttpWebCall(fetch_value);


    }

    // check connection

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    /**
     *  Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    private void setSnackbarMessage(String status,boolean showBar) {
        String internetStatus = "";
        if (status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
            internetStatus = "Internet Connected";
        } else {
            internetStatus = "Lost Internet Connection";
        }
        snackbar = Snackbar
                .make(relativeLayout, internetStatus, Snackbar.LENGTH_LONG)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if (internetStatus.equalsIgnoreCase("Lost Internet Connection")) {
            if (internetConnected) {
                snackbar.show();
                internetConnected = false;
            }
        } else {
            if (!internetConnected) {
                internetConnected = true;
                snackbar.show();
            }
        }
    }






    public void HttpWebCall(final String PreviousListViewClickedItem) {

        class HttpWebCallFunction extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.setRefreshing(true);

                progressDialog = ProgressDialog.show(order_detail_activity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {


                super.onPostExecute(httpResponseMsg);


                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new order_detail_activity.GetHttpResponse(order_detail_activity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("Order_id", params[0]);

                ParseResult = httpParse.postRequest(ResultHash, fetching_url);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    // Parsing Complete JSON Object.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {
        public Context context;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if (FinalJSonObject != null) {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);

                            // Storing Student Name, Phone Number, Class into Variables.
                            status_order=jsonObject.getString("order_status").toString();
                            Name_value = jsonObject.getString("product_name").toString();
                            image_value = jsonObject.getString("product_image").toString();
                            final_price_value=jsonObject.getString("product_final_price").toString();
                            quantity_value=jsonObject.getString("product_quantity").toString();
                            order_date_value=jsonObject.getString("order_date").toString();
                            Bill_number=jsonObject.getString("Bill_number").toString();


                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            product_name.setText(Name_value);
            order_status.setText(status_order);
            final_price.setText(final_price_value);
            String check=order_status.getText().toString();
            order_date.setText(order_date_value);
            bill.setText(Bill_number);
            if (user_status.equals(check)||check.equals(delivery_status))
            {
                cancel_order.setVisibility(View.INVISIBLE);
            }
            else {
                cancel_order.setVisibility(View.VISIBLE);
            }
            try {
                Picasso.get().load(image_value).into(Product_image);
            } catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }


        }
    }
}
