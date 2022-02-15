package com.lmaniya.loocall.business_user_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.fetch_shop.fetch_product.customer.print_bill_data;
import com.lmaniya.loocall.fetch_shop.fetch_product.customer.print_data_adapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class see_bill_statement extends AppCompatActivity {

    EditText from_date;
    TextView pick_date,Current_date,load_data;
    final Calendar myCalendar = Calendar.getInstance();

    private SessionManager session;
    private DatabaseHandler db;
    private HashMap<String,String> user = new HashMap<>();
    TextView Customer_name,Customer_address,Customer_phone,Customer_email,print,date_time,bill_number;
    String FinalHttpData = "",usersid;
    BufferedWriter bufferedWriter ;
    WebCallParse webCallParse =new WebCallParse();
    BufferedReader bufferedReader ;
    OutputStream outputStream ;
    StringBuilder stringBuilder = new StringBuilder();
    String Result ;
    ListView Subcategory_list;
    ProgressDialog pDialog;
    String HttpURL = "https://lmaniya.com.np/hunt_new/see_order/statement_with_bill.php";
    String ParseResult ;
    HashMap<String,String> ResultHash = new HashMap<>();
    URL url;
    List<String> listString = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter ;
    String FinalJSonObject,shop_id ;
    WebView webView;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_bill_statement);

        from_date=findViewById(R.id.from_date);
        pick_date=findViewById(R.id.click_date);
        Current_date=findViewById(R.id.current);
        lottieAnimationView=findViewById(R.id.empty_cart);
        load_data=findViewById(R.id.load);
        setTitle("Bill statement");
        pDialog=new ProgressDialog(this);
        Subcategory_list=findViewById(R.id.see_bill);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        Current_date.setText(currentDateandTime);
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        shop_id=getIntent().getStringExtra("business");





        load_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfileEmpty(from_date)) {


                    final String date_pass=from_date.getText().toString();
                    HttpWebCall(date_pass,shop_id);
                    lottieAnimationView.setVisibility(View.INVISIBLE);
                }
                else {
                    Toast.makeText(see_bill_statement.this, "Date empty" , Toast.LENGTH_SHORT).show();

                }
            }
        });


        pick_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(see_bill_statement.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
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

        from_date.setText(sdf.format(myCalendar.getTime()));
    }



    public void HttpWebCall(final String PreviousListViewClickedItem ,String useridvalue){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = ProgressDialog.show(see_bill_statement.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String.
                new see_bill_statement.GetHttpResponse(see_bill_statement.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("Order_date",params[0]);
                ResultHash.put("userid",params[1]);

                ParseResult = webCallParse.postRequest(ResultHash);
                pDialog.dismiss();

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem,useridvalue);
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
        List<print_bill_data> subcategory_data;

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
                        print_bill_data subcategoryData ;
                        subcategory_data = new ArrayList<print_bill_data>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            subcategoryData = new print_bill_data();
                            jsonObject = jsonArray.getJSONObject(i);

                         
                            subcategoryData.Product_name=jsonObject.getString("product_name");
                            subcategoryData.Total_price=jsonObject.getInt("product_final_price");
                            subcategoryData.order_id=jsonObject.getString("Bill_number");
                            subcategoryData.Product_price=jsonObject.getString("product_price");
                            subcategoryData.quantity=jsonObject.getString("product_quantity");
                            subcategoryData.shop_name=jsonObject.getString("customer_name");
                            subcategory_data.add(subcategoryData);


                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        pDialog.dismiss();
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
            try {
                Subcategory_list.setVisibility(View.VISIBLE);
                for (int i = 0; i<subcategory_data.size(); i++)
                {


                }
            } catch(NullPointerException ex)
            {
                ex.printStackTrace();
                lottieAnimationView.setVisibility(View.VISIBLE);
                Toast.makeText(see_bill_statement.this,"No data found",Toast.LENGTH_SHORT).show();
            }


            print_data_adapter adapter = new print_data_adapter(subcategory_data, context);
            Subcategory_list.setAdapter(adapter);




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

