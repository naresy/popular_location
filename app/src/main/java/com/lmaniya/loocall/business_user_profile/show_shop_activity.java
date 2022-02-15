package com.lmaniya.loocall.business_user_profile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class show_shop_activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView name,phone,start_date,town_village,postal_code,latitude,longitude;
    ImageView image;
    Button add_product;
    static LatLng latLng;
    static double lang,lant;
    ProgressDialog progressDialog;
    HashMap<String, String> ResultHash = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String FinalJSonObject,ParseResult,fetching_url="https://lmaniya.com.np/hunt_new/business_profile/fetch_my_shop.php";
    String phone_value,name_value,start_value,village_value,postal_value,shop_id,shop_image;
    // check connection
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    HashMap<String, String> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shop_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        relativeLayout=findViewById(R.id.product_detail_layout);
        mapFragment.getMapAsync(this);
        progressDialog=new ProgressDialog(this);

        final String id=getIntent().getStringExtra("id");
        name=findViewById(R.id.identification_full_name);
        phone=findViewById(R.id.identification_phone_number);
        image=findViewById(R.id.imageview_id_picture);
        start_date=findViewById(R.id.identification_birth_date_text_view);
        town_village=findViewById(R.id.identification_city);
        postal_code=findViewById(R.id.identification_postal_code);
        latitude=findViewById(R.id.identification_address1);
        longitude=findViewById(R.id.identification_address2);
        setTitle("My shop");
        image=findViewById(R.id.imageview_id_picture);

        setTitle("My shop");
        add_product=findViewById(R.id.add_product);
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(show_shop_activity.this,add_product_activity.class);
                intent.putExtra("shop_id",shop_id);
                intent.putExtra("user_id",id);
                startActivity(intent);

            }
        });
        progressDialog=new ProgressDialog(this);
        HttpWebCall(id);

    }

    //update




    public void HttpWebCall(final String PreviousListViewClickedItem) {

        class HttpWebCallFunction extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setTitle("Loading...");
                progressDialog.show();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {


                super.onPostExecute(httpResponseMsg);




                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new show_shop_activity.GetHttpResponse(show_shop_activity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("selection", params[0]);

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
                            name_value = jsonObject.getString("shop_name").toString();
                            phone_value = jsonObject.getString("shop_phone").toString();
                            start_value = jsonObject.getString("start_date").toString();
                            village_value = jsonObject.getString("shop_city").toString();
                            postal_value = jsonObject.getString("postal_code").toString();
                            lant=jsonObject.getDouble("shop_latitude");
                            lang=jsonObject.getDouble("shop_longitude");
                            shop_id=jsonObject.getString("shop_id");
                            shop_image=jsonObject.getString("shop_image");


                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                progressDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // Setting Student Name, Phone Number, Class into TextView after done all process .
            name.setText(name_value);
            phone.setText(phone_value);
            start_date.setText(start_value);

            town_village.setText(village_value);
            postal_code.setText(postal_value);
            latitude.setText(String.valueOf(lant));
            longitude.setText(String.valueOf(lang));
            Picasso.get().load(shop_image).into(image);
            LatLng nepal = new LatLng(lant,lang);
            progressDialog.dismiss();
            try {
                mMap.addMarker(new MarkerOptions().position(nepal).title("My shop"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nepal, 15));
            } catch (NullPointerException ex)
            {
                progressDialog.dismiss();
                ex.printStackTrace();
                Toast.makeText(show_shop_activity.this,"your device does not support google map ",Toast.LENGTH_SHORT).show();
            }


        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



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
