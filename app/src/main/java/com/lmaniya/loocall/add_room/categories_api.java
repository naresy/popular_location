package com.lmaniya.loocall.add_room;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class categories_api {
    public static final String BASE_URL = "https://huntsewa.com/hunt_new/add_room/fetch_all_room.php/";
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
