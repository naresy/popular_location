package com.lmaniya.loocall.login.profile.sale;

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
import com.lmaniya.loocall.add_room.detail_activity;

import com.squareup.picasso.Picasso;

import java.util.List;

public class room_fetch_adapter extends BaseAdapter {


    Context context;
    List<fetch_room_data> product_data;
    public room_fetch_adapter(List<fetch_room_data> listValue, Context context)
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
        fetch_room_data  subcategory_data=product_data.get(position);

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
                    Intent intent=new Intent(context.getApplicationContext(), detail_activity.class);
                    intent.putExtra("id",product_data.get(position).room_id);
                    intent.putExtra("owner_name",product_data.get(position).owner_name);
                    intent.putExtra("address",product_data.get(position).room_address);
                    intent.putExtra("latitude",product_data.get(position).room_latitude);
                    intent.putExtra("longitude",product_data.get(position).longitude);
                    intent.putExtra("phone",product_data.get(position).owner_phone);
                    intent.putExtra("image1",product_data.get(position).image1);
                    intent.putExtra("image2",product_data.get(position).image2);
                    intent.putExtra("description",product_data.get(position).description);
                    intent.putExtra("room_type",product_data.get(position).room_type);
                    intent.putExtra("status",product_data.get(position).available);
                    context.startActivity(intent);
                }
            });



            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (fetch_class) convertView.getTag();
        }

        viewItem.Bank_name.setText(product_data.get(position).room_type);
        try {
            Picasso.get().load(product_data.get(position).image1).into(viewItem.Bank_image);
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
