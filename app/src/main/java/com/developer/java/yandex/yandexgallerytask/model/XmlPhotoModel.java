package com.developer.java.yandex.yandexgallerytask.model;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;

/**
 * Created by Anton on 26.04.2018.
 */

@Xml(name="rsp")
public class XmlPhotoModel {
    @Attribute
    String status;

    @Element
    public Photos photos;



    private class Photos {
        @Attribute
        int id;

        @Attribute
        int pages;

        @Attribute
        int perpage;

        @Attribute
        int total;

        @Element
        List<Photo> photo;
    }

    private class Photo{
        
    }
}
