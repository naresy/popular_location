package com.lmaniya.loocall.super_admin.view_shop;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<Contact> contacts;
    private Context context;




    public Adapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.direct_product_fetch, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.name.setText(contacts.get(position).getName());






        try {
            Picasso.get().load(contacts.get(position).getEmail()).into(holder.imageView);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }

        holder.shop_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(), shop_detail_activity.class);
                intent.putExtra("ListViewValue",contacts.get(position).getId());
                intent.putExtra("name",contacts.get(position).getName());
                intent.putExtra("phone",contacts.get(position).getFinal_price());
                intent.putExtra("latitude",contacts.get(position).getLatitude());
                intent.putExtra("longitude",contacts.get(position).getLongitude());
                intent.putExtra("village",contacts.get(position).getIntial_price());
                intent.putExtra("image",contacts.get(position).getEmail());
                intent.putExtra("star_date",contacts.get(position).getStart_date());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,product_price,initail_price,shop_now,discoun;
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_title);
            product_price=itemView.findViewById(R.id.product_price);
            imageView=itemView.findViewById(R.id.product_image);
            initail_price=itemView.findViewById(R.id.product_attribute);
            shop_now=itemView.findViewById(R.id.shop_now);
            discoun=itemView.findViewById(R.id.discount);



        }
    }
}
