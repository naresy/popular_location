package com.lmaniya.loocall.business_user_profile.previous;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmaniya.loocall.R;
import com.lmaniya.loocall.fetch_shop.fetch_product.customer.myorder_data;
import com.squareup.picasso.Picasso;

import java.util.List;

class myorder_adapter extends BaseAdapter {
    Context context;
    List<myorder_data> valueList;

    boolean isImageFitToScreen;
    public myorder_adapter(List<myorder_data> listValue, Context context)
    {
        this.context = context;
        this.valueList = listValue;
    }

    public int getCount()

    {
        try {


            return this.valueList.size();
        }catch (NullPointerException e)
        {
            e.printStackTrace();



        }
        return 0;

    }

    @Override
    public Object getItem(int position)
    {
        return this.valueList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        produxt_class viewItem = null;
        myorder_data  subcategory_data=valueList.get(position);

        if(convertView == null)
        {
            viewItem = new produxt_class();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.past_order, null);

            viewItem.Product_name = (TextView)convertView.findViewById(R.id.product_title);
            viewItem.Product_image = convertView.findViewById(R.id.product_image);
            viewItem.fetch_id=convertView.findViewById(R.id.fetchin_id);
            viewItem.price=convertView.findViewById(R.id.product_price);
            viewItem.attribute=convertView.findViewById(R.id.product_attribute);
            viewItem.menu=convertView.findViewById(R.id.cart_delete);





            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (produxt_class) convertView.getTag();
        }
        int dt=Integer.parseInt(valueList.get(position).quantity);
        int pr=Integer.parseInt(valueList.get(position).Price);
        int tt=dt*pr;
        viewItem.Product_name.setText(valueList.get(position).Name);
        viewItem.fetch_id.setText(valueList.get(position).id);
        viewItem.price.setText(String.valueOf(tt));

            viewItem.attribute.setVisibility(View.VISIBLE);
            viewItem.attribute.setText(valueList.get(position).quantity);




        try {
            Picasso.get().load(valueList.get(position).image).into(viewItem.Product_image);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }




        return convertView;
    }
}

class produxt_class
{
    static TextView Product_name,fetch_id,price,attribute;
    Button menu;
    ImageView Product_image;
}



