package com.developer.java.yandex.yandexgallerytask.api;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Anton on 25.04.2018.
 */

public class FlickrApi {
    private static final String TAG = FlickrApi.class.getSimpleName();
    private static Retrofit retrofit;
    private static ApiMethods apiMethods;
    private static FlickrApi flickrApi;

    private FlickrApi(String url){
        retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(TikXmlConverterFactory.create()).build();

        apiMethods = retrofit.create(ApiMethods.class);
    }

    public static FlickrApi getInstance(){
        if(flickrApi == null)
            flickrApi = new FlickrApi("https://api.flickr.com");
        return flickrApi;
    }

    interface ApiMethods{
        @POST("/services/rest/")
        //{"api_key: bc5371d5c931b1e37d1a5b3626a2b93e", "format: rest"}
        public Call<String> getFromMethods(@QueryMap(encoded = true) Map<String, String> query);
    }

    public String getXMLRecentPhoto(){
        Map<String, String> map = new HashMap<>();
        map.put("method", "flickr.photos.getRecent");
        map.put("api_key", "bc5371d5c931b1e37d1a5b3626a2b93e");
        map.put("format", "rest");
        apiMethods.getFromMethods(map).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.w(TAG, call.request().url().toString());
                String message = response.isSuccessful() ?
                        "Data sending was successful" :
                        "Ups... Something went wrong!" + "\n" + getAllInformationAboutResponse(response);
                Log.w(TAG, message);
                Log.w(TAG, String.valueOf(response.body().length()));
                Log.w(TAG, response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.w(TAG, "error: " + t.getMessage());
            }
        });
        return null;
    }

    private <T> String getAllInformationAboutResponse(Response<T> response){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("code=").append(response.code()).append("\n").append("errorBody=\'");
        BufferedReader bf = new BufferedReader(new InputStreamReader(response.errorBody().byteStream()));
        try {
            while(bf.ready()){
                stringBuilder.append(bf.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuilder.append("\nheaders:");
        for (String name :
                response.headers().names()) {
            stringBuilder.append(name).append("=").append(response.headers().get(name)).append("\n");
        }
        return stringBuilder.toString();
    }
}
