package com.lmaniya.loocall.fetch_shop.fetch_product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.Main_page_activity;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.fetch_shop.all_shop_activity;
import com.lmaniya.loocall.fetch_shop.fetch_product.customer.my_cart_activity;
import com.lmaniya.loocall.login.helper.DatabaseHandler;
import com.lmaniya.loocall.login.helper.SessionManager;
import com.lmaniya.loocall.login.login_activity;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class add_cart_activity extends AppCompatActivity {

    TextView product_name, product_price, discount_per,attribute,unit,add_cart;
    ImageView product_image;
    HttpParse httpParse = new HttpParse();
    String ParseResult,category_value,wt_value,unit_value;
    String FinalJSonObject,Category_type;
    EditText content;
    //fetching string
    String name_value,price_value,image_value,discount,initial_price,product_id,business_id;
    Button increase, drecrease;
    static String currentDateandTime;
    private HashMap<String, String> user = new HashMap<>();
    public static String usersid, final_price;
    private DatabaseHandler db;
    SessionManager sessionManager;
    public static String url = "https://lmaniya.com.np/hunt_new//product/add_cart.php";

    public static int integer = 0;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    ProgressDialog progressDialog;
    android.app.AlertDialog.Builder builder;
    double discount_value,start_value,end_value;







    String FinalHttpData = "";
    BufferedWriter bufferedWriter ;
    WebCallParse webCallParse = new WebCallParse();
    BufferedReader bufferedReader ;
    OutputStream outputStream ;
    StringBuilder stringBuilder = new StringBuilder();
    String Result ;
    GridView Subcategory_list;
    ProgressDialog pDialog;
    String HttpURL = "https://huntsewa.com/hunt_new/main_section/fetch_all_product.php";

    HashMap<String,String> ResultHash = new HashMap<>();
    URL url1;
    List<String> listString = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter ;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences1;
    String role_id;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cart_activity);

        progressDialog = new ProgressDialog(this);
        product_price = findViewById(R.id.product_price);
        product_name = findViewById(R.id.product_title);
        relativeLayout=findViewById(R.id.addcart_layout);
        product_image = findViewById(R.id.product_image);
        discount_per = findViewById(R.id.product_discount);
        attribute=findViewById(R.id.product_attribute);
        unit=findViewById(R.id.unit);
        increase = findViewById(R.id.quantity_plus);
        drecrease = findViewById(R.id.quantity_minus);
        content=findViewById(R.id.quantity);
        price_value=getIntent().getStringExtra("final_price");
        image_value=getIntent().getStringExtra("image");
        name_value=getIntent().getStringExtra("name");
        initial_price=getIntent().getStringExtra("discount");
        business_id=getIntent().getStringExtra("business_id");
         end_value=Double.parseDouble(price_value);
         start_value=Double.parseDouble(initial_price);
         discount_value=((start_value-end_value)/start_value)*100;
         int dis=(int)discount_value;
         discount_per.setText((dis+"%"));
        product_name.setText(name_value);
        product_price.setText(price_value);
        product_name.setText(name_value);
        Picasso.get().load(image_value).into(product_image);
        add_cart=findViewById(R.id.add_to_cart);
        product_id=getIntent().getStringExtra("ListViewValue");
        builder=new AlertDialog.Builder(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        currentDateandTime = sdf.format(new Date());
        sharedPreferences=getSharedPreferences("shop",0);
        final String id_value=sharedPreferences.getString("shop_id","");
        setTitle("Add Cart");

        sessionManager = new SessionManager(getApplicationContext());

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        usersid = user.get("uid");
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase_method();
            }
        });
        drecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease_method();
            }
        });
        add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_information();

            }
        });


        // for suggestion product
        Subcategory_list = (GridView) findViewById(R.id.listview1);
        swipeRefreshLayout=findViewById(R.id.swipe);

        final String TempItem = getIntent().getStringExtra("ListViewValue");

        pDialog=new ProgressDialog(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnected())
                {
                    HttpWebCall(id_value);
                }
                else {
                    Toast.makeText(add_cart_activity.this,"Please Check Your Internet Connection..",Toast.LENGTH_SHORT).show();
                    try {
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (RuntimeException ex)
                    {
                        ex.printStackTrace();
                    }

                }

            }
        });


        HttpWebCall(id_value);

        Subcategory_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (0==firstVisibleItem)
                { try {
                    swipeRefreshLayout.setEnabled(true);
                } catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }

                }
                else {
                    try {
                        swipeRefreshLayout.setEnabled(false);
                    } catch (RuntimeException ex)
                    {
                        ex.printStackTrace();
                    }

                }

            }
        });
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






    private void upload_information() {
        {
            if (usersid == null) {
                Toast.makeText(add_cart_activity.this, "Please Login first", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(add_cart_activity.this, login_activity.class));
            } else {


                if (sessionManager.isroller()==0) {


                    progressDialog.setTitle("Request adding on list");
                    progressDialog.setMessage("Please wait...!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    final String quantity_value = content.getText().toString();

                    final double content = Double.parseDouble(quantity_value);
                    final double price_content = Double.parseDouble(price_value);
                    final double total = content * price_content;
                    final String total_value = Double.toString(total);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String Success = jsonObject.getString("success");
                                        if (Success.equals("1")) {
                                            Toast.makeText(add_cart_activity.this, "your oder on cart ...", Toast.LENGTH_LONG).show();
                                            builder.setMessage("Do you want shop more/के तपाई बढि किनमेल गर्न चाहानुहुन्छ?");
                                            builder.setCancelable(false)
                                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            startActivity(new Intent(add_cart_activity.this, Main_page_activity.class));

                                                        }
                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Intent intent = new Intent(add_cart_activity.this, my_cart_activity.class);
                                                    intent.putExtra("Category_type", Category_type);
                                                    startActivity(intent);
                                                }
                                            });
                                            builder.show();

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(add_cart_activity.this, "Error!" + response, Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(add_cart_activity.this, "Error!" + error, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", usersid);
                            params.put("product_id", product_id);
                            params.put("product_name", name_value);
                            params.put("product_image", image_value);
                            params.put("product_price", price_value);
                            params.put("product_quantity", quantity_value);
                            params.put("product_final_price", total_value);
                            params.put("cart_date", currentDateandTime);
                            params.put("business_id", business_id);


                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);


                }
                else {
                    Toast.makeText(add_cart_activity.this,"You dont have permission",Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    private void decrease_method() {
        if (integer>0)
        {
            display(integer);
            integer = integer - 1;
        }


    }

    private void display(int integer) {
        content.setText("" + integer);

    }

    private void increase_method() {
        integer = integer + 1;
        display(integer);
    }


    // for suggestion

    public void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                try {
                    swipeRefreshLayout.setRefreshing(true);
                } catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }

                progressDialog.setTitle("Loading...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                try {
                    swipeRefreshLayout.setRefreshing(false);
                } catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String.
                new add_cart_activity.GetHttpResponse(add_cart_activity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("shop_id",params[0]);

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
                url1 = new URL(HttpURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();

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
                progressDialog.dismiss();
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
        List<fetch_product_date> subcategory_data;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
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
                        fetch_product_date subcategoryData ;
                        subcategory_data = new ArrayList<fetch_product_date>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            subcategoryData = new fetch_product_date();
                            jsonObject = jsonArray.getJSONObject(i);


                            subcategoryData.product_id=jsonObject.getString("product_id");
                            subcategoryData.Product_name=jsonObject.getString("product_name");
                            subcategoryData.product_image=jsonObject.getString("product_image");
                            subcategoryData.product_start_price=jsonObject.getString("product_initial_price");
                            subcategoryData.product_final_price=jsonObject.getString("product_final_price");
                            subcategoryData.product_description=jsonObject.getString("product_description");




                            subcategory_data.add(subcategoryData);


                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        progressDialog.dismiss();
                        try {
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (RuntimeException ex)
                        {
                            ex.printStackTrace();
                        }

                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

                progressDialog.dismiss();
                try {
                    swipeRefreshLayout.setRefreshing(false);
                } catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)

        {
            progressDialog.dismiss();
            try {
                swipeRefreshLayout.setRefreshing(false);
            } catch (RuntimeException ex)
            {
                ex.printStackTrace();
            }

            Subcategory_list.setVisibility(View.VISIBLE);

            fetch_product_adapter adapter = new fetch_product_adapter(subcategory_data, context);
            Subcategory_list.setAdapter(adapter);

        }
    }


}

