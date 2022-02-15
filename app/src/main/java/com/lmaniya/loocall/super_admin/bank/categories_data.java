package com.lmaniya.loocall.super_admin.bank;

import com.google.gson.annotations.SerializedName;

public class categories_data {
    @SerializedName("bank_id")private String id;
    @SerializedName("bank_name") private String Categories_name;
    @SerializedName("bank_image")private String Categories_image;
    @SerializedName("bank_email")private String email;
    @SerializedName("bank_phone") private String phone;
    @SerializedName("bank_latitude")private String bank_latitude;
    @SerializedName("bank_longitude")private String longitude;
    @SerializedName("bank_address") private String address;

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
