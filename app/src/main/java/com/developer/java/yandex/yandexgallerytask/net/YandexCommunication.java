package com.developer.java.yandex.yandexgallerytask.net;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.model.ResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * class for getting information about images and getting an image from yandex disk with OAuth2
 * @author Shiplayer
 */
public class YandexCommunication {
    private static Retrofit retrofit;
    private static YandexCommunication mYandexCommunication;
    private static ApiCommunication apiCommunication;
    private static final String TAG = YandexCommunication.class.getSimpleName();
    private static String oAuth = null;

    /**
     * Initialize retrofit for communication with yandex disk, create client with timeout
     * internet connection
     */
    private YandexCommunication(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://cloud-api.yandex.net:443")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build();
        apiCommunication = retrofit.create(ApiCommunication.class);
    }

    /**
     * Gets instance of YandexCommunication for interaction with yandex disk
     * @return mYandexCommunication A instance of YandexCommunication
     */

    public static YandexCommunication getInstance(){
        if(mYandexCommunication == null)
            mYandexCommunication = new YandexCommunication();
        return mYandexCommunication;
    }

    /**
     * Sets authentication for communication
     * @param auth A String containing authentication
     */

    public static void setAuth(String auth){
        oAuth = "OAuth " + auth;
    }

    /**
     * Used retrofit for getting json object that contain information about images in
     * directory "Фотокамера"
     * @return List of PhotoResponse element wrapped in LiveData
     */

    public LiveData<ResponseModel<List<PhotoResponse>>> getPhotoResponses(){
        Log.w(TAG, "getPhotoResponses");
        Map<String, String> map = new HashMap<>();
        map.put("path", "disk:/Фотокамера/");
        map.put("sort", "-modified");
        map.put("limit", "50");
        final MutableLiveData<ResponseModel<List<PhotoResponse>>> listLiveData = new MutableLiveData<>();
        apiCommunication.getImages(map, oAuth).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.w(TAG, call.request().url().toString());
                Log.w(TAG, "header: " + call.request().header("Authorization"));
                JsonObject body = response.body();
                List<PhotoResponse> photoList;
                if(response.isSuccessful() && response.code() == 200 && body.has("_embedded")){
                    Gson gson = new GsonBuilder().create();
                    body = body.getAsJsonObject("_embedded");
                    photoList = new ArrayList<>();
                    JsonArray array = body.getAsJsonArray("items");
                    for(JsonElement elem : array){

                        if(elem.getAsJsonObject().get("mime_type").getAsString().equals("image/jpeg")) {
                            final PhotoResponse photo = gson.fromJson(elem, PhotoResponse.class);
                            photoList.add(photo);
                        }
                    }
                    for(PhotoResponse photo : photoList){
                        Log.w(TAG, photo.toString());
                    }
                    ResponseModel<List<PhotoResponse>> r = new ResponseModel<>(photoList);
                    Log.w(TAG, r.toString());
                    listLiveData.postValue(r);
                } else{
                    Log.e(TAG, response.message());
                    listLiveData.postValue(new ResponseModel<List<PhotoResponse>>(null, response.message()));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.w(TAG, "fail: " + t.getMessage() + "\nurl: " + call.request().url().toString()+
                "\nheader: " + call.request().header("Authorization"));
                listLiveData.postValue(new ResponseModel<List<PhotoResponse>>(null, t.getMessage()));
            }
        });
        return listLiveData;
    }

    public LiveData<String> getLink(String path){
        Map<String, String> map = new HashMap<>();
        map.put("path", path);
        map.put("field", "href");
        final MutableLiveData<String> link = new MutableLiveData<>();
        apiCommunication.getLink(map, oAuth).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful())
                    link.postValue(response.body().get("href").getAsString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
        return link;
    }

    public void lastUpdated(){
        Map<String, String> map = new HashMap<>();
        map.put("media_type", "image");
        map.put("limit", "200");
        apiCommunication.getLastUpdated(map, oAuth).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonArray array= response.body().getAsJsonArray("items");
                    for(JsonElement el : array){
                        JsonObject obj = el.getAsJsonObject();
                        Log.w(TAG, "name = " + obj.get("name").getAsString() + "\n" +
                            "modified = " + obj.get("modified"));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void getInfo(){
        apiCommunication.getInfo(oAuth).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.w(TAG, response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public interface ApiCommunication{
        @GET("/v1/disk/")
        @Headers("Content-Type: application/json; charset=utf-8")
        public Call<JsonObject> getInfo(@Header("Authorization") String auth);
        @GET("/v1/disk/resources")
        @Headers("Content-Type: application/json; charset=utf-8")
        public Call<JsonObject> getImages(@QueryMap(encoded = true) Map<String, String> query, @Header("Authorization") String auth);
        @GET("/v1/disk/resources/last-uploaded")
        @Headers("Content-Type: application/json; charset=utf-8")
        public Call<JsonObject> getLastUpdated(@QueryMap(encoded = true) Map<String, String> query, @Header("Authorization") String auth);

        @GET("/v1/disk/resources/download")
        public Call<JsonObject> getLink(@QueryMap(encoded = true) Map<String, String> query, @Header("Authorization") String auth);
    }
}
