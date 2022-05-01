package com.up959875.mobiletraveljournal.models;

import androidx.databinding.BaseObservable;

import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route extends BaseObservable implements Serializable {

    public String id;
    public String image;
    public int imageInt;
    public String title;
    public String desc;
    Map<String, Object> markerOptions = new HashMap<String, Object>();



    public Route(int imageInt, String title, String desc) {
        this.imageInt = imageInt;
        this.title = title;
        this.desc = desc;
    }

    public Route(String id, String image, String title, String desc, Map<String, Object> markerOptions) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.markerOptions = (markerOptions);
    }

    public Route() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageInt() {
        return imageInt;
    }

    public void setImageInt(int imageInt) {
        this.imageInt = imageInt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, Object> getMarkerOptions() {
        return markerOptions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;

    }

    public void setMarkerOptions() {
        this.markerOptions = markerOptions;
    }




}
