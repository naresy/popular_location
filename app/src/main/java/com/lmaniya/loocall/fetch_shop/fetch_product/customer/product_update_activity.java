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

public class product_update_activity extends AppCompatActivity {

    TextView Product_name, product_quantity, product_price, Total_price, remove_from_cart, attribute;
    String name_value, quantity_value, price_value, total_value, image_value,idholder;
    ImageView product_image;
    ProgressDialog progressDialog;
    String finalResult;
    public static String HttpUrlDeleteRecord = "https://lmaniya.com.np/hunt_new/product/remove_cart.php";
    HttpParse httpParse = new HttpParse();
    HashMap<String, String> hashMap = new HashMap<>();
    static String fetch_value;
    AlertDialog.Builder builder;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected = true;
    int quantity,price,total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_update_activity);

        Product_name = findViewById(R.id.product_title);
        product_quantity = findViewById(R.id.quantity);
        builder = new AlertDialog.Builder(this);
        product_price = findViewById(R.id.product_price);
        Total_price = findViewById(R.id.sub_total);
        attribute = findViewById(R.id.product_attribute);
        product_image = findViewById(R.id.product_image);
        remove_from_cart = findViewById(R.id.remove);
        relativeLayout = findViewById(R.id.product_update_layout);
        progressDialog = new ProgressDialog(this);
        idholder=getIntent().getStringExtra("Cart_id");
        name_value=getIntent().getStringExtra("Product_name");
        price_value=getIntent().getStringExtra("Product_price");
        quantity_value=getIntent().getStringExtra("Product_quantity");
       quantity=Integer.parseInt(quantity_value);
       price=Integer.parseInt(price_value);
       total=quantity*price;
       Total_price.setText(String.valueOf(total));
        image_value=getIntent().getStringExtra("Product_image");
        Product_name.setText(name_value);
        product_quantity.setText(quantity_value);
        product_price.setText(price_value);
        Picasso.get().load(image_value).into(product_image);
        remove_from_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setMessage("Do you want to remove your cart")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (isConnected()) {
                                    StudentDelete(idholder);


                                    finish();
                                } else {
                                    Toast.makeText(product_update_activity.this, "Check Your Internet connection", Toast.LENGTH_SHORT).show();

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
        });


    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
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
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status, false);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
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

    private void setSnackbarMessage(String status, boolean showBar) {
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


    public void StudentDelete(final String StudentID) {


        class StudentDeleteClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();


            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);


                startActivity(new Intent(product_update_activity.this, my_cart_activity.class));

                Toast.makeText(product_update_activity.this, "Item removed from your list ", Toast.LENGTH_LONG).show();

                finish();
                progressDialog.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                // Sending STUDENT id.
                hashMap.put("Cart_id", params[0]);

                finalResult = httpParse.postRequest(hashMap, HttpUrlDeleteRecord);
                progressDialog.dismiss();

                return finalResult;
            }
        }

        StudentDeleteClass studentDeleteClass = new StudentDeleteClass();

        studentDeleteClass.execute(StudentID);
    }
}