package com.lmaniya.loocall.super_admin.school;

import com.google.gson.annotations.SerializedName;

public class categories_data {
    @SerializedName("hospital_id")private String id;
    @SerializedName("hospital_name") private String Categories_name;
    @SerializedName("hospital_image")private String Categories_image;
    @SerializedName("hospital_email")private String email;
    @SerializedName("hospital_phone") private String phone;
    @SerializedName("hospital_latitude")private String bank_latitude;
    @SerializedName("hospital_longitude")private String longitude;
    @SerializedName("hospital_address") private String address;

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
