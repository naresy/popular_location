package com.lmaniya.loocall.fetch_shop.fetch_product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class fetch_product_adapter  extends BaseAdapter {
    double  start_price,final_price,discount;

    Context context;
    List<fetch_product_date> product_data;
    public fetch_product_adapter(List<fetch_product_date> listValue, Context context)
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
        fetch_product_date  subcategory_data=product_data.get(position);

        if(convertView == null)
        {
            viewItem = new fetch_class();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.fetch_all_shop_product, null);

            viewItem.product_name = (TextView)convertView.findViewById(R.id.product_title);
            viewItem.sub_image=convertView.findViewById(R.id.product_image);
            viewItem.product_start_image=convertView.findViewById(R.id.product_start_price);
            viewItem.product_final_price=convertView.findViewById(R.id.product_final_price);
            viewItem.discount_value=convertView.findViewById(R.id.discount);
            viewItem.shop_now=convertView.findViewById(R.id.shop_now);






            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (fetch_class) convertView.getTag();
        }

        viewItem.product_name.setText(product_data.get(position).Product_name);
        viewItem.product_start_image.setText(product_data.get(position).product_start_price);
        viewItem.product_final_price.setText(product_data.get(position).product_final_price);
        start_price=Double.parseDouble(product_data.get(position).product_start_price);
        final_price =Double.parseDouble(product_data.get(position).product_final_price);
        discount=((start_price-final_price)/start_price)*100;
        final  int dis=(int)discount;

          viewItem.discount_value.setText(dis+"%");



        try {
            Picasso.get().load(product_data.get(position).product_image).into(viewItem.sub_image);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }

        viewItem.shop_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), add_cart_activity.class);
                intent.putExtra("name",product_data.get(position).Product_name);
                intent.putExtra("image",product_data.get(position).product_image);
                intent.putExtra("final_price",product_data.get(position).product_final_price);
                intent.putExtra("discount", product_data.get(position).product_start_price);
                intent.putExtra("business_id",product_data.get(position).business_id);
                intent.putExtra("ListViewValue",product_data.get(position).product_id);
                context.startActivity(intent);

            }
        });

        viewItem.sub_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.hover_layout, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Product description");
                dialogBuilder.setCancelable(true);
                final ImageView imageView=dialogView.findViewById(R.id.apv_image);
                TextView product_name=dialogView.findViewById(R.id.apv_title);
                TextView a_discount=dialogView.findViewById(R.id.apv_discount);
                TextView start_price=dialogView.findViewById(R.id.apv_price);
                TextView final_price=dialogView.findViewById(R.id.apv_final);
                TextView description=dialogView.findViewById(R.id.disc1);
                try {
                    Picasso.get().load(product_data.get(position).product_image).into(imageView);
                } catch (IllegalArgumentException ex)
                {
                    ex.printStackTrace();
                }

                start_price.setText(product_data.get(position).product_start_price);
                final_price.setText(product_data.get(position).product_final_price);
                product_name.setText(product_data.get(position).Product_name);
                description.setText(product_data.get(position).product_description);
               a_discount.setText(dis+"%");
                dialogBuilder.show();
            }



        });




        return convertView;
    }
}

class fetch_class
{
    TextView product_name,product_start_image,product_final_price,discount_value,shop_now;
    CardView click;
    ImageView sub_image;




}
