package com.cafeteria.free.findcafeteria.model;

import com.cafeteria.free.findcafeteria.api.KakaoApi;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageProvider {

    final String KAKAO_API_KEY = "KakaoAK " + "0aa376ea49fbf83258c162627de6b3bd";
    final String BASE_URL = "https://dapi.kakao.com/";


    public Observable<ImageResponse> get(String query) {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

        KakaoApi api = retrofit.create(KakaoApi.class);


        Observable<ImageResponse> observable = api.getImages(KAKAO_API_KEY, query);

        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


    }
}
