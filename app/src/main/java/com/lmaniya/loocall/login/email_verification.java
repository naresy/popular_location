package com.lmaniya.loocall.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.lmaniya.loocall.Main_page_activity;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.business_user_profile.business_user_profile;
import com.lmaniya.loocall.login.helper.DatabaseHandler;
import com.lmaniya.loocall.login.helper.Functions;
import com.lmaniya.loocall.login.helper.SessionManager;
import com.lmaniya.loocall.login.profile.user_profile_activity;
import com.lmaniya.loocall.super_admin.super_admin_dash_board;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class email_verification extends AppCompatActivity {

    private static final String TAG = email_verification.class.getSimpleName();

    private TextInputLayout textVerifyCode;
    private TextView btnVerify, btnResend;
    private TextView otpCountDown;
    private SessionManager session;
    private DatabaseHandler db;
    private ProgressDialog pDialog;
    private static final String FORMAT = "%02d:%02d";
    Bundle bundle;
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";


    // check connection
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        setTitle("verification");

        textVerifyCode = findViewById(R.id.email_verification_code_layout);
        btnVerify = findViewById(R.id.email_verification);
        btnResend = findViewById(R.id.email_verification_code_resend);
        otpCountDown = findViewById(R.id.email_verification_time);
        bundle = getIntent().getExtras();
        db = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        relativeLayout=findViewById(R.id.email_verify_layout);


        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    // check connection



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





    private void init() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide Keyboard
                Functions.hideSoftKeyboard(email_verification.this);

                String email = bundle.getString("email");
                String otp = textVerifyCode.getEditText().getText().toString();
                if (!otp.isEmpty()) {
                    verifyCode(email, otp);
                    textVerifyCode.setErrorEnabled(false);
                } else {
                    textVerifyCode.setError("Please enter verification code");
                }
            }
        });

        btnResend.setEnabled(false);
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = bundle.getString("email");
                resendCode(email);
            }
        });

        countDown();
    }

    private void countDown() {
        new CountDownTimer(70000, 1000) { // adjust the milli seconds here

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                otpCountDown.setVisibility(View.VISIBLE);
                otpCountDown.setText(""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) ));
            }

            public void onFinish() {
                otpCountDown.setVisibility(View.GONE);
                btnResend.setEnabled(true);
            }
        }.start();
    }

    private void verifyCode(final String email, final String otp) {
        // Tag used to cancel the request
        String tag_string_req = "req_verify_code";

        pDialog.setMessage("Checking in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
            Functions.OTP_VERIFY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Verification Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        JSONObject json_user = jObj.getJSONObject("user");
                        if(Integer.parseInt(json_user.getString("role"))==1)
                        {
                            Functions logout = new Functions();
                            logout.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                            session.setLogin(true,1);
                            Intent upanel = new Intent(email_verification.this, business_user_profile.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            finish();
                        }
                        else if (Integer.parseInt(json_user.getString("role"))==2)
                        {
                            Functions logout = new Functions();
                            logout.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                            session.setLogin(true,2);
                            Intent upanel = new Intent(email_verification.this, business_user_profile.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            finish();
                        }
                        else if (Integer.parseInt(json_user.getString("role"))==3)
                        {
                            Functions logout = new Functions();
                            logout.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                            session.setLogin(true,3);
                            Intent upanel = new Intent(email_verification.this, super_admin_dash_board.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            finish();
                        }
                        else {
                            Functions logout = new Functions();
                            logout.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                            session.setLogin(true,0);
                            Intent upanel = new Intent(email_verification.this, Main_page_activity.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            finish();
                        }




                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Verification Code", Toast.LENGTH_LONG).show();
                        textVerifyCode.setError("Invalid Verification Code");
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Verify Code Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("tag", "verify_code");
                params.put("email", email);
                params.put("otp", otp);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void resendCode(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_resend_code";

        pDialog.setMessage("Resending code ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
            Functions.OTP_VERIFY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Resend Code Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Code successfully sent to your email!", Toast.LENGTH_LONG).show();
                        btnResend.setEnabled(false);
                        countDown();
                    } else {
                        Toast.makeText(getApplicationContext(), "Code sending failed!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Resend Code Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "resend_code");
                params.put("email", email);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onResume(){
        super.onResume();
        countDown();
        registerInternetCheckReceiver();
    }
}

