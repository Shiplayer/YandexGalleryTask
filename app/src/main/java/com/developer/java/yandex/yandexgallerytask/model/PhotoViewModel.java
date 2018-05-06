package com.developer.java.yandex.yandexgallerytask.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.net.YandexCommunication;

import java.util.List;

public class PhotoViewModel extends ViewModel {
    private LiveData<ResponseModel<List<PhotoResponse>>> data;
    private MediatorLiveData<String> links = new MediatorLiveData<>();
    private String text;

    public LiveData<ResponseModel<List<PhotoResponse>>> getPhotoResponses(){
        if(data == null){
            data = new MutableLiveData<>();
            data = YandexCommunication.getInstance().getPhotoResponses();
        }
        Log.w("PhotoViewModel", data.toString());
        return data;
    }

    public void updatePhotoResponses(){
        data = YandexCommunication.getInstance().getPhotoResponses();
        Log.w("PhotoViewModel", data.toString());
    }

    public void setText(String str){
        text = str;
    }

    public String getText(){
        return text;
    }
}
