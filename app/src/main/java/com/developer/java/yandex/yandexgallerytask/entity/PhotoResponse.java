package com.developer.java.yandex.yandexgallerytask.entity;


public class PhotoResponse {
    public String file;
    public String name;
    public String preview;
    public String path;
    public String mime_type;
    public String link;

    @Override
    public String toString() {
        return "PhotoResponse{" +
                "file='" + file + '\'' +
                ", name='" + name + '\'' +
                ", preview='" + preview + '\'' +
                ", path='" + path + '\'' +
                ", mime_type='" + mime_type + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
