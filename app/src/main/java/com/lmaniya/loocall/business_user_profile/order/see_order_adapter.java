package com.lmaniya.loocall.business_user_profile.order;

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
import com.squareup.picasso.Picasso;

import java.util.List;

public class see_order_adapter  extends BaseAdapter {

    Context context;
    List<see_order_data> cartData;
    public String select_value="1";


    boolean isImageFitToScreen;
    public see_order_adapter(List<see_order_data> listValue, Context context)
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
        final see_order_data  cartData1=cartData.get(position);

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








        return convertView;
    }
    public void removeItem(int position) {
        cartData.remove(position);

        notifyDataSetChanged();
    }


}


class cart_class
{
    TextView product_name,quantity,price,Total_price,Cart_id,attribute;
    ImageView product_image;
    Button cart_update;



}


