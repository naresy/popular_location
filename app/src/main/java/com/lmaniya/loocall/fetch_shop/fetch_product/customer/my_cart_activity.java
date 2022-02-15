package com.lmaniya.loocall.fetch_shop.fetch_product.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.BottomNavigationBehavior;
import com.lmaniya.loocall.Main_page_activity;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.login.helper.DatabaseHandler;
import com.lmaniya.loocall.login.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class my_cart_activity extends AppCompatActivity {


    public static TextView total_price,shope_more,chech_out;
    String FinalHttpData = "";
    BufferedWriter bufferedWriter ;
    WebCallParse webCallParse = new WebCallParse();
    BufferedReader bufferedReader ;
    OutputStream outputStream ;
    StringBuilder stringBuilder = new StringBuilder();
    String Result ;
    ListView Subcategory_list;
    ProgressDialog pDialog;
    String HttpURL = "https://lmaniya.com.np/hunt_new/product/user_cart_view.php";
    String ParseResult ;
    HashMap<String,String> ResultHash = new HashMap<>();
    URL url;
    List<String> listString = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter ;
    private DatabaseHandler db;
    private SessionManager session;
    private HashMap<String,String> user = new HashMap<>();
    static String FinalJSonObject,Category_type;
    int final_total_price=0;
    public static  String usersid;
    FloatingActionButton fab;
    public user_cart_adapter adapter;
    BottomNavigationBehavior bottomNavigationBehavior;
    SwipeRefreshLayout swipeRefreshLayout;


    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    LottieAnimationView lottieAnimationView;
    private boolean internetConnected=true;
    SharedPreferences sharedPreferences;
    LinearLayout shore_more_click,checkout_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart_activity);


        swipeRefreshLayout=findViewById(R.id.refresh);
        relativeLayout=findViewById(R.id.user_cart_layout);
        Subcategory_list = (ListView)findViewById(R.id.listview1);
        //String TempItem = getIntent().getStringExtra("user_id");
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        setTitle("My cart");
        lottieAnimationView =findViewById(R.id.empty_cart);
        Category_type=getIntent().getStringExtra("Category_type");
        total_price=findViewById(R.id.total_price);
        shope_more=findViewById(R.id.shop_more);
        chech_out=findViewById(R.id.check_out);
        shore_more_click=findViewById(R.id.shop_more_click);
        checkout_click=findViewById(R.id.checkout_click);
        checkout_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(my_cart_activity.this, checkout_activity.class);
                startActivity(intent);
                finish();
            }
        });
        shore_more_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_cart_activity.this, Main_page_activity.class));
                finish();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final String usersid=user.get("uid");
        // Displaying the user details on the screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpWebCall(usersid);
            }
        });
        HttpWebCall(usersid);
        set_total_value();
        sharedPreferences =getSharedPreferences("Total_bill",0);

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

    private void set_total_value() {
        List<cart_data> cartData =new ArrayList<>();
        int total = 0;
        for(int i = 0; i < cartData.size(); i++){
            total = total + cartData.get(i).Total_price();
        }



    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_checkout:

                    return true;
                case R.id.navigation_shop_more:

                    return true;
                case R.id.navigation_update:

                    return true;

            }
            return false;
        }
    };

    public void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.setRefreshing(true);
                pDialog = ProgressDialog.show(my_cart_activity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                swipeRefreshLayout.setRefreshing(false);

                pDialog.dismiss();


                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String.
                new my_cart_activity.GetHttpResponse(my_cart_activity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("user_id",params[0]);
                swipeRefreshLayout.setRefreshing(false);

                ParseResult = webCallParse.postRequest(ResultHash);



                return ParseResult;

            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }

    public class WebCallParse {

        public String postRequest(HashMap<String, String> Data) {

            try {
                url = new URL(HttpURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(12000);

                httpURLConnection.setConnectTimeout(12000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(FinalDataParse(Data));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                if (httpURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(
                            new InputStreamReader(
                                    httpURLConnection.getInputStream()
                            )
                    );
                    FinalHttpData = bufferedReader.readLine();
                }
                else {
                    FinalHttpData = "Something Went Wrong";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return FinalHttpData;
        }

        public String FinalDataParse(HashMap<String, String> hashMap2) throws UnsupportedEncodingException {

            for(Map.Entry<String, String> map_entry : hashMap2.entrySet()){

                stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));

            }

            Result = stringBuilder.toString();

            return Result ;
        }
    }

    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        List<cart_data> cartData;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null)
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;
                        cart_data cartData1 ;
                        cartData = new ArrayList<cart_data>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            cartData1 = new cart_data();
                            jsonObject = jsonArray.getJSONObject(i);


                            cartData1.Product_title=jsonObject.getString("product_name");
                            cartData1.product_price=jsonObject.getString("product_price");
                            cartData1.product_quantity=jsonObject.getString("product_quantity");
                            cartData1.image=jsonObject.getString("product_image");
                            cartData1.Total_price=jsonObject.getInt("product_final_price");
                            cartData1.cart_id=jsonObject.getString("cart_id");
                            cartData.add(cartData1);



                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        pDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                pDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            Subcategory_list.setVisibility(View.VISIBLE);
            int totalPrice = 0;
            int item=0;
            try {


                for (int i = 0; i<cartData.size(); i++)
                {
                    item++;
                    totalPrice += cartData.get(i).Total_price();
                    total_price.setText(String.valueOf(totalPrice));

                }
            } catch (NullPointerException ex)
            {
                totalPrice=0;
                total_price.setText(String.valueOf(totalPrice));
                chech_out.setVisibility(View.GONE);
                shope_more.setText("shop");
                ex.printStackTrace();
                lottieAnimationView.setVisibility(View.VISIBLE);
                Toast.makeText(my_cart_activity.this,"Empty Cart",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }


            adapter = new user_cart_adapter(cartData, context);


            Subcategory_list.setAdapter(adapter);

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("total",String.valueOf(totalPrice));
            editor.putString("item",String.valueOf(item));
            editor.apply();
        }


    }
    }
