package com.lmaniya.loocall.shell;

import com.google.gson.annotations.SerializedName;

public class categories_data {
    @SerializedName("sale_id")private String id;
    @SerializedName("owner_name") private String Categories_name;
    @SerializedName("sale_image1")private String Categories_image;
    @SerializedName("sale_image2")private String image2;
    @SerializedName("sale_address") private String address;
    @SerializedName("sale_latitude")private String latitude;
    @SerializedName("sale_longitude")private String longitude;
    @SerializedName("owner_phone")private String owner_phone;
    @SerializedName("sale_description")private String description;
    @SerializedName("sale_type")private String room_type;
    @SerializedName("sold_status")private String sold;



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
    public  String getImage2()
    {
        return  image2;
    }
    public  String getLatitude()
    {
        return  latitude;
    }
    public  String getLongitude()
    {
        return  longitude;
    }
    public  String getOwner_phone()
    {
        return  owner_phone;
    }
    public  String getAddress()
    {
        return  address;
    }
    public  String getDescription()
    {
        return  description;
    }
    public  String getRoom_type()
    {
        return  room_type;
    }
    public  String getSold()
    {
        return sold;
    }

}
