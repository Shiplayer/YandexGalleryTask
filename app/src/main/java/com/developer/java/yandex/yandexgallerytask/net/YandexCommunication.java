package com.developer.java.yandex.yandexgallerytask.net;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

/**
 * Created by Shiplayer on 26.04.18.
 */

public class YandexCommunication {
    private static Retrofit retrofit;
    private static YandexCommunication mYandexCommunication;
    private static ApiCommunication apiCommunication;
    private static final String TAG = YandexCommunication.class.getSimpleName();

    private YandexCommunication(){
        retrofit = new Retrofit.Builder().baseUrl("https://cloud-api.yandex.net:443")
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiCommunication = retrofit.create(ApiCommunication.class);
    }

    public static YandexCommunication getInstance(){
        if(mYandexCommunication == null)
            mYandexCommunication = new YandexCommunication();
        return mYandexCommunication;
    }

    public List<String> getImages(String auth){
        Map<String, String> map = new HashMap<>();
        map.put("path", "disk:/Фотокамера/");
        map.put("sort", "-modified");
        LiveData<List<String>> listLiveData = new MutableLiveData<>();
        apiCommunication.getImages(map, "OAuth " + auth).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                Log.w(TAG, response.raw().message());
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.w(TAG, "fail: " + t.getMessage());
            }
        });
        return null;
    }

    public interface ApiCommunication{
        @GET("/v1/disk/resources")
        @Headers("Content-Type: application/json; charset=utf-8")
        public Call<List<String>> getImages(@QueryMap(encoded = true) Map<String, String> query,
                                            @Header("Authorization") String OAuth);
    }
}
