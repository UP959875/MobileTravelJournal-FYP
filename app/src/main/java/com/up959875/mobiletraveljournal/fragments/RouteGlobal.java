package com.up959875.mobiletraveljournal.fragments;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class RouteGlobal extends RouteFragment {


    private static RouteGlobal singleton;
    private List<Location> myLocations;


    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public RouteGlobal getInstance() {
        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
}
