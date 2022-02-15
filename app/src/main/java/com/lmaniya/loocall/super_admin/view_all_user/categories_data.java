package com.lmaniya.loocall.super_admin.view_all_user;

import com.google.gson.annotations.SerializedName;

public class categories_data {
    @SerializedName("id")private String id;
    @SerializedName("name") private String Categories_name;
    @SerializedName("image")private String Categories_image;
    public String getId()
    {
        return  id;
    }
    public String getCategories_name()
    {
        return Categories_name;
    }
    public String getCategories_image()
    {
        return Categories_image;
    }
}
