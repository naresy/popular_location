package com.lmaniya.loocall.super_admin.view_all_user;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class categories_api {
    public static final String BASE_URL = "https://huntsewa.com/hunt_new/see_order/see_all_user.php/";
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
