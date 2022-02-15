package com.lmaniya.loocall.business_user_profile;

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
import com.squareup.picasso.Picasso;

import java.util.List;

public class product_adapter extends BaseAdapter {
    Context context;
    List<product_data>product_data;
    public product_adapter(List<product_data> listValue, Context context)
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
        product_data  subcategory_data=product_data.get(position);

        if(convertView == null)
        {
            viewItem = new fetch_class();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.fetch_all_product, null);

            viewItem.sub_category_name = (TextView)convertView.findViewById(R.id.Categories_name);
            viewItem.click=convertView.findViewById(R.id.card_view);
            viewItem.sub_image=convertView.findViewById(R.id.Categories_image);



            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (fetch_class) convertView.getTag();
        }

        viewItem.sub_category_name.setText(product_data.get(position).product_name);
        try {
            Picasso.get().load(product_data.get(position).product_image).into(viewItem.sub_image);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }

        viewItem.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), detail_product_activity.class);
                intent.putExtra("ListViewValue",product_data.get(position).id);
                intent.putExtra("name",product_data.get(position).product_name);
                intent.putExtra("image",product_data.get(position).product_image);
                intent.putExtra("start_price",product_data.get(position).product_start_price);
                intent.putExtra("final_price",product_data.get(position).product_final_price);
                intent.putExtra("description",product_data.get(position).product_description);

                context.startActivity(intent);

            }
        });




        return convertView;
    }
}

class fetch_class
{
    TextView sub_category_name,id;
    CardView click;
    ImageView sub_image;



}
