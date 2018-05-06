package com.developer.java.yandex.yandexgallerytask.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.net.YandexCommunication;

import java.util.List;

public class PhotoViewModel extends ViewModel {
    private MutableLiveData<ResponseModel<List<PhotoResponse>>> data;
    private String text;

    public LiveData<ResponseModel<List<PhotoResponse>>> getPhotoResponses(){
        if(data == null){
            data = new MutableLiveData<>();
            download();
        }
        return data;
    }

    private void download(){
        final LiveData<ResponseModel<List<PhotoResponse>>> live = YandexCommunication.getInstance().getPhotoResponses();
        live.observeForever(new Observer<ResponseModel<List<PhotoResponse>>>() {
            @Override
            public void onChanged(@Nullable ResponseModel<List<PhotoResponse>> listResponseModel) {
                data.postValue(listResponseModel);
                live.removeObserver(this);
            }
        });
    }

    public void updatePhotoResponses(){
        download();
        Log.w("PhotoViewModel", data.toString());
    }

    public void setText(String str){
        text = str;
    }

    public String getText(){
        return text;
    }
}
