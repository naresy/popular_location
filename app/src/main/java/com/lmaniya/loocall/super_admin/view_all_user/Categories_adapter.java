package com.lmaniya.loocall.super_admin.view_all_user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lmaniya.loocall.R;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fetch_all_business, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.Categories_name.setText(C_data.get(position).getCategories_name());
       Picasso.get().load(C_data.get(position).getCategories_image()).into(holder.Categories_image);




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
        CircleImageView Categories_image;
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
