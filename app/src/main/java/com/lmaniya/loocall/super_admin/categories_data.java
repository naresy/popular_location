package com.lmaniya.loocall.super_admin;

import com.google.gson.annotations.SerializedName;

public class categories_data {
    @SerializedName("business_id")private String id;
    @SerializedName("business_name") private String Categories_name;
    @SerializedName("business_image")private String Categories_image;
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
