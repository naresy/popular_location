package com.lmaniya.loocall.login.profile.room;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.login.helper.DatabaseHandler;


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

public class fetch_room_uploaded extends AppCompatActivity {


    String FinalHttpData = "";
    BufferedWriter bufferedWriter ;
    WebCallParse webCallParse = new WebCallParse();
    BufferedReader bufferedReader ;
    OutputStream outputStream ;
    StringBuilder stringBuilder = new StringBuilder();
    String Result ;
    GridView Subcategory_list;
    ProgressDialog pDialog;
    String HttpURL = "https://lmaniya.com.np/hunt_new/add_room/fetch_all_room_for_user.php";
    public static String ParseResult,Category_name ;
    HashMap<String,String> ResultHash = new HashMap<>();
    HashMap<String,String>user=new HashMap<>();
    DatabaseHandler databaseHandler;
    URL url;
    List<String> listString = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter ;
    String FinalJSonObject ,dist;


    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean internetConnected=true;
    ProgressDialog progressDialog;
    String userid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_room_uploaded);

        Subcategory_list = (GridView) findViewById(R.id.my_view);
        relativeLayout=findViewById(R.id.relative);
        swipeRefreshLayout=findViewById(R.id.swipe);
        progressDialog=new ProgressDialog(this);
        databaseHandler=new DatabaseHandler(getApplicationContext());
       user=databaseHandler.getUserDetails();
       userid=user.get("uid");
       setTitle("Uploaded Room");





        //Receiving the ListView Clicked item value send by previous activity.

        dist=getIntent().getStringExtra("distance");

        pDialog=new ProgressDialog(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnected())
                {
                    HttpWebCall(userid);
                }
                else {
                    Toast.makeText(fetch_room_uploaded.this,"Please Check Your Internet Connection..",Toast.LENGTH_SHORT).show();
                    try {
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (RuntimeException ex)
                    {
                        ex.printStackTrace();
                    }

                }

            }




        });





        HttpWebCall(userid);

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




//

    }





    // check connection
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




    public void HttpWebCall(final String PreviousListViewClickedItem ){

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
                new fetch_room_uploaded.GetHttpResponse(fetch_room_uploaded.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("user_id",params[0]);


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
        List<fetch_room_data> subcategory_data;

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
                        fetch_room_data subcategoryData ;
                        subcategory_data = new ArrayList<fetch_room_data>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            subcategoryData = new fetch_room_data();
                            jsonObject = jsonArray.getJSONObject(i);


                            subcategoryData.room_id=jsonObject.getString("room_id");
                            subcategoryData.owner_name=jsonObject.getString("owner_name");
                            subcategoryData.image1=jsonObject.getString("room_image1");
                            subcategoryData.owner_phone=jsonObject.getString("owner_phone");
                            subcategoryData.image2=jsonObject.getString("room_image2");
                            subcategoryData.room_latitude=jsonObject.getString("room_latitude");
                            subcategoryData.longitude=jsonObject.getString("room_longitude");
                            subcategoryData.description=jsonObject.getString("room_description");
                            subcategoryData.room_type=jsonObject.getString("room_type");
                            subcategoryData.room_address=jsonObject.getString("room_address");
                            subcategoryData.available=jsonObject.getString("sold_status");

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

            room_fetch_adapter adapter = new room_fetch_adapter(subcategory_data, context);
            Subcategory_list.setAdapter(adapter);

        }
    }


}

