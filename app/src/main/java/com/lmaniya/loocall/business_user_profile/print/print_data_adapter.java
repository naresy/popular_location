package com.lmaniya.loocall.business_user_profile.print;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.lmaniya.loocall.R;

import java.util.List;

public class print_data_adapter extends BaseAdapter {
    Context context;
    List<print_bill_data> valueList;
    int count=1;

    boolean isImageFitToScreen;
    public print_data_adapter(List<print_bill_data> listValue, Context context)
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
        print_data viewItem = null;
        print_bill_data  subcategory_data=valueList.get(position);

        if(convertView == null)
        {
            viewItem = new print_data();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.print_bill_admin, null);

            viewItem.Product_name = (TextView)convertView.findViewById(R.id.product_name);
            viewItem.S_N = (TextView)convertView.findViewById(R.id.order_id);
            viewItem.quantity= convertView.findViewById(R.id.quantity);
            viewItem.total_price=convertView.findViewById(R.id.total);
            viewItem.price=convertView.findViewById(R.id.product_price);
            viewItem.sku=convertView.findViewById(R.id.sku);







            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (print_data) convertView.getTag();
        }
        viewItem.S_N.setText(String.valueOf(count));
        viewItem.Product_name.setText(valueList.get(position).Product_name);
        viewItem.quantity.setText(valueList.get(position).quantity);
        viewItem.total_price.setText(String.valueOf(valueList.get(position).Total_price));
        viewItem.price.setText(valueList.get(position).Product_price);
        viewItem.sku.setText(valueList.get(position).sku);
        count++;
        return convertView;

    }
}

class print_data
{
    static TextView Product_name,S_N,quantity,total_price,price,sku;




}
