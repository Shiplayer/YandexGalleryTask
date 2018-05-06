package com.developer.java.yandex.yandexgallerytask.model;

public class ResponseModel<T>{
    private String mError;
    private T mResponse;
    private boolean mCheckError;

    public ResponseModel(T response){
        mResponse = response;
        mCheckError = false;
    }

    public ResponseModel(T response, String errorMessage){
        mResponse = response;
        mError = errorMessage;
        mCheckError = true;
    }

    public boolean isSuccessful(){
        return !mCheckError;
    }

    public String getError() {
        return mError;
    }

    public void setError(String error){
        mError = error;
        mCheckError = true;
    }

    public T getResponse() {
        return mResponse;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "mError='" + mError + '\'' +
                ", mResponse='" + (mResponse != null) + '\'' +
                ", mCheckError=" + mCheckError +
                '}';
    }
}
