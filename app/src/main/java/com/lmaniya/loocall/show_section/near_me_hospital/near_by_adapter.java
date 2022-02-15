package com.lmaniya.loocall.show_section.near_me_hospital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.lmaniya.loocall.R;

import com.lmaniya.loocall.super_admin.school.update_hospital;
import com.squareup.picasso.Picasso;

import java.util.List;

public class near_by_adapter  extends BaseAdapter {

    Context context;
    List<near_by_data> product_data;
    public near_by_adapter(List<near_by_data> listValue, Context context)
    {
        this.context = context;
        this.product_data = listValue;
    }

    public int getCount()

    {
        try {


            return this.product_data.size();
        }catch (NullPointerException e)
        {
            e.printStackTrace();



        }
        return 0;

    }

    @Override
    public Object getItem(int position)
    {
        return this.product_data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        fetch_class viewItem = null;
        near_by_data  subcategory_data=product_data.get(position);

        if(convertView == null)
        {
            viewItem = new fetch_class();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.fetch_bank, null);

            viewItem.Bank_name = (TextView)convertView.findViewById(R.id.Categories_name);

            viewItem.Bank_image=convertView.findViewById(R.id.Categories_image);
            viewItem.click=convertView.findViewById(R.id.card_view);


             viewItem.click.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent=new Intent(context.getApplicationContext(), update_hospital.class);
                     intent.putExtra("ListViewValue",product_data.get(position).bank_id);
                     intent.putExtra("bank_name",product_data.get(position).bank_name);
                     intent.putExtra("bank_address",product_data.get(position).Bank_address);
                     intent.putExtra("bank_latitude",product_data.get(position).bank_latitude);
                     intent.putExtra("bank_longitude",product_data.get(position).bank_longitude);
                     intent.putExtra("bank_phone",product_data.get(position).bank_phone);
                     intent.putExtra("bank_email",product_data.get(position).bank_email);
                     intent.putExtra("bank_image",product_data.get(position).bank_image);
                     context.startActivity(intent);
                 }
             });



            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (fetch_class) convertView.getTag();
        }

        viewItem.Bank_name.setText(product_data.get(position).bank_name);
        try {
            Picasso.get().load(product_data.get(position).bank_image).into(viewItem.Bank_image);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }






        return convertView;
    }
}

class fetch_class
{
    TextView Bank_name;
    ImageView Bank_image;
    CardView click;

}
