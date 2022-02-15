package com.lmaniya.loocall.help.show;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.lmaniya.loocall.R;

import java.util.List;


public class out_help_adapter  extends BaseAdapter {

    Context context;
    List<out_help_data> valueList;
     public out_help_adapter(List<out_help_data> listValue, Context context)
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
        fetch_class viewItem = null;
        out_help_data  subcategory_data=valueList.get(position);

        if(convertView == null)
        {
            viewItem = new fetch_class();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.out_user_help, null);
            viewItem.name=convertView.findViewById(R.id.name);
            viewItem.type=convertView.findViewById(R.id.help_title);
            viewItem.map=convertView.findViewById(R.id.product_image);
            viewItem.detail=convertView.findViewById(R.id.detail);








            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (fetch_class) convertView.getTag();
        } boolean isImageFitToScreen;
        viewItem.name.setText(valueList.get(position).Name);
        viewItem.type.setText(valueList.get(position).help_type);
        viewItem.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context.getApplicationContext(),help_detail_activity.class);
                intent.putExtra("id",valueList.get(position).id);
                context.startActivity(intent);
            }
        });
        viewItem.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context.getApplicationContext(),help_detail_activity.class);
                intent.putExtra("id",valueList.get(position).id);
                context.startActivity(intent);

            }
        });







        return convertView;
    }
}

class fetch_class
{
    ImageView map;
    TextView name,type;
    Button detail;




}

