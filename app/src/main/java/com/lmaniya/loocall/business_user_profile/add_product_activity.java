package com.lmaniya.loocall.business_user_profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class add_product_activity extends AppCompatActivity {
    public static EditText product_name,start_price,final_price,product_description,code;
    ImageView imageView;
    Button add,cancel;
    TextView add_image;
    String shop_id,user_id;


    public String result1,postURL="https://lmaniya.com.np/hunt_new/product/add_product.php";
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    LinearLayout relativeLayout;
    private boolean internetConnected=true;
    ProgressDialog progressDialog;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_activity);
        add_image=findViewById(R.id.add_image);
        imageView=findViewById(R.id.imageview_id_picture);
        product_name=findViewById(R.id.product_name);
        start_price=findViewById(R.id.product_start_price);
        final_price=findViewById(R.id.product_final_price);
        product_description=findViewById(R.id.product_description);
        add=findViewById(R.id.add_product);
        cancel=findViewById(R.id.cancel);
        relativeLayout=findViewById(R.id.relative);
        shop_id=getIntent().getStringExtra("shop_id");
        user_id=getIntent().getStringExtra("user_id");
        progressDialog=new ProgressDialog(this);
        code=findViewById(R.id.code);
        setTitle("Add Product");
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cooseimagefromfile();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isfileEmpty(product_name)&& isfileEmpty(start_price)&& isfileEmpty(final_price)&& isfileEmpty(product_description)&&isfileEmpty(code))
                {
                    upload_information();
                }
            }
        });


    }

    private void upload_information() {

        progressDialog.setTitle("Loading...");
        progressDialog.show();
        final String name_value=product_name.getText().toString();
        final String start_price_value=start_price.getText().toString();
        final String final_price_value=final_price.getText().toString();
        final String description=product_description.getText().toString();
        final  String code_value=code.getText().toString();



        StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String Success = jsonObject.getString("success");
                        if (Success.equals("1")) {
                            Toast.makeText(add_product_activity.this,"Product added successfully \n" ,Toast.LENGTH_LONG).show();
                            startActivity(new Intent(add_product_activity.this, add_product_activity.class));
                        }
                        if (Success.equals("2")) {
                            Toast.makeText(add_product_activity.this,"product code already exist\n" ,Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(add_product_activity.this,"Error!"+e,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(add_product_activity.this,"Error!"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("product_name",name_value);
                params.put("product_initial_price",start_price_value);
                params.put("product_final_price",final_price_value);
                params.put("product_description",description);
                params.put("sku",code_value);
                try {
                    params.put("product_image",getStringImage(bitmap));
                } catch (NullPointerException ex)
                {
                    ex.printStackTrace();
                    Toast.makeText(add_product_activity.this,"please upload image also",Toast.LENGTH_SHORT).show();
                }

                params.put("shop_id",shop_id);
                params.put("user_id",user_id);





                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public static boolean isfileEmpty(EditText view) {
        String value = view.getText().toString();
        if (value.length() > 0) {
            return true;
        } else
            view.setError("enter value");
        return false;
    }
    public static boolean istext(TextView view) {
        String value = view.getText().toString();
        if (value.length() > 0) {
            return true;
        } else
            view.setError("enter value");
        return false;
    }


    private void cooseimagefromfile() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String getStringImage(Bitmap bitmap1)
    {
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
        byte[]imagebutearry=byteArrayOutputStream.toByteArray();
        String encodeImage= Base64.encodeToString(imagebutearry,Base64.DEFAULT);
        return encodeImage;

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
}
