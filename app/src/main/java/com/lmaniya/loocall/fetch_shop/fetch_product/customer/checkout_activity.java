package com.lmaniya.loocall.fetch_shop.fetch_product.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.login.helper.DatabaseHandler;
import com.lmaniya.loocall.login.helper.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class checkout_activity extends AppCompatActivity {

    TextView fullname,email,no_item,total_amt;
    ImageView save;
    EditText phone,address;
    RadioGroup payment;
    private DatabaseHandler db;
    private SessionManager session;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    static String check_out="checkout",name,email_value,usersid,phone_value,address_value,payment_value;
    static String post_url="https://lmaniya.com.np/hunt_new/product/customer_checkout.php";
    String finalResult,order_status="Pending";
    ProgressDialog progressDialog;
    private HashMap<String,String> user = new HashMap<>();


    // check connection
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences1;
    public static String Billl_number;
    //get default email
    TelephonyManager telephonyManager;
    private static final int PERMISSION_REQUEST_CODE = 100;
    SharedPreferences sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_activity);

        fullname=findViewById(R.id.checkout_fullname);
        email=findViewById(R.id.checkout_email);
        no_item=findViewById(R.id.no_of_items);
        total_amt=findViewById(R.id.total_amount);
        save=findViewById(R.id.checkout);
        phone=findViewById(R.id.checkout_phone);
        progressDialog=new ProgressDialog(this);
        address=findViewById(R.id.checkout_address);
        payment=findViewById(R.id.payment);
        relativeLayout=findViewById(R.id.check_layout);
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        session = new SessionManager(getApplicationContext());
        //user get information
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        name = user.get("name");
        email_value = user.get("email");
        usersid=user.get("uid");
        sharedPreferences=getSharedPreferences("Billing",0);
        sharedPreferences1=getSharedPreferences("Bill_number",0);
        Billl_number=sharedPreferences1.getString("bill_number","");
        sharedPreferences2=getSharedPreferences("Total_bill",0);
        String i_n=sharedPreferences2.getString("item","");
        String t_m=sharedPreferences2.getString("total","");
        no_item.setText(i_n);
        setTitle("Checkout ");
        total_amt.setText(t_m);



        // Displaying the user details on the screen
        fullname.setText(name);
        email.setText(email_value);


        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        final String currentDateandTime = sdf.format(new Date());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfileEmpty(phone)&& isfileEmpty(address)) {
                    if (phone.length()>=10) {


                        upload_information();


                        Update_cart(usersid, name, email_value, phone_value, address_value, check_out,payment_value,order_status,currentDateandTime,Billl_number);
                    }

                }
            }
        });

        //get default phone number




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






    private void Update_cart(String User_id, String user_name, String user_email,String user_phone,String user_address,String check_status,String Payment,String order_status,String date,String b_n) {
        class menuUpdateClass extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setTitle("Loading.....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Toast.makeText(checkout_activity.this,s.toString(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(checkout_activity.this, print_bill_activity.class));
                finish();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("customer_id",params[0]);
                hashMap.put("Customer_name",params[1]);
                hashMap.put("Customer_email",params[2]);
                hashMap.put("Customer_phone",params[3]);
                hashMap.put("Customer_address",params[4]);
                hashMap.put("Checkout_status",params[5]);
                hashMap.put("Payment_status",params[6]);
                hashMap.put("Order_status",params[7]);
                hashMap.put("Checkout_date",params[8]);
                hashMap.put("Bill_number",params[9]);
                finalResult=httpParse.postRequest(hashMap,post_url);
                return finalResult;
            }
        }
        menuUpdateClass menuUpdateClass=new menuUpdateClass();
        menuUpdateClass.execute(User_id,user_name,user_email,user_phone,user_address,check_status,Payment,order_status,date,b_n);


    }

    private void upload_information() {

        phone_value = phone.getText().toString();
        address_value = address.getText().toString();
        RadioButton bank=findViewById(payment.getCheckedRadioButtonId());
        payment_value=bank.getText().toString();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("phone",phone_value);
        editor.putString("address",address_value);
        editor.putString("payment",payment_value);
        editor.apply();
        String bill=   sharedPreferences1.getString("bill_number","");
        if (bill.equals(""))
        {

            String number_value="100";
            SharedPreferences.Editor editor1=sharedPreferences1.edit();
            editor1.putString("bill_number",number_value);
            editor1.apply();

        }






    }
    public static boolean isfileEmpty(EditText view) {
        String value = view.getText().toString();
        if (value.length() > 0) {
            return true;
        } else
            view.setError("enter value");
        return false;
    }

}