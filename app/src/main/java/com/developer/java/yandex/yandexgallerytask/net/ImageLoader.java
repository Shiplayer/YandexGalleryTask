package com.developer.java.yandex.yandexgallerytask.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.developer.java.yandex.yandexgallerytask.adapters.GalleryAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Anton on 30.04.2018.
 */

public class ImageLoader implements Runnable {
    private final String baseURL = "https://webdav.yandex.ru";
    private static final String TAG = ImageLoader.class.getSimpleName();
    private String url;
    private String auth;
    private GalleryAdapter.OnStateListener listener;

    public ImageLoader(String path, GalleryAdapter.OnStateListener listener, String auth) {
        this.url = path.split(":/")[1] + "";
        this.auth = auth;
        Log.w(TAG, "constructor " + url);
        this.listener = listener;
    }

    public ImageLoader(String url){
        this.url = url;
        listener = new GalleryAdapter.OnStateListener() {
            @Override
            public void shutdown() {
                Log.w(TAG, "process was interrupt");
            }

            @Override
            public void downloading() {
                Log.w(TAG, "downloading");
            }

            @Override
            public void downloaded() {
                Log.w(TAG, "downloaded");
            }

            @Override
            public void setResultIntoView(Bitmap bitmap) {
                Log.w(TAG, "inserting!");
            }
        };
    }

    @Override
    public void run() {
        try {
            Log.w(TAG + "Runnable", url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(baseURL + "/" + url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.w(TAG, urlConnection.getResponseMessage());
            listener.downloading();
            //BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while(bufferedReader.ready())
                Log.w(TAG, bufferedReader.readLine());
            listener.downloaded();
            //listener.setResultIntoView(BitmapFactory.decodeStream(inputStream));
        } catch (IOException e) {
            listener.shutdown();
            e.printStackTrace();
        }
    }

    public void setOnStateListener(GalleryAdapter.OnStateListener listener){
        this.listener = listener;
    }
}
