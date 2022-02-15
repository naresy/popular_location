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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class update_user_cart_activity extends AppCompatActivity {
    TextView Product_name,product_price,product_quantity,product_total_price,update,cancel,attribute;
    Button Minus,plus;
    ImageView product_image;
    AlertDialog.Builder builder;
    String idholder,namehoder,priceholder,quantityholder,total_priceholder,imageholder;
    ProgressDialog progressDialog;
    String finalResult,category_type;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    public  static String HttpURL="https://lmaniya.com.np/hunt_new/product/update_cart.php";
    static int integer=1;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_cart_activity);


        Product_name=findViewById(R.id.product_title);
        product_price=findViewById(R.id.product_price);
        product_quantity=findViewById(R.id.quantity);
        builder=new AlertDialog.Builder(this);
        product_total_price=findViewById(R.id.sub_total);
        Minus=findViewById(R.id.quantity_minus);
        attribute=findViewById(R.id.product_attribute);
        plus=findViewById(R.id.quantity_plus);
        product_image=findViewById(R.id.product_image);
        progressDialog=new ProgressDialog(this);
        update=findViewById(R.id.Update);
        cancel=findViewById(R.id.cancel);
        idholder=getIntent().getStringExtra("Cart_id");
        namehoder=getIntent().getStringExtra("Product_name");
        priceholder=getIntent().getStringExtra("Product_price");
        quantityholder=getIntent().getStringExtra("Product_quantity");

        imageholder=getIntent().getStringExtra("Product_image");
        Product_name.setText(namehoder);
        product_price.setText(priceholder);
        product_quantity.setText(quantityholder);
        attribute.setText(quantityholder);
        product_total_price.setText(total_priceholder);
        Picasso.get().load(imageholder).into(product_image);
        relativeLayout=findViewById(R.id.user_cart_update_layout);
        get_data();

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });
        Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Do You want to cancel updating information..?")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startActivity(new Intent(update_user_cart_activity.this,my_cart_activity.class));
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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDataFromEditText();
                builder.setMessage("Do You want to updating information..?")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isConnected())
                                {
                                    integer=1;

                                    Update_cart(idholder, quantityholder, total_priceholder);
                                }
                                else {
                                    Toast.makeText(update_user_cart_activity.this,"Please Check Your Internet Connection",Toast.LENGTH_SHORT).show();
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

                // Sending Menu Name, price , image to method to update on server.

            }
        });

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




    private void get_data() {
    }

    private void increase() {
        integer=integer+1;
        display(integer);

    }

    private void display(int integer) {
        product_quantity.setText(""+integer);
        final String get_price=product_price.getText().toString();
        final int set_price=Integer.parseInt(get_price);
        final int total=integer*set_price;
        final String set_total=Integer.toString(total);
        product_total_price.setText(set_total);
    }

    private void decrease() {
        if (integer>1)
        {
            integer=integer-1;
            display(integer);

        }

    }

    private void Update_cart(String id, String quantity, String price) {
        class menuUpdateClass extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog= ProgressDialog.show(update_user_cart_activity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Toast.makeText(update_user_cart_activity.this,s.toString(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(update_user_cart_activity.this,my_cart_activity.class));
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("Cart_id",params[0]);
                hashMap.put("Product_quantity",params[1]);
                hashMap.put("Total_price",params[2]);
                finalResult=httpParse.postRequest(hashMap,HttpURL);
                return finalResult;
            }
        }
        menuUpdateClass menuUpdateClass=new menuUpdateClass();
        menuUpdateClass.execute(id,quantity,price);


    }

    private void GetDataFromEditText() {
        quantityholder=product_quantity.getText().toString();
        total_priceholder=product_total_price.getText().toString();




    }
}