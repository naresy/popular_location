package com.lmaniya.loocall.business_user_profile.print;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.lmaniya.loocall.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class print_bill_for_admin extends AppCompatActivity {


    private HashMap<String,String> user = new HashMap<>();
    TextView Customer_name,Customer_address,Customer_phone,Customer_email,print,date_time,bill_number,total_price;
    String FinalHttpData = "";
    BufferedWriter bufferedWriter ;
    WebCallParse webCallParse =new WebCallParse();
    BufferedReader bufferedReader ;
    OutputStream outputStream ;
    StringBuilder stringBuilder = new StringBuilder();
    String Result ;
    ListView Subcategory_list;
    ProgressDialog pDialog;
    String HttpURL = "https://huntsewa.com/hunt_new/see_order/print_bill_for_admin.php";
    String ParseResult ;
    HashMap<String,String> ResultHash = new HashMap<>();
    URL url;
    List<String> listString = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter ;
    String FinalJSonObject ;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imageView;
    public  static  String name,email,total_amount,phone,address,e_id,bill_n;
    SharedPreferences sharedPreferences;
    TextView net_totel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_bill_for_admin);



        Customer_name=findViewById(R.id.customer_name);
        Customer_phone=findViewById(R.id.customer_phone);
        Customer_email=findViewById(R.id.email);
        relativeLayout=findViewById(R.id.click);
        swipeRefreshLayout=findViewById(R.id.swipe);
        imageView=findViewById(R.id.barcode);



        Subcategory_list=findViewById(R.id.my_list);
        date_time=findViewById(R.id.Date);
        bill_number=findViewById(R.id.Bill_number);
        print=findViewById(R.id.print);
        net_totel=findViewById(R.id.Net_payment);
        pDialog=new ProgressDialog(this);


        total_price=findViewById(R.id.total_view);
        pDialog=new ProgressDialog(this);

        sharedPreferences=getSharedPreferences("checkout_information",0);
        name=sharedPreferences.getString("c_name","");
        email=sharedPreferences.getString("c_email","");
        phone=sharedPreferences.getString("c_phone","");
        bill_n=sharedPreferences.getString("b_number","");
        e_id=sharedPreferences.getString("c_id","");
        Customer_name.setText(name);
        Customer_phone.setText(phone);
        bill_number.setText(bill_n);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpWebCall(bill_n,e_id);
            }
        });
        HttpWebCall(bill_n,e_id);



        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        final String currentDateandTime = sdf.format(new Date());
        date_time.setText(currentDateandTime);





        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
        else {

        }

        generate_qr();




        print.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                printPDF();
            }

        });

    }











    private void generate_qr() {
        ; // Whatever you need to encode in the QR code
        StringBuilder textToSend = new StringBuilder();
        textToSend.append("Phone number  "+phone+" | "+"Bill Number  "+bill_n+" | "+"Total amount  "+total_amount);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToSend.toString(), BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    ///

    public void printPDF() {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("print_any_view_job_name", new print_bill_for_admin.ViewPrintAdapter(this,
                findViewById(R.id.click)), null);
    }

    public class ViewPrintAdapter extends PrintDocumentAdapter {

        private PrintedPdfDocument mDocument;
        private Context mContext;
        private View mView;

        public ViewPrintAdapter(Context context, View view) {
            mContext = context;
            mView = view;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback, Bundle extras) {

            mDocument = new PrintedPdfDocument(mContext, newAttributes);

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);

        }




        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal,
                            WriteResultCallback callback) {

            // Start the page
            PdfDocument.Page page = mDocument.startPage(0);
            // Create a bitmap and put it a canvas for the view to draw to. Make it the size of the view
            Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mView.draw(canvas);
            // create a Rect with the view's dimensions.
            Rect src = new Rect(0, 0, mView.getWidth(), mView.getHeight());
            // get the page canvas and measure it.
            Canvas pageCanvas = page.getCanvas();
            float pageWidth = pageCanvas.getWidth();
            float pageHeight = pageCanvas.getHeight();
            // how can we fit the Rect src onto this page while maintaining aspect ratio?
            float scale = Math.min(pageWidth/src.width(), pageHeight/src.height());
            float left = pageWidth / 2 - src.width() * scale / 2;
            float top = pageHeight / 2 - src.height() * scale / 2;
            float right = pageWidth / 2 + src.width() * scale / 2;
            float bottom = pageHeight / 2 + src.height() * scale / 2;
            RectF dst = new RectF(left, top, right, bottom);

            pageCanvas.drawBitmap(bitmap, src, dst, null);
            mDocument.finishPage(page);

            try {
                mDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                mDocument.close();
                mDocument = null;
            }
            callback.onWriteFinished(new PageRange[]{new PageRange(0, 0)});
        }
    }
    ///

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==100)
        {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Permission not Granted",Toast.LENGTH_SHORT).show();
                finish();

            }
        }
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

    public void HttpWebCall(String bill_n,String c_id){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog.setTitle("Loading.....");
                pDialog.setCanceledOnTouchOutside(false);

                pDialog.show();
                try {
                    swipeRefreshLayout.setRefreshing(true);
                } catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }


            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pDialog.dismiss();
                try {
                    swipeRefreshLayout.setRefreshing(false);
                } catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String.
                new print_bill_for_admin.GetHttpResponse(print_bill_for_admin.this).execute();

            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {


                ResultHash.put("Bill_number",params[0]);
                ResultHash.put("customer_id",params[1]);
                ParseResult = webCallParse.postRequest(ResultHash);
                pDialog.dismiss();
                try {
                    swipeRefreshLayout.setRefreshing(false);
                } catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(bill_n,c_id);
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
                        print_bill_data subcategoryData ;
                        subcategory_data = new ArrayList<print_bill_data>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            subcategoryData = new print_bill_data();
                            jsonObject = jsonArray.getJSONObject(i);

                            listString.add(jsonObject.getString("cart_id").toString()) ;
                            subcategoryData.Product_name=jsonObject.getString("product_name");
                            subcategoryData.Total_price=jsonObject.getInt("product_final_price");
                            subcategoryData.order_id=jsonObject.getString("cart_id");
                            subcategoryData.Product_price=jsonObject.getString("product_price");
                            subcategoryData.quantity=jsonObject.getString("product_quantity");
                            subcategoryData.sku=jsonObject.getString("sku");

                            subcategory_data.add(subcategoryData);


                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        pDialog.dismiss();
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
                pDialog.dismiss();
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
            int totalPrice = 0;
            int net=0;
            try {


                for (int i = 0; i<subcategory_data.size(); i++)
                {
                    totalPrice += subcategory_data.get(i).Total_price();
                    total_price.setText(String.valueOf(totalPrice));
                    net=totalPrice+totalPrice*13/100;
                    net_totel.setText(String.valueOf(net));


                }
            } catch (NullPointerException ex)
            {

                total_price.setText(String.valueOf(0));

                ex.printStackTrace();



            }

            Subcategory_list.setVisibility(View.VISIBLE);

            print_data_adapter adapter = new print_data_adapter(subcategory_data, context);
            Subcategory_list.setAdapter(adapter);

        }
    }

}



