package com.cafeteria.free.findcafeteria.model;

import android.util.Log;

import com.cafeteria.free.findcafeteria.api.KakaoApi;
import com.cafeteria.free.findcafeteria.util.Logger;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageProvider {

    final String KAKAO_API_KEY = "KakaoAK "+ "0aa376ea49fbf83258c162627de6b3bd";

    final String BASE_URL = "https://dapi.kakao.com/";


    public void get(String query) {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                Retrofit retrofit =
                        new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(client)
                                .build();

                KakaoApi api = retrofit.create(KakaoApi.class);


                Call<ImageResponse> call = api.getImages(KAKAO_API_KEY, query);
                try {
                    retrofit2.Response<ImageResponse> response= call.execute();
                    Logger.d(response.body().toString());


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();


    }
}
