package com.lmaniya.loocall.super_admin.req_area;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class categories_api {
    public static final String BASE_URL = "https://lmaniya.com.np/hunt_new/add_room/fetch_room_for_admin.php/";
    public static Retrofit retrofit;

    public static Retrofit getApiClient(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
