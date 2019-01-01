package com.cafeteria.free.findcafeteria.api;

import com.cafeteria.free.findcafeteria.model.ImageResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface KakaoApi {

    @GET("/v2/search/image")
    @Headers("Host: dapi.kakao.com")
    Call<ImageResponse> getImages(@Header("Authorization") String apiKey, @Query("query") String keyword);
}
