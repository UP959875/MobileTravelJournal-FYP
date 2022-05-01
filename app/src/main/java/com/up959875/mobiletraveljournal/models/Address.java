package com.up959875.mobiletraveljournal.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import java.io.Serializable;
import com.google.android.libraries.places.api.model.AddressComponents;
//import com.up959875.mobiletraveljournal.BR;

public class Address extends BaseObservable implements Serializable {

    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;



    private AddressComponents addressComponents;

    public Address() {
    }

    public Address(String id) {
        this.id = id;
    }

    public Address(String name, String address, double latitude, double longitude, AddressComponents addressComponents) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressComponents = addressComponents;
    }

    public Address(String id, String name, String address, double latitude, double longitude, AddressComponents addressComponents) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressComponents = addressComponents;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        //notifyPropertyChanged(BR.name);
    }

    @Bindable
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        //notifyPropertyChanged(BR.latitude);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        //notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        //notifyPropertyChanged(BR.id);
    }

    @Bindable
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        //notifyPropertyChanged(BR.longitude);
    }

    public AddressComponents getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(AddressComponents addressComponents) {
        this.addressComponents = addressComponents;
    }



}
