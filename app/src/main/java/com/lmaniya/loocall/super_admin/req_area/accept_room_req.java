package com.lmaniya.loocall.super_admin.req_area;

import androidx.annotation.NonNull;
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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.super_admin.super_admin_dash_board;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class accept_room_req extends AppCompatActivity implements OnMapReadyCallback {
    TextView name,type,phone,address;
    ImageView image1,image2;
    static public ReadMoreTextView textView;

    private GoogleMap mMap;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    LinearLayout relativeLayout;
    private boolean internetConnected=true;
    String name_Value,phone_value,address_value,image1_value,image2_value,latitude,longitude,decription,room_type,id;
    Button reject,accept;
    ProgressDialog progressDialog;
    String postURL="https://lmaniya.com.np/hunt_new/add_room/accept_room.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_room_req);
        name=findViewById(R.id.owner_name);
        type=findViewById(R.id.room_type);
        phone=findViewById(R.id.owner_phone);
        address=findViewById(R.id.address);
        image1=findViewById(R.id.image1);
        relativeLayout=findViewById(R.id.relative);
        image2=findViewById(R.id.image2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        name_Value=getIntent().getStringExtra("owner_name");
        id=getIntent().getStringExtra("id");
        address_value=getIntent().getStringExtra("address");
        latitude=getIntent().getStringExtra("latitude");
        longitude=getIntent().getStringExtra("longitude");
        address_value=getIntent().getStringExtra("phone");
        address_value=getIntent().getStringExtra("address");
        image1_value=getIntent().getStringExtra("image1");
        image2_value=getIntent().getStringExtra("image2");
        phone_value=getIntent().getStringExtra("phone");
        decription=getIntent().getStringExtra("description");
        room_type=getIntent().getStringExtra("room_type");
        textView=findViewById(R.id.description);
        textView.setText(decription);
        accept=findViewById(R.id.accept);
        reject=findViewById(R.id.reject);
        progressDialog=new ProgressDialog(this);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
upload_information();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        name.setText(name_Value);
        address.setText(address_value);
        phone.setText(phone_value);
        type.setText(room_type);
        try {
            Picasso.get().load(image1_value).into(image1);
            Picasso.get().load(image2_value).into(image2);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }




        mapFragment.getMapAsync(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Double lant=Double.valueOf(latitude);
        Double lang=Double.valueOf(longitude);
        LatLng nepal = new LatLng(lant,lang);

        try {
            mMap.addMarker(new MarkerOptions().position(nepal).title("My shop"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nepal, 15));
        } catch (NullPointerException ex)
        {

            ex.printStackTrace();
            Toast.makeText(accept_room_req.this,"your device does not support google map ",Toast.LENGTH_SHORT).show();
        }
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
        mMap.setMyLocationEnabled(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        enablelocation();
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


    private void upload_information() {

        progressDialog.setTitle("Loading...");
        progressDialog.show();




        StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Success = jsonObject.getString("success");
                            if (Success.equals("1")) {
                                Toast.makeText(accept_room_req.this,"request accepted\n",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(accept_room_req.this, super_admin_dash_board.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(accept_room_req.this,"Error!"+e,Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(accept_room_req.this,"Error!"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("room_id",id);






                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}