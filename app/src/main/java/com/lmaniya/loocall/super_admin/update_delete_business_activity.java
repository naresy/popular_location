package com.lmaniya.loocall.super_admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class update_delete_business_activity extends AppCompatActivity {
    CircleImageView imageView;
    TextView business;
    String image,name,id,finalResult,HttpURL="hhttps://lmaniya.com.np/hunt_new/delete_update_business.php",update_name,update_image;
    Button delete_business,update_business;
    AlertDialog.Builder builder;
    String http="https://lmaniya.com.np/hunt_new/update_add_business.php";
    ProgressDialog progressDialog;
    HttpParse httpParse = new HttpParse();
    HashMap<String, String> ResultHash = new HashMap<>();
    Bitmap bitmap;
    HashMap<String, String> hashMap = new HashMap<>();
    String postURL="https://lmaniya.com.np/hunt_new/update_business.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_business_activity);
        imageView = findViewById(R.id.Categories_image);
        business = findViewById(R.id.Categories_name);
        image = getIntent().getStringExtra("image");
        name = getIntent().getStringExtra("Category_name");
        id = getIntent().getStringExtra("ListViewValue");
        business.setText(name);
        builder = new AlertDialog.Builder(this);
       try {
           Picasso.get().load(image).into(imageView);
       } catch (IllegalArgumentException ex)
       {
           ex.printStackTrace();
       }

        delete_business = findViewById(R.id.delete_business);
        update_business = findViewById(R.id.updatebusiness);
        progressDialog = new ProgressDialog(this);
        final String delete = "delete";
  update_business.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          upload_information();
      }
  });
  imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          cooseimagefromfile();
      }
  });
        delete_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Do you want to delete...?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Update_cart(id, delete);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
                builder.show();

            }
        });
    }

    //update
    private void Update_cart(String id,String delete) {
        class menuUpdateClass extends AsyncTask<String,Void,String>
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setTitle("loading...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Toast.makeText(update_delete_business_activity.this,s.toString(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(update_delete_business_activity.this, super_admin_dash_board.class));
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("selection",params[0]);
                finalResult=httpParse.postRequest(hashMap,HttpURL);
                return finalResult;
            }
        }
        menuUpdateClass menuUpdateClass=new menuUpdateClass();
        menuUpdateClass.execute(id,delete);



    }

    private void upload_information() {

        progressDialog.setTitle("Loading...");
        progressDialog.show();
        final String name_value_get=business.getText().toString();



        StringRequest stringRequest=new StringRequest(Request.Method.POST, postURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Success = jsonObject.getString("success");
                            if (Success.equals("1")) {
                                Toast.makeText(update_delete_business_activity.this,"business update\n",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(update_delete_business_activity.this, super_admin_dash_board.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(update_delete_business_activity.this,"Error!"+e,Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(update_delete_business_activity.this,"Error!"+error,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("business_id",id);
                try {
                    params.put("business_image",getStringImage(bitmap));
                } catch (NullPointerException ex)
                {
                    params.put("image_value",image);
                }
                params.put("business_name",name_value_get);





                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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


}
