package com.lmaniya.loocall.help;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class help_and_support extends AppCompatActivity {
    EditText user_name,user_phone,user_type,user_message;
    TextView address,lat,lang;
    CircleImageView pressed;
    LocationTrack locationTrack;
    public  String  result;
    public String result1,postURL="https://lmaniya.com.np/hunt_new/upload_help.php";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
        address=findViewById(R.id.get_address);
        lat=findViewById(R.id.help_address);
        lang=findViewById(R.id.help_address1);
        user_name=findViewById(R.id.help_name);
        user_phone=findViewById(R.id.help_phone);
        user_type=findViewById(R.id.help_type_message);
        user_message=findViewById(R.id.help_message);
        pressed=findViewById(R.id.pressed);
        relativeLayout=findViewById(R.id.mainactivity);
        progressDialog=new ProgressDialog(this);
        pressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isfileEmpty(user_name)&&isfileEmpty(user_phone)&&isfileEmpty(user_message)&&isfileEmpty(user_type)
                &&istext(lat)&&istext(lang)) {


                    PostRegistrartion();
                }
            }
        });
        permissionsToRequest = findUnAskedPermissions(permissions);


        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(help_and_support.this);


                if (locationTrack.canGetLocation()) {


                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    result=String.valueOf(latitude);
                    result1=String.valueOf(longitude);
                    lat.setText(result);
                    lang.setText(result1);
                    if (longitude==0.0 && latitude==0.0)
                    {
                        startActivity(new Intent(help_and_support.this, MapsActivity.class));
                        lat.setText(String.valueOf(MapsActivity.latitude));
                        lang.setText(String.valueOf(MapsActivity.longitude));
                    }

                } else {

                    locationTrack.showSettingsAlert();
                }

            }
        });

    }

    // check connection


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

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    }
                                });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(help_and_support.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            locationTrack.stopListener();
        } catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }



    // uploading

    private void PostRegistrartion() {

        progressDialog.setTitle("Help Post");
        progressDialog.setMessage("Please wait don't panic we will be there soon...?");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final String fullnamevalue=user_name.getText().toString();
        final String phonevalue=user_phone.getText().toString();
        final String message_value=user_message.getText().toString();
        final String latitude_value=lat.getText().toString();
        final String longitudevalue=lang.getText().toString();
             final String help_type_value=user_type.getText().toString();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String Success = jsonObject.getString("success");
                        if (Success.equals("1")) {
                            Toast.makeText(help_and_support.this,"Your Help Post Register successfully \n" +
                                "help team arrive soon ...",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(help_and_support.this, help_and_support.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(help_and_support.this,"Error!"+e,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(help_and_support.this,"Error!"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            } 
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("name",fullnamevalue);
                params.put("phone",phonevalue);
                params.put("help_message",message_value);
                params.put("latitude",latitude_value);
                params.put("longitude",longitudevalue);
                params.put("help_type",help_type_value);

                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //For validation
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
    }

