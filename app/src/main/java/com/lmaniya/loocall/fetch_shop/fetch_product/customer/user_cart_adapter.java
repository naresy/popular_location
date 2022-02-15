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

public class user_cart_adapter extends BaseAdapter {
    Context context;
    List<cart_data> cartData;
    public String select_value="1";


    boolean isImageFitToScreen;
    public user_cart_adapter(List<cart_data> listValue, Context context)
    {
        this.context = context;
        this.cartData = listValue;
    }

    public int getCount()

    {
        try {


            return this.cartData.size();
        }catch (NullPointerException e)
        {
            e.printStackTrace();



        }
        return 0;

    }

    @Override
    public Object getItem(int position)
    {
        return this.cartData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
    int total=0;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
       cart_class viewItem = null;
        final cart_data  cartData1=cartData.get(position);

        if(convertView == null)
        {
            viewItem = new cart_class();

            LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.cart_rom, null);

            viewItem.product_name = (TextView)convertView.findViewById(R.id.product_title);
            viewItem.product_image=convertView.findViewById(R.id.product_image);
            viewItem.quantity=convertView.findViewById(R.id.quantity);
            viewItem.price=convertView.findViewById(R.id.product_price);
            viewItem.Total_price=convertView.findViewById(R.id.sub_total);
            viewItem.cart_update=convertView.findViewById(R.id.cart_delete);
            viewItem.Cart_id=convertView.findViewById(R.id.cart_id);
            viewItem.attribute=convertView.findViewById(R.id.product_attribute);




            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (cart_class) convertView.getTag();
        }

        viewItem.product_name.setText(cartData.get(position).Product_title);
        viewItem.quantity.setText(cartData.get(position).product_quantity);
        viewItem.quantity.setText(cartData.get(position).product_quantity);
        viewItem.price.setText(cartData.get(position).product_price);
        viewItem.Total_price.setText(String.valueOf(cartData.get(position).Total_price));
        viewItem.Cart_id.setText(cartData.get(position).cart_id);

        try {
            Picasso.get().load(cartData.get(position).image).into(viewItem.product_image);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }


        viewItem.cart_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.cart_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.edit:
                                Intent intent=new Intent(context.getApplicationContext(),update_user_cart_activity.class);
                                intent.putExtra("Cart_id",cartData.get(position).cart_id);
                                intent.putExtra("Product_name",cartData.get(position).Product_title);
                                intent.putExtra("Product_quantity",cartData.get(position).product_quantity);
                                intent.putExtra("Product_price",cartData.get(position).product_price);
                                intent.putExtra("Product_image",cartData.get(position).image);
                                intent.putExtra("Total_price",cartData.get(position).Total_price);



                                context.startActivity(intent);
                                break;
                            case R.id.delete:

                               Intent intent1=new Intent(context.getApplicationContext(),product_update_activity.class);
                                intent1.putExtra("Cart_id",cartData.get(position).cart_id);
                                intent1.putExtra("Product_name",cartData.get(position).Product_title);
                                intent1.putExtra("Product_quantity",cartData.get(position).product_quantity);
                                intent1.putExtra("Product_price",cartData.get(position).product_price);
                                intent1.putExtra("Product_image",cartData.get(position).image);
                                intent1.putExtra("Total_price",cartData.get(position).Total_price);





                                context.startActivity(intent1);
                                break;
                        }
                        return false;
                    }
                });



            }

        });





        return convertView;
    }
    public void removeItem(int position) {
        cartData.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyDataSetChanged();
    }


}


class cart_class
{
    TextView product_name,quantity,price,Total_price,Cart_id,attribute;
    ImageView product_image;
   Button cart_update;



}
