package com.lmaniya.loocall.super_admin.add_business;

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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmaniya.loocall.R;
import com.lmaniya.loocall.super_admin.super_admin_dash_board;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class add_business_activity extends AppCompatActivity {
    EditText business_name;
    CircleImageView user_image;
    Bitmap bitmap;
    Button add_business,cancel;


    ProgressDialog progressDialog;
    static  String image_url="https://lmaniya.com.np/hunt_new/Super_add_business.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_activity);

        business_name=findViewById(R.id.business_type);

        user_image=findViewById(R.id.image);
        add_business=findViewById(R.id.Add_bussiness);
        cancel=findViewById(R.id.Cancel);


        progressDialog=new ProgressDialog(this);

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cooseimagefromfile();
            }
        });

        add_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uplaod_image_information();
                } catch (NullPointerException ex)
                {
                    ex.printStackTrace();
                    Toast.makeText(add_business_activity.this,"Please load image first..!",Toast.LENGTH_SHORT);

                }




            }
        });
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
                user_image.setImageBitmap(bitmap);
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
    private void uplaod_image_information() {
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        final String name_value=business_name.getText().toString();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, image_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(add_business_activity.this, "Business Added.....!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(add_business_activity.this, super_admin_dash_board.class));
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(add_business_activity.this,response,Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(add_business_activity.this,"Error check the connection"+error,Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
               try {
                   params.put("business_image",getStringImage(bitmap));
               } catch (NullPointerException ex)
               {
                   ex.printStackTrace();

               }
                params.put("business_name",name_value);


                return params;

            }

        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }
}


