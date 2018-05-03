package com.developer.java.yandex.yandexgallerytask.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developer.java.yandex.yandexgallerytask.entity.PhotoResponse;
import com.developer.java.yandex.yandexgallerytask.net.YandexCommunication;

import java.util.List;

public class PhotoViewModel extends ViewModel {
    private static final String TAG = PhotoViewModel.class.getCanonicalName();
    private LiveData<List<PhotoResponse>> data;
    private MediatorLiveData<String> links = new MediatorLiveData<>();

    public LiveData<List<PhotoResponse>> getPhotoResponses(){
        return data;
    }

    public LiveData<List<PhotoResponse>> getPhotoResponses(String auth){
        if(data == null){
            data = YandexCommunication.getInstanceWithClient(auth).getPhotoResponses();
        }
        return data;
    }

    public MediatorLiveData<String> getLinkImage(List<String> list) {

        for (int i = 0; i < list.size(); i++) {
            Log.e(TAG, String.valueOf(YandexCommunication.getInstance().getLink(list.get(i)) == null));
            links.addSource(YandexCommunication.getInstance().getLink(list.get(i)), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    links.setValue(s);
                }
            });
        }

        return links;
    }
}
