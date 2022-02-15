package com.lmaniya.loocall;



import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface categories_api_interface {
    @GET("getcontact.php")
    Call<List<categories_data>> getData(@Query("item_type") String item_type,
                                        @Query("key") String keyword);

}
