package com.lmaniya.loocall.login.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.business_user_profile.business_user_profile;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class detail_profile_activity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    TextView full_name;
  public   EditText Date_of_birth;
    public static EditText age;
    public EditText phone;
    public EditText village;
    public EditText address1;
    public EditText address2;
    public EditText postal_code;
    public EditText relation,work;
    String name_value;
    RadioGroup radioGroup;
    ImageView imageView;
    String[] Didtrict = {"Achham",
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
    String[] state_value_string = {"1", "2", "3", "4", "5", "6", "7"};
    String[] economic_value_string = {"high", "middle", "low"};
    String[] education = {"SEE", "HSEB", "BE", "ME", "PHD"};
    String[] occupation = {"student", "officer"};
    Spinner country_value, state_value, economic_value, occupation_value, education_value;
    String county_get, state_get, echonomic_get, education_get, occupation_get,userid,postURL="https://lmaniya.com.np/hunt_new/product/user_information.php";
    FloatingActionButton update;
    static  long difference_In_Days;
    final Calendar myCalendar = Calendar.getInstance();
    Bitmap bitmap;
    ProgressDialog progressDialog ;

    //fetching
    String ParseResult,FinalJSonObject,fetching_url="https://lmaniya.com.np/hunt_new/product/fetch_my_information.php";
    HashMap<String,String> ResultHash = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    ProgressDialog pDialog;
    String fet_image,fet_date,fet_phone,fet_village,fet_address1,fet_address2,fet_postal,fet_relation,fet_work;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile_activity);
        full_name = findViewById(R.id.identification_first_name);
        name_value = getIntent().getStringExtra("name");
        userid=getIntent().getStringExtra("userid");
        full_name.setText(name_value);
        Date_of_birth = findViewById(R.id.identification_birth_date_text_view);
        phone = findViewById(R.id.identification_phone_number);
        village = findViewById(R.id.identification_city);
        address1 = findViewById(R.id.identification_address1);
        address2 = findViewById(R.id.identification_address2);
        postal_code = findViewById(R.id.identification_postal_code);
        relation = findViewById(R.id.identification_relationship);
        country_value = findViewById(R.id.spinner_country);
        state_value = findViewById(R.id.spinner_state);
        setTitle("User detail");
        economic_value = findViewById(R.id.spinner_economic_status);
        occupation_value = findViewById(R.id.spinner_caste);
        education_value = findViewById(R.id.spinner_education);
        radioGroup = findViewById(R.id.radioGrp);
        work=findViewById(R.id.identification_occupation);

        imageView=findViewById(R.id.imageview_id_picture);
        progressDialog=new ProgressDialog(this);
        pDialog=new ProgressDialog(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cooseimagefromfile();
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, economic_value_string);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        economic_value.setAdapter(adapter);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, state_value_string);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        state_value.setAdapter(arrayAdapter);

        ArrayAdapter aaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Didtrict);
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country_value.setAdapter(aaa);
        ArrayAdapter aaaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, occupation);
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupation_value.setAdapter(aaaa);
        ArrayAdapter aaaaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, education);
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        education_value.setAdapter(aaaaa);
        update = findViewById(R.id.fab);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDateandTime = sdf.format(new Date());
       String date2=Date_of_birth.getText().toString();

        Date_of_birth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(detail_profile_activity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //date difference

        findDifference(currentDateandTime,date2);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_information();

            }
        });
        HttpWebCall(userid);

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

        county_get = country_value.getItemAtPosition(position).toString();
        echonomic_get = economic_value.getItemAtPosition(position).toString();
        state_get = state_value.getItemAtPosition(position).toString();

        education_get = education_value.getItemAtPosition(position).toString();
        occupation_get = occupation_value.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

        Date_of_birth.setText(sdf.format(myCalendar.getTime()));
    }


    static void findDifference(final String start_date, final String end_date) {
        // SimpleDateFormat converts the
        // string format to date object
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Try Class
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            try {
                Date d1 = sdf.parse(start_date);
                Date d2 = sdf.parse(end_date);
                // in milliseconds
                long difference_In_Time
                        = d2.getTime() - d1.getTime();
                difference_In_Days
                        = TimeUnit
                        .MILLISECONDS
                        .toDays(difference_In_Time)
                        % 365;

                long difference_In_Years
                        = TimeUnit
                        .MILLISECONDS
                        .toDays(difference_In_Time)
                        / 365l;

                // Print the date difference in
                // years, in days, in hours, in
                // minutes, and in seconds


                // Print result
                age.setText("age"+String.valueOf(difference_In_Years) + "Y" + "  " + String.valueOf(difference_In_Days) + "D");

            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void upload_information() {

            progressDialog.setTitle("Loading...");
            progressDialog.show();
           final String date_of_birth=Date_of_birth.getText().toString();
//        RadioButton cehck=findViewById(radioGroup.getCheckedRadioButtonId());
//                //String gender=cehck.getText().toString();
                final String phone_value=phone.getText().toString();
                final String village_value=village.getText().toString();
                final String address1_value=address1.getText().toString();
                final String address2_value=address2.getText().toString();
                final String Postal_code=postal_code.getText().toString();
                final String relation_value=relation.getText().toString();



            StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Success = jsonObject.getString("success");
                                if (Success.equals("1")) {
                                    Toast.makeText(detail_profile_activity.this,"Your profile added\n",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(detail_profile_activity.this, user_profile_activity.class));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(detail_profile_activity.this,"Error!"+e,Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                 public void onErrorResponse(VolleyError error) {
                    Toast.makeText(detail_profile_activity.this,"Error!"+error,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>params=new HashMap<>();
                    params.put("user_id",userid);
                    try {
                        params.put("image",getStringImage(bitmap));
                    } catch (NullPointerException ex)
                    {
                        ex.printStackTrace();
                        params.put("image_value",fet_image);
                    }

                    params.put("date_birth",date_of_birth);
                    params.put("phone",phone_value);
                   // params.put("district",county_get);
                   // params.put("state",state_get);
                    params.put("village",village_value);
                    params.put("address1",address1_value);
                    params.put("address2",address2_value);
                    params.put("postal_code",Postal_code);
                    params.put("relation",relation_value);
                    //params.put("occupation",occupation_get);
//                    params.put("economic_status",echonomic_get);
//                    params.put("Education_status",education_get);



                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }





    private void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = ProgressDialog.show(detail_profile_activity.this,"Loading Data",null,true,true);
                pDialog.setCanceledOnTouchOutside(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {


                super.onPostExecute(httpResponseMsg);


                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new detail_profile_activity.GetHttpResponse(detail_profile_activity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {
                ResultHash.put("user_id",params[0]);
                ParseResult = httpParse.postRequest(ResultHash, fetching_url);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    // Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
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

                            fet_image = jsonObject.getString("image");
                            fet_phone = jsonObject.getString("phone");
                            fet_village = jsonObject.getString("village");
                            fet_address1 = jsonObject.getString("address1");
                            fet_address2 = jsonObject.getString("address2");
                            fet_postal = jsonObject.getString("postal_code");
                            fet_relation = jsonObject.getString("relation");
                            fet_work=jsonObject.getString("occupation");
                            fet_date=jsonObject.getString("date_birth");

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
            phone.setText(fet_phone);
            village.setText(fet_village);
            address1.setText(fet_address1);
            address2.setText(fet_address2);
            postal_code.setText(fet_postal);
            relation.setText(fet_relation);
            work.setText(fet_work);



            try {
                Picasso.get().load(fet_image).into(imageView);
            } catch (IllegalArgumentException ex)
            {

            }





        }


    }
}