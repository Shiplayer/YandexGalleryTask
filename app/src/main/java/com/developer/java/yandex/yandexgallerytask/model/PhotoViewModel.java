package com.developer.java.yandex.yandexgallerytask.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.net.YandexCommunication;

import java.util.List;

public class PhotoViewModel extends ViewModel {
    private LiveData<List<PhotoResponse>> data;

    public LiveData<List<PhotoResponse>> getPhotoResponses(String auth){
        if(data == null){
            data = YandexCommunication.getInstance().getImages(auth);
        }
        return data;
    }
}
