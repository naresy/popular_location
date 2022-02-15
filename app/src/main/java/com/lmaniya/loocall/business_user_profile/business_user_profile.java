package com.lmaniya.loocall.business_user_profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.business_user_profile.order.order_by_user_activity;
import com.lmaniya.loocall.business_user_profile.previous.see_user_previous_cart;
import com.lmaniya.loocall.login.MyApplication;
import com.lmaniya.loocall.login.helper.DatabaseHandler;
import com.lmaniya.loocall.login.helper.Functions;
import com.lmaniya.loocall.login.helper.SessionManager;
import com.lmaniya.loocall.login.login_activity;
import com.lmaniya.loocall.login.profile.user_profile_activity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class business_user_profile extends AppCompatActivity {
    LinearLayout add_shop,my_shop,my_product;
    private static final String TAG = user_profile_activity.class.getSimpleName();
    private SessionManager session;
    TextView fullname,notofication;
    private DatabaseHandler db;
    private HashMap<String,String> user = new HashMap<>();
    ProgressDialog pDialog;
    static  String usersid,business_id,shop_id;
    LinearLayout password_change,statement,previous_data,My_order,active_notification,logout,change_password,mu_profile;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    android.app.AlertDialog.Builder builder;
    ProgressDialog progressDialog;


    HashMap<String, String> ResultHash = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String FinalJSonObject,ParseResult,fetching_url="https://lmaniya.com.np/hunt_new/business_profile/fetch_business.php";

    String image_url="https://lmaniya.com.np/hunt_new/product/upload_user_id.php";

    //upload user_id


    // fetch_image

    String fet_image,new_url="https://lmaniya.com.np/hunt_new/product/fetch_my_information.php",name;
    CircleImageView imageView;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_user_profile);
        add_shop = findViewById(R.id.add_shop);

        mu_profile=findViewById(R.id.profile);
        builder = new android.app.AlertDialog.Builder(this);
        password_change = findViewById(R.id.card_change);
        logout = findViewById(R.id.logout);
        relativeLayout = findViewById(R.id.user_profile_layout);
        my_product=findViewById(R.id.myproduct);
        setTitle("My profile");
        imageView=findViewById(R.id.user_profile_image);
        My_order=findViewById(R.id.see_order);
        statement=findViewById(R.id.statement);
        previous_data=findViewById(R.id.past_order_view);
        previous_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(business_user_profile.this, see_user_previous_cart.class);
                intent.putExtra("id",usersid);
                intent.putExtra("business",shop_id);
                startActivity(intent);

            }
        });





        progressDialog = new ProgressDialog(this);
        init();

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        usersid=user.get("uid");
        name=user.get("name");
        mu_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(business_user_profile.this,business_own_profile.class);
                intent.putExtra("name",name);
                intent.putExtra("userid",usersid);
                startActivity(intent);
            }
        });

        my_shop=findViewById(R.id.my_shop);
        my_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(business_user_profile.this,show_shop_activity.class);
                intent.putExtra("id",usersid);
                startActivity(intent);

            }
        });
        add_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(business_user_profile.this,add_shop_activity.class);
                intent.putExtra("id",usersid);
                intent.putExtra("business",business_id);
                startActivity(intent);
            }
        });
        my_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(business_user_profile.this,my_product.class);
                intent.putExtra("id",usersid);
                startActivity(intent);

            }
        });
        My_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(business_user_profile.this, order_by_user_activity.class);
                intent.putExtra("id",usersid);
                intent.putExtra("business",business_id);
                intent.putExtra("shop_id",shop_id);
                startActivity(intent);
            }
        });
        statement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(business_user_profile.this,see_bill_statement.class);
                intent.putExtra("id",usersid);
                intent.putExtra("business",shop_id);
                startActivity(intent);

            }
        });


        // session manager
        session = new SessionManager(getApplicationContext());
        HttpWebCall(usersid);
        upload_user_id();
        HttpWebCall1(usersid);

        if (!session.isLoggedIn()) {


        }
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


        private void init() {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setMessage("Do you want to logout..?")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                logoutUser();
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
            password_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(business_user_profile.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.change_password, null);

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setTitle("Change Password");
                    dialogBuilder.setCancelable(false);

                    final TextInputLayout oldPassword = dialogView.findViewById(R.id.old_password);
                    final TextInputLayout newPassword = dialogView.findViewById(R.id.new_password);

                    dialogBuilder.setPositiveButton("Reset",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // empty
                        }
                    });

                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog alertDialog = dialogBuilder.create();

                    TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(oldPassword.getEditText().getText().length() > 0 &&
                                newPassword.getEditText().getText().length() > 0){
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            } else {
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    };

                    oldPassword.getEditText().addTextChangedListener(textWatcher);
                    newPassword.getEditText().addTextChangedListener(textWatcher);

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            b.setEnabled(false);

                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String email = user.get("email");
                                    String old_pass = oldPassword.getEditText().getText().toString();
                                    String new_pass = newPassword.getEditText().getText().toString();

                                    if (!old_pass.isEmpty() && !new_pass.isEmpty()) {
                                        if( new_pass.length()<6)
                                        {
                                            newPassword.setError("Eneter 6 digit Password");
                                        }
                                        else {
                                            changePassword(email, old_pass, new_pass);
                                            dialog.dismiss();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Fill all values!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    });

                    alertDialog.show();
                }
            });


        }

        private void logoutUser() {
            session.setLogin(false,5);
            // Launching the login activity
            Functions logout = new Functions();
            logout.logoutUser(getApplicationContext());
            Intent intent = new Intent(business_user_profile.this, login_activity.class);
            startActivity(intent);
            finish();
        }
        private void changePassword(final String email, final String old_pass, final String new_pass) {
            // Tag used to cancel the request
            String tag_string_req = "req_reset_pass";

            pDialog.setMessage("Please wait...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.RESET_PASS_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Reset Password Response: " + response);
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        if (!error) {
                            Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Reset Password Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();

                    params.put("tag", "change_pass");
                    params.put("email", email);
                    params.put("old_password", old_pass);
                    params.put("password", new_pass);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }

            };

            // Adding request to volley request queue
            strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
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




        ///for business_id

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
                progressDialog.dismiss();


                super.onPostExecute(httpResponseMsg);




                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new business_user_profile.GetHttpResponse(business_user_profile.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("selection", params[0]);

                ParseResult = httpParse.postRequest(ResultHash, fetching_url);
                progressDialog.dismiss();

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
                            business_id = jsonObject.getString("business_id").toString();
                            shop_id=jsonObject.getString("shop_id");




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
            progressDialog.dismiss();




        }
    }


    // upload user id

    private void upload_user_id() {





        StringRequest stringRequest=new StringRequest(Request.Method.POST, image_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(business_user_profile.this,"Error check the connection",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();

                params.put("user_id",usersid);

                return params;

            }

        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }

    //for image


    private void HttpWebCall1(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = ProgressDialog.show(business_user_profile.this,"Loading Data",null,true,true);
                pDialog.setCanceledOnTouchOutside(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {


                super.onPostExecute(httpResponseMsg);


                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new business_user_profile.GetHttpResponse1(business_user_profile.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {
                ResultHash.put("user_id",params[0]);
                ParseResult = httpParse.postRequest(ResultHash, new_url);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    // Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponse1 extends AsyncTask<Void, Void, Void> {
        public Context context;

        public GetHttpResponse1(Context context) {
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

                            fet_image = jsonObject.getString("image");


                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            try {
                Picasso.get().load(fet_image).into(imageView);
            } catch (IllegalArgumentException ex) {

            }


        }
    }







    }
