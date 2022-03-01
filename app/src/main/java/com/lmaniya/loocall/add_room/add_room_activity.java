package com.lmaniya.loocall.add_room;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.lmaniya.loocall.Main_page_activity;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.help.MapsActivity;
import com.lmaniya.loocall.login.helper.DatabaseHandler;
import com.lmaniya.loocall.login.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class add_room_activity extends AppCompatActivity {
  public static EditText room_type,owner_phone,owner_name,full_address,latitude,longitude,description;
    ImageView image,image2;
    Button add;
    TextView get_location;

   SessionManager sessionManager;
   DatabaseHandler databaseHandler;
    private HashMap<String,String> user = new HashMap<>();
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    LinearLayout relativeLayout;
    private boolean internetConnected=true;
    ProgressDialog progressDialog;
    Bitmap bitmap,bitmap1;
    final Calendar myCalendar = Calendar.getInstance();
    String postURL="https://lmaniya.com.np/hunt_new/add_room/add_room.php";
    String name,user_id,user_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room_activity);

        room_type=findViewById(R.id.title);
        owner_phone=findViewById(R.id.owner_phone);
        owner_name=findViewById(R.id.owner_name);
        full_address=findViewById(R.id.full_address);
        latitude=findViewById(R.id.latitude);
        longitude=findViewById(R.id.longitude);
        description=findViewById(R.id.description_room);
        image=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        add=findViewById(R.id.add);
        setTitle("Add Room for find ");
        get_location=findViewById(R.id.get);
        progressDialog=new ProgressDialog(this);
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(add_room_activity.this, MapsActivity.class);
                intent.putExtra("select","6");
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_information();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cooseimagefromfile();
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cooseimagefromfile1();
            }
        });
        sessionManager=new SessionManager(getApplicationContext());
        databaseHandler=new DatabaseHandler(getApplicationContext());
      user=databaseHandler.getUserDetails();
       user_id=user.get("uid");
       name=user.get("name");
       user_name=user.get("email");



    }

    private void upload_information() {

        if (isfileEmpty(room_type)&&isfileEmpty(owner_name)&&isfileEmpty(owner_phone)&&isfileEmpty(full_address)&&isfileEmpty(latitude)&&
        isfileEmpty(longitude)&&isfileEmpty(description))
        {


             progressDialog.setTitle("Loading....");
             progressDialog.show();
            final String type_value=room_type.getText().toString();
            final String owner_name_value=owner_name.getText().toString();
            final String owner_phone_value=owner_phone.getText().toString();
            final String address_value=full_address.getText().toString();
            final String latitude_value=latitude.getText().toString();
            final String longitude_value=longitude.getText().toString();
            final String description_value=description.getText().toString();



            double lat1 =Double.valueOf(latitude_value);

            double lon1 = Double.valueOf(longitude_value);
            double lat2 = 27.7888830;
            double lon2 = 85.345124;
            final Double dist= distance(lat1, lat2, lon1, lon2);


            StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Success = jsonObject.getString("success");
                                if (Success.equals("1")) {
                                    Toast.makeText(add_room_activity.this,"Room added please wait for admin verification",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(add_room_activity.this, Main_page_activity.class));
                                }
                                else if (Success.equals("0"))
                                {
                                    Toast.makeText(add_room_activity.this,"you already added shop please contact with admin",Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(add_room_activity.this,"Error!"+e,Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(add_room_activity.this,"Error!"+error,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>params=new HashMap<>();
                    params.put("room_type",type_value);
                    params.put("owner_name",owner_name_value);
                    params.put("full_address",address_value);
                    params.put("description",description_value);
                    params.put("latitude",latitude_value);
                    params.put("longitude",longitude_value);
                    params.put("image",getStringImage(bitmap));
                    params.put("image2",getStringImage(bitmap1));
                    params.put("owner_phone",owner_phone_value);
                    params.put("distance",String.valueOf(dist));
                    params.put("user_name",name);
                    params.put("user_id",user_id);
                    params.put("user_email",user_name);




                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
    }


    public static double distance(double lat1, double lat2, double lon1, double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }





    private void cooseimagefromfile() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),1);
    }

    private void cooseimagefromfile1() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filepath = data.getData();
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                image2.setImageBitmap(bitmap1);
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