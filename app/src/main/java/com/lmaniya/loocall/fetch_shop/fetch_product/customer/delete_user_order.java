package com.lmaniya.loocall.fetch_shop.fetch_product.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class delete_user_order extends AppCompatActivity {
    TextView product_name,price,Cancel_order,attribute;
    ImageView product_image;
    AlertDialog.Builder builder;
    String finalResult,unit_fetch,quantity_unit;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String nameholder,priceholder,imageholder,order_id,order_status,HttpURL="https://lmaniya.com.np/hunt_new/product/delete_customer_order.php";
    ProgressDialog progressDialog;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user_order);

        product_name=findViewById(R.id.product_title);
        price=findViewById(R.id.product_price);
        relativeLayout=findViewById(R.id.delete_order_layout);
        builder=new AlertDialog.Builder(this);
        progressDialog=new ProgressDialog(this);
        product_image=findViewById(R.id.product_image);
        attribute=findViewById(R.id.product_attribute);
        setTitle("Delete Order");
        nameholder=getIntent().getStringExtra("Product_name");
        priceholder=getIntent().getStringExtra("product_price");
        imageholder=getIntent().getStringExtra("Product_image");
        order_id=getIntent().getStringExtra("order_id");
        unit_fetch=getIntent().getStringExtra("Wt_unit");
        quantity_unit=getIntent().getStringExtra("Quantity");
        product_name.setText(nameholder);
        price.setText(priceholder);
        attribute.setText(quantity_unit);
        Picasso.get().load(imageholder).into(product_image);
        Cancel_order=findViewById(R.id.cancel_order);
        Cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_data();
                builder.setMessage("Do You want to delete your order history .......?")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (isConnected())
                                {
                                    delete_order(order_id,order_status);
                                }
                                else {
                                    Toast.makeText(delete_user_order.this,"",Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();

            }





            // Sending Menu Name, price , image to method to update on server.


        });



    }

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
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }



    private void delete_order(String order_id, String order_status) {
        class menuUpdateClass extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog= ProgressDialog.show(delete_user_order.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Toast.makeText(delete_user_order.this,s,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(delete_user_order.this, my_order_activity.class));
                finish();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("Order_id",params[0]);
                hashMap.put("Cart_deletion",params[1]);

                finalResult=httpParse.postRequest(hashMap,HttpURL);
                return finalResult;
            }
        }
        menuUpdateClass menuUpdateClass=new menuUpdateClass();
        menuUpdateClass.execute(order_id,order_status);

    }

    private void get_data() {
        order_status="Deleted";
    }
}

