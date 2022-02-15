package com.lmaniya.loocall.super_admin.req_area;

import com.google.gson.annotations.SerializedName;

public class categories_data {
    @SerializedName("room_id")private String id;
    @SerializedName("owner_name") private String Categories_name;
    @SerializedName("room_image1")private String Categories_image;
    @SerializedName("room_image2")private String image2;
    @SerializedName("room_address") private String address;
    @SerializedName("room_latitude")private String latitude;
    @SerializedName("room_longitude")private String longitude;
    @SerializedName("owner_phone")private String owner_phone;
    @SerializedName("room_description")private String description;
    @SerializedName("room_type")private String room_type;



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

}
