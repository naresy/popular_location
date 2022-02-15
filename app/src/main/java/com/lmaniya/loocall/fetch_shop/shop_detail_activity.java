package com.lmaniya.loocall.fetch_shop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.fetch_shop.fetch_product.fetch_product;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class shop_detail_activity extends AppCompatActivity implements OnMapReadyCallback {
    TextView shop_name,shop_phone,shop_latitude,shop_longitude,shop_village,start_date;
    ImageView shop_image;
    String name_value,phone_value,latitude_value,longitude_value,village_value,image_value,start_date_value,id;
    Button see_product;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    HashMap<String, String> hashMap = new HashMap<>();
    GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail_activity);
        shop_name=findViewById(R.id.identification_full_name);
        shop_phone=findViewById(R.id.identification_phone_number);
        shop_latitude=findViewById(R.id.identification_address1);
        shop_longitude=findViewById(R.id.identification_address2);
        start_date=findViewById(R.id.identification_birth_date_text_view);
        shop_village=findViewById(R.id.identification_city);
        shop_image=findViewById(R.id.imageview_id_picture);
        relativeLayout=findViewById(R.id.product_detail_layout);
        name_value=getIntent().getStringExtra("name");
        phone_value=getIntent().getStringExtra("phone");
        latitude_value=getIntent().getStringExtra("latitude");
        longitude_value=getIntent().getStringExtra("longitude");
        village_value=getIntent().getStringExtra("village");
        image_value=getIntent().getStringExtra("image");
        start_date_value=getIntent().getStringExtra("star_date");
        id=getIntent().getStringExtra("ListViewValue");
        see_product=findViewById(R.id.see_product);
        see_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(shop_detail_activity.this, fetch_product.class);
                intent.putExtra("ListViewValue",id);
                startActivity(intent);
            }
        });
        shop_name.setText(name_value);
        shop_phone.setText(phone_value);
        shop_village.setText(village_value);
        shop_latitude.setText(latitude_value);
        shop_longitude.setText(longitude_value);
        start_date.setText(start_date_value);
        Picasso.get().load(image_value).into(shop_image);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        relativeLayout=findViewById(R.id.product_detail_layout);
        mapFragment.getMapAsync(this);
        setTitle("Shop Detail");



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
