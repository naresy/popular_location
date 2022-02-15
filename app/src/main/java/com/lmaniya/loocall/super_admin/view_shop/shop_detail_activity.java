package com.lmaniya.loocall.super_admin.view_shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.lmaniya.loocall.super_admin.super_admin_dash_board;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class shop_detail_activity extends AppCompatActivity implements OnMapReadyCallback {

   public static EditText shop_name,shop_phone,shop_latitude,shop_longitude,shop_village,start_date;
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
    final Calendar myCalendar = Calendar.getInstance();
    Bitmap bitmap;
    ProgressDialog progressDialog;
    String postURL="https://lmaniya.com.np/hunt_new/business_profile/update_shop_profile.php";
    TextView get_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail_activity2);
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
        progressDialog=new ProgressDialog(this);
        see_product=findViewById(R.id.see_product);
        get_map=findViewById(R.id.get_latitude);

        get_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Intent intent= new Intent(shop_detail_activity.this, MapsActivity.class);
              String value="1";
              intent.putExtra("select",value);
              startActivity(intent);


            }


        });
        shop_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cooseimagefromfile();
            }
        });
        see_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            upload_information();
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


        // update section

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDateandTime = sdf.format(new Date());

        start_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(shop_detail_activity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



    }


    // update

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();

        }


    };
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which we need put here in text view
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        start_date.setText(sdf.format(myCalendar.getTime()));
    }



    private void upload_information() {

        progressDialog.setTitle("Loading...");
        progressDialog.show();
        final String date_of_birth=start_date.getText().toString();

        final String phone_value=shop_phone.getText().toString();
        final String village_value=shop_village.getText().toString();
              final String shop_latitude_value=shop_latitude.getText().toString();
              final String shop_longitude_value=shop_longitude.getText().toString();
              final String name_value=shop_name.getText().toString();



        StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Success = jsonObject.getString("success");
                            if (Success.equals("1")) {
                                Toast.makeText(shop_detail_activity.this,"Your shop data updated\n",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(shop_detail_activity.this, super_admin_dash_board.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(shop_detail_activity.this,"Error!"+e,Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(shop_detail_activity.this,"Error!"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("shop_id",id);
               try {
                   params.put("image",getStringImage(bitmap));
               } catch (NullPointerException ex)
               {
                   params.put("image_value",image_value);
               }

                params.put("start_date",date_of_birth);
                params.put("phone",phone_value);
                params.put("village",village_value);
                params.put("shop_latitude",shop_latitude_value);
                params.put("shop_longitude",shop_longitude_value);
                params.put("shop_name",name_value);



                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }





    //

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        try {
            double lant = Double.valueOf(latitude_value);
            double lang = Double.valueOf(longitude_value);


            LatLng nepal = new LatLng(lant, lang);


            map.addMarker(new MarkerOptions().position(nepal).title("My shop"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(nepal, 15));
            enablelocation();

        } catch ( NumberFormatException ex)
        {
            ex.printStackTrace();
        }
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

    //
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
                shop_image.setImageBitmap(bitmap);
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
