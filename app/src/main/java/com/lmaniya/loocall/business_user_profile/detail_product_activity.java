package com.lmaniya.loocall.business_user_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lmaniya.loocall.HttpParse;
import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class detail_product_activity extends AppCompatActivity {
    EditText name,start_price,final_price,description;
    ImageView image;
    String name_value,start_price_value,final_price_value,description_value,image_value,id;
      String  HttpUrlDeleteRecord="https://lmaniya.com.np/hunt_new/product/delete_product.php",
          HttpURL="https://lmaniya.com.np/hunt_new/update_product.php";
    Button delete,update;


    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    String ParseResult ;
    String FinalJSonObject ;
    ProgressDialog progressDialog2;
    HttpParse httpParse = new HttpParse();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product_activity);
        name=findViewById(R.id.product_name);
        start_price=findViewById(R.id.product_start_price);
        final_price=findViewById(R.id.product_final_price);
        image=findViewById(R.id.imageview_id_picture);
        description=findViewById(R.id.product_description);
        name_value=getIntent().getStringExtra("name");
        image_value=getIntent().getStringExtra("image");
        start_price_value=getIntent().getStringExtra("start_price");
        final_price_value=getIntent().getStringExtra("final_price");
        description_value=getIntent().getStringExtra("description");
        id=getIntent().getStringExtra("ListViewValue");
        delete=findViewById(R.id.delete_product);
        update=findViewById(R.id.update_product);

        name.setText(name_value);
        start_price.setText(start_price_value);
        final_price.setText(final_price_value);
        description.setText(description_value);
        start_price.setText(start_price_value);
        final_price.setText(final_price_value);
        description.setText(description_value);
        Picasso.get().load(image_value).into(image);
        progressDialog2=new ProgressDialog(this);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_product(id);

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_from_edit();
              update_product(id,name_value,start_price_value,final_price_value,description_value);


            }
        });

    }

    private void get_from_edit() {

        name_value=name.getText().toString();
        start_price_value=start_price.getText().toString();
        final_price_value=final_price.getText().toString();
        description_value=description.getText().toString();
    }

    private void update_product(String id, String name_value, String start_price_value, String final_price_value, String description_value) {

        class StudentRecordUpdateClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog2.setTitle("loading...");
                progressDialog2.show();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog2.dismiss();

                Toast.makeText(detail_product_activity.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(detail_product_activity.this,business_user_profile.class));


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("product_id",params[0]);

                hashMap.put("product_name",params[1]);

                hashMap.put("product_start_price",params[2]);

                hashMap.put("product_final_price",params[3]);
                hashMap.put("product_description",params[4]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        StudentRecordUpdateClass studentRecordUpdateClass = new StudentRecordUpdateClass();

        studentRecordUpdateClass.execute(id,name_value,start_price_value,final_price_value,description_value);

    }

    private void delete_product(String id) {

        class StudentDeleteClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog2.setTitle("Loading...");
                progressDialog2.show();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog2.dismiss();

                Toast.makeText(detail_product_activity.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(detail_product_activity.this,business_user_profile.class));

                finish();

            }

            @Override
            protected String doInBackground(String... params) {

                // Sending STUDENT id.
                hashMap.put("product_id", params[0]);

                finalResult = httpParse.postRequest(hashMap, HttpUrlDeleteRecord);

                return finalResult;
            }
        }

        StudentDeleteClass studentDeleteClass = new StudentDeleteClass();

        studentDeleteClass.execute(id);
    }
}
