package com.up959875.mobiletraveljournal.models;

import java.io.Serializable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.databinding.BaseObservable;
import androidx.annotation.Nullable;

//Marker model class, contains all constructors and initialises variables for all the data.
//Contains different constructors for different uses, and getters and setters for all variables.
public class Markers extends BaseObservable implements Serializable {

    private double latitude;
    private double longitude;
    private String description;

    public Markers() {
    }

    public Markers(double latitude, double longitude, String description){
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    @Bindable
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        notifyPropertyChanged(BR.latitude);
    }

    @Bindable
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        notifyPropertyChanged(BR.longitude);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * If the object is not null and the class is the same, then check if the description is not null
     * and if it is equal to the description of the object, or if the description is null and the
     * description of the object is null, and if the latitude and longitude are equal, then return true
     * 
     * @param obj The object to compare this instance with.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            if (obj == null || getClass() != obj.getClass())
                return false;

            Markers m = (Markers) obj;
            return (description != null && (description.equals(m.description)) || (description == null && m.description == null))
                    && (latitude == m.latitude)
                    && (longitude == m.longitude);
        } catch (Exception ex) {
            return false;
        }
    }
}
