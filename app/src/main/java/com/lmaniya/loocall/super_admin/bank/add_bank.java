package com.lmaniya.loocall.super_admin.bank;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.help.MapsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class add_bank extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    public static EditText bank_name,phone_number,state,village_town,latitude,longitude,postal_code,get_email;
    ImageView imageView;
    Spinner district_value,state_value;
    FloatingActionButton submit;
    TextView add_location;



    public String result1,postURL="https://lmaniya.com.np/hunt_new/add_bank/add_bank.php";
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
    final Calendar myCalendar = Calendar.getInstance();
    String business_id;

    String [] Didtrict={"Achham",
            "Arghakhanchi",
            "Baglung",
            "Baitadi",
            "Bajhang",
            "Bajura",
            "Banke",
            " Bara",
            "Bardiya",
            "Bhaktapur",
            "Bhojpur",
            "Chitwan",
            "Dadeldhura",
            "Dailekh",
            "Dang Deokhuri",
            "Darchula",
            "Dhading",
            "Dhankuta",
            "Dhanusa",
            "Dolakha",
            "Dolpa",
            "Doti",
            "Gorkha",
            "Gulmi",
            "Humla",
            "Ilam",
            "Jajarkot",
            "Jhapa",
            "Jumla",
            "Kailali",
            "Kalikot",
            "Kanchanpur",
            "Kapilvastu",
            "Kaski",
            "Kathmandu",
            "Kavrepalanchok",
            "Khotang",
            "Lalitpur",
            "Lamjung",
            "Mahottari",
            "Makwanpur",
            "Manang",
            "Morang",
            "Mugu",
            "Mustang",
            "Myagdi",
            "Nawalparasi",
            "Nuwakot",
            "Okhaldhunga",
            "Palpa",
            "Panchthar",
            "Parbat",
            "Parsa",
            "Pyuthan",
            "Ramechhap",
            "Rasuwa",
            "Rautahat",
            "Rolpa",
            "Rukum	",
            "Rupandehi",
            "Salyan",
            "Sankhuwasabha",
            "Saptari",
            "Sarlahi",
            "Sindhuli",
            "Sindhupalchok",
            "Siraha",
            "Solukhumbu",
            "Sunsari",
            "Surkhet",
            "Syangja",
            "Tanahu",
            "Taplejung",
            "Terhathum",
            "Udayapur"};
    String []state_value_string={"1","2","3","4","5","6","7"};
    String [] economic_value_string={"high","middle","low"};
    String userid,district_get,state_get,echonomic_get,shop_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        bank_name=findViewById(R.id.identification_full_name);
        phone_number=findViewById(R.id.identification_phone_number);
        state=findViewById(R.id.identification_state);
        village_town=findViewById(R.id.identification_city);
        latitude=findViewById(R.id.identification_address1);
        longitude=findViewById(R.id.identification_address2);
        postal_code=findViewById(R.id.identification_postal_code);
        imageView=findViewById(R.id.imageview_id_picture);
        district_value=findViewById(R.id.spinner_country);
        state_value=findViewById(R.id.spinner_state);
        get_email=findViewById(R.id.identification_email_number);
        submit=findViewById(R.id.fab);
        relativeLayout=findViewById(R.id.relative);
        add_location=findViewById(R.id.get_latitude);
        setTitle("Add bank");
        progressDialog=new ProgressDialog(this);
        userid=getIntent().getStringExtra("id");
        business_id=getIntent().getStringExtra("business");



        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(add_bank.this, MapsActivity.class);
                intent.putExtra("select","3");
                startActivity(intent);

            }
        });




        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,state_value_string);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        state_value.setAdapter(arrayAdapter);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Didtrict);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        district_value.setAdapter(aa);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cooseimagefromfile();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isfileEmpty(bank_name) && isfileEmpty(phone_number) && isfileEmpty(
                        village_town
                ))
                {

                    upload_information();
                }
            }
        });







    }



    private void upload_information() {
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        final String name_value=bank_name.getText().toString();
        final String email_value=get_email.getText().toString();
        final String phone_value=phone_number.getText().toString();
        final String vaillage_town_value=village_town.getText().toString();
        final String latitude_value=latitude.getText().toString();
        final String longitude_value=longitude.getText().toString();
        final String postal_address_value=postal_code.getText().toString();

        double lat1 =Double.valueOf(latitude_value);

        double lon1 = Double.valueOf(longitude_value);
        double lat2 = 27.7888830;
        double lon2 = 85.345124;
       final Double dist= distance(lat1, lat2, lon1, lon2);


        StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Success = jsonObject.getString("success");
                            if (Success.equals("1")) {
                                Toast.makeText(add_bank.this,"bank added",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(add_bank.this, bank_activity.class));
                            }
                            else if (Success.equals("2"))
                            {
                                Toast.makeText(add_bank.this,"you already added shop please contact with admin",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(add_bank.this,"Error!"+e,Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(add_bank.this,"Error!"+error,Toast.LENGTH_LONG).show();
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
                params.put("postal_code",postal_address_value);
                params.put("bank_image",getStringImage(bitmap));
                params.put("bank_email",email_value);
                params.put("distance",String.valueOf(dist));




                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }









    // validation
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


    public static double distance(double lat1, double lat2, double lon1, double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        district_get= district_value.getItemAtPosition(position).toString();

        state_get=state_value.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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