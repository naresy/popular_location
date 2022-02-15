package com.lmaniya.loocall.fetch_shop;

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
        fetch_data viewItem = null;
        near_by_data  subcategory_data=product_data.get(position);

        if (convertView == null) {
            viewItem = new fetch_data();

            LayoutInflater layoutInfiater = (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.fetch_all_product, null);

            viewItem.sub_category_name = (TextView) convertView.findViewById(R.id.Categories_name);
            viewItem.click = convertView.findViewById(R.id.card_view);
            viewItem.sub_image = convertView.findViewById(R.id.Categories_image);


            convertView.setTag(viewItem);
        } else {
            viewItem = (fetch_data) convertView.getTag();
        }

        viewItem.sub_category_name.setText(product_data.get(position).name);
        try {
            Picasso.get().load(product_data.get(position).image).into(viewItem.sub_image);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        viewItem.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), shop_detail_activity.class);
                intent.putExtra("ListViewValue", product_data.get(position).id);
                intent.putExtra("name", product_data.get(position).name);
                intent.putExtra("image", product_data.get(position).image);
                intent.putExtra("star_date", product_data.get(position).start_date);
                intent.putExtra("latitude", product_data.get(position).latitude);
                intent.putExtra("longitude", product_data.get(position).Longitude);
                intent.putExtra("phone", product_data.get(position).phone);
                intent.putExtra("village",product_data.get(position).village);

                context.startActivity(intent);

            }
        });


        return convertView;
    }
}

class fetch_data
{
    TextView sub_category_name, id;
    CardView click;
    ImageView sub_image;

}
