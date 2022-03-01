package com.lmaniya.loocall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lmaniya.loocall.fetch_shop.all_shop_activity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Categories_adapter extends RecyclerView.Adapter<Categories_adapter.MyViewHolder> {

    private List<categories_data> C_data;
    public static Context context;

    public Categories_adapter(List<categories_data> C_data, Context context) {
        this.C_data = C_data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fetch_room, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.Categories_name.setText(C_data.get(position).getCategories_name());
        try {
            Picasso.get().load(C_data.get(position).getCategories_image()).into(holder.Categories_image);
        } catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }

        holder.Categories_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(context.getApplicationContext(), all_shop_activity.class);
                intent.putExtra("ListViewValue",C_data.get(position).getId());
                intent.putExtra("Category_name",C_data.get(position).getCategories_name());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        try {
            return C_data.size();
        } catch (NullPointerException ex)
        {
            ex.printStackTrace();

        }

       return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Categories_name;
        ImageView Categories_image;
        LinearLayout Categories_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            Categories_name = itemView.findViewById(R.id.business_name);
            Categories_image = itemView.findViewById(R.id.business_image);
            Categories_view=itemView.findViewById(R.id.categories_cardview);

        }
    }
}
