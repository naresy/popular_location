package com.lmaniya.loocall;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class categories_api {
    public static final String BASE_URL = "https://lmaniya.com.np/hunt_new/fetch_all_business.php/";
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
