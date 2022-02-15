package com.lmaniya.loocall.super_admin.bank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.help.MapsActivity;
import com.lmaniya.loocall.login.helper.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class update_bank extends AppCompatActivity implements OnMapReadyCallback {

   public static EditText bank_name,bank_email,bank_phone,bank_address,bank_latitude,bank_longitude;
    ImageView bank_image;
    String name_value,email_value,phone_value,address_value,latitude_value,longitude_value,image_value,id;
    ProgressDialog progressDialog ;
    Button update,delete;
    TextView get_lat;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    String postURL="https://lmaniya.com.np/hunt_new/add_bank/update_bank.php";
    String delete_url="https://lmaniya.com.np/hunt_new/add_bank/delete_bank.php";
    SessionManager sessionManager;
    LinearLayout linearLayout;
    Bitmap bitmap;
    GoogleMap map;
    TextView get_latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bank);
        relativeLayout=findViewById(R.id.relative);
        bank_name=findViewById(R.id.identification_full_name);
        bank_email=findViewById(R.id.identification_email_number);
        bank_phone=findViewById(R.id.identification_phone_number);
        bank_address=findViewById(R.id.identification_city);
        bank_latitude=findViewById(R.id.identification_address1);
        bank_longitude=findViewById(R.id.identification_address2);
        bank_image=findViewById(R.id.imageview_id_picture);
        name_value=getIntent().getStringExtra("bank_name");
        email_value=getIntent().getStringExtra("bank_email");
        phone_value=getIntent().getStringExtra("bank_phone");
        address_value=getIntent().getStringExtra("bank_address");
        latitude_value=getIntent().getStringExtra("bank_latitude");
        longitude_value=getIntent().getStringExtra("bank_longitude");
        image_value=getIntent().getStringExtra("bank_image");
        id=getIntent().getStringExtra("ListViewValue");
        bank_name.setText(name_value);
        bank_email.setText(email_value);
        bank_phone.setText(phone_value);
        bank_address.setText(address_value);
        bank_latitude.setText(latitude_value);
        bank_longitude.setText(longitude_value);
        Picasso.get().load(image_value).into(bank_image);
        update=findViewById(R.id.update);
        setTitle("Bank Detail");
        progressDialog=new ProgressDialog(this);
        delete=findViewById(R.id.delete);
        linearLayout=findViewById(R.id.map_map);
        get_lat=findViewById(R.id.get_latitude);
        get_latitude=findViewById(R.id.get_latitude);
        get_latitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(update_bank.this, MapsActivity.class);
                intent.putExtra("select","7");
                startActivity(intent);

            }
        });

        sessionManager=new SessionManager(getApplicationContext());
        if (sessionManager.isroller()!=3)
        {
            linearLayout.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            get_lat.setVisibility(View.GONE);
            bank_name.setFocusable(false);
            bank_name.setCursorVisible(false);
            bank_address.setFocusable(false);
            bank_address.setCursorVisible(false);
            bank_email.setFocusable(false);
            bank_email.setCursorVisible(false);
            bank_phone.setFocusable(false);
            bank_phone.setCursorVisible(false);
            bank_latitude.setFocusable(false);
            bank_latitude.setCursorVisible(false);
            bank_longitude.setFocusable(false);
            bank_longitude.setCursorVisible(false);

        }
        else {
            linearLayout.setVisibility(View.GONE);
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_information();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_information();
            }
        });
        bank_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cooseimagefromfile();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        double lant=Double.valueOf(latitude_value);
        double lang=Double.valueOf(longitude_value);

        LatLng nepal = new LatLng(lant,lang);


        map.addMarker(new MarkerOptions().position(nepal).title(name_value));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nepal, 15));

        enablelocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void enablelocation() {



        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        map.setMyLocationEnabled(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        enablelocation();
    }

    private void delete_information() {


        StringRequest stringRequest=new StringRequest(Request.Method.POST, delete_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Success = jsonObject.getString("success");
                            if (Success.equals("1")) {
                                Toast.makeText(update_bank.this,"bank delete",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(update_bank.this, bank_activity.class));
                            }
                            else if (Success.equals("2"))
                            {
                                Toast.makeText(update_bank.this,"you already added shop please contact with admin",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(update_bank.this,"Error!"+e,Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(update_bank.this,"Error!"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("bank_id",id);





                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }




    private void upload_information() {
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        final String name_value=bank_name.getText().toString();
        final String email_value=bank_email.getText().toString();
        final String phone_value=bank_phone.getText().toString();
        final String vaillage_town_value=bank_address.getText().toString();
        final String latitude_value=bank_latitude.getText().toString();
        final String longitude_value=bank_longitude.getText().toString();



        StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Success = jsonObject.getString("success");
                            if (Success.equals("1")) {
                                Toast.makeText(update_bank.this,"bank update",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(update_bank.this, bank_activity.class));
                            }
                            else if (Success.equals("2"))
                            {
                                Toast.makeText(update_bank.this,"you already added shop please contact with admin",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(update_bank.this,"Error!"+e,Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(update_bank.this,"Error!"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("bank_name",name_value);
                params.put("bank_phone",phone_value);
                params.put("bank_address",vaillage_town_value);
                params.put("bank_latitude",latitude_value);
                params.put("bank_longitude",longitude_value);
                params.put("bank_email",email_value);
                try {
                    params.put("bank_image",getStringImage(bitmap));
                } catch (NullPointerException ex)
                {
                    params.put("image",image_value);
                }

                params.put("bank_id",id);





                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                bank_image.setImageBitmap(bitmap);
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
}