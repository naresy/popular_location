package com.lmaniya.loocall.super_admin.hospital;

import com.google.gson.annotations.SerializedName;

public class categories_data {
    @SerializedName("school_id")private String id;
    @SerializedName("school_name") private String Categories_name;
    @SerializedName("school_image")private String Categories_image;
    @SerializedName("school_email")private String email;
    @SerializedName("school_phone") private String phone;
    @SerializedName("school_latitude")private String bank_latitude;
    @SerializedName("school_longitude")private String longitude;
    @SerializedName("school_address") private String address;

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

    public String getPhone()
    {
        return phone;
    }
    public String getEmail()
    {
        return email;
    }
    public String getBank_latitude()
    {
        return bank_latitude;
    }
    public String getLongitude()
    {
        return longitude;
    }
    public String getAddress()
    {
        return address;
    }
}
