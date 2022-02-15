package com.lmaniya.loocall.business_user_profile.previous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lmaniya.loocall.R;

import java.util.List;


public class order_by_user_adapter extends BaseAdapter {

    Context context;
    List<order_by_user_data> product_data;
    public order_by_user_adapter(List<order_by_user_data> listValue, Context context)
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
        order_by_user_data  subcategory_data=product_data.get(position);

        if(convertView == null)
        {
            viewItem = new fetch_class();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.order_by_user, null);

            viewItem.user_name = (TextView)convertView.findViewById(R.id.ordername);
            viewItem.email=convertView.findViewById(R.id.email);
            viewItem.phone=convertView.findViewById(R.id.phone);
            viewItem.bill=convertView.findViewById(R.id.bill_number);
            viewItem.payment=convertView.findViewById(R.id.payment);
            viewItem.address=convertView.findViewById(R.id.address);
            viewItem.click=convertView.findViewById(R.id.click);




            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (fetch_class) convertView.getTag();
        }

        viewItem.user_name.setText(product_data.get(position).name);
        viewItem.email.setText(product_data.get(position).email);
        viewItem.phone.setText(product_data.get(position).phone);
        viewItem.address.setText(product_data.get(position).address);
        viewItem.payment.setText(product_data.get(position).payment);
        viewItem.bill.setText(product_data.get(position).bill);


        viewItem.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), detail_activity.class);

               intent.putExtra("shop_id",product_data.get(position).shop_id);
               intent.putExtra("name",product_data.get(position).name);
               intent.putExtra("user_id",product_data.get(position).user_id);


                context.startActivity(intent);

            }
        });




        return convertView;
    }
}

class fetch_class
{
    TextView user_name,bill,payment,phone,email,address;
    LinearLayout click;





}


