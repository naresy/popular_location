package com.lmaniya.loocall.fetch_shop.fetch_product.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.lmaniya.loocall.R;
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

            convertView = layoutInfiater.inflate(R.layout.my_order_fetch, null);

            viewItem.Product_name = (TextView)convertView.findViewById(R.id.product_title);
            viewItem.detail = (TextView)convertView.findViewById(R.id.shop_now);
            viewItem.Product_image = convertView.findViewById(R.id.product_image);
            viewItem.fetch_id=convertView.findViewById(R.id.fetchin_id);
            viewItem.price=convertView.findViewById(R.id.product_price);
            viewItem.attribute=convertView.findViewById(R.id.product_attribute);
            viewItem.menu=convertView.findViewById(R.id.cart_delete);
            viewItem.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu= new PopupMenu(context,v);
                    popupMenu.getMenuInflater().inflate(R.menu.my_order,popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())
                            {
                                case R.id.cancel:
                                 Intent intent=new Intent(context.getApplicationContext(), cancel_order_activity.class);
                                 intent.putExtra("Product_name",valueList.get(position).Name);
                                 intent.putExtra("Product_image",valueList.get(position).image);
                                 intent.putExtra("product_price",valueList.get(position).Price);
                                 intent.putExtra("order_id",valueList.get(position).id);
                                 intent.putExtra("Wt_unit",valueList.get(position).Unit);
                                 intent.putExtra("Quantity",valueList.get(position).quantity);


                                 context.startActivity(intent);
                                 break;
                                case R.id.delete_order:

                                    Intent intent1=new Intent(context.getApplicationContext(), delete_user_order.class);
                                    intent1.putExtra("Product_name",valueList.get(position).Name);
                                    intent1.putExtra("Product_image",valueList.get(position).image);
                                    intent1.putExtra("product_price",valueList.get(position).Price);
                                    intent1.putExtra("order_id",valueList.get(position).id);
                                    intent1.putExtra("Wt_unit",valueList.get(position).Unit);
                                    intent1.putExtra("Quantity",valueList.get(position).quantity);


                                    context.startActivity(intent1);


                            }
                            return false;
                        }
                    });
                }
            });




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



        viewItem.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), order_detail_activity.class);
                intent.putExtra("Product_id",valueList.get(position).id);
                intent.putExtra("Product_name",valueList.get(position).Name);
                intent.putExtra("Product_image",valueList.get(position).image);
                intent.putExtra("product_price",valueList.get(position).Price);
                intent.putExtra("order_id",valueList.get(position).id);
                intent.putExtra("Order_date",valueList.get(position).Date);
                intent.putExtra("Wt_unit",valueList.get(position).Unit);
                intent.putExtra("Quantity",valueList.get(position).quantity);
                intent.putExtra("shop_name",valueList.get(position).shop_name);
                context.startActivity(intent);
            }
        });
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
    static TextView Product_name,detail,fetch_id,price,attribute;
    Button menu;
    ImageView Product_image;
}



