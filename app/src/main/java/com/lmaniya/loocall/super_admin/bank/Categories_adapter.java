package com.lmaniya.loocall.super_admin.bank;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lmaniya.loocall.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Categories_adapter extends RecyclerView.Adapter<Categories_adapter.MyViewHolder> {

    private List<categories_data> C_data;
    public static Context context;

    public Categories_adapter(List<categories_data> C_data, Context context) {
        this.C_data = C_data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fetch_bank, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.Categories_name.setText(C_data.get(position).getCategories_name());
       Picasso.get().load(C_data.get(position).getCategories_image()).into(holder.Categories_image);
        holder.Categories_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(context.getApplicationContext(), update_bank.class);
                intent.putExtra("ListViewValue",C_data.get(position).getId());
                intent.putExtra("bank_name",C_data.get(position).getCategories_name());
                intent.putExtra("bank_address",C_data.get(position).getAddress());
                intent.putExtra("bank_latitude",C_data.get(position).getBank_latitude());
                intent.putExtra("bank_longitude",C_data.get(position).getLongitude());
                intent.putExtra("bank_phone",C_data.get(position).getPhone());
                intent.putExtra("bank_email",C_data.get(position).getEmail());
                intent.putExtra("bank_image",C_data.get(position).getCategories_image());


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
        TextView Categories_name,menu;
        ImageView Categories_image;
        LinearLayout Categories_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            Categories_name = itemView.findViewById(R.id.Categories_name);
            menu=itemView.findViewById(R.id.menu);
            Categories_image = itemView.findViewById(R.id.Categories_image);
            Categories_view=itemView.findViewById(R.id.categories_cardview);

        }
    }
}
