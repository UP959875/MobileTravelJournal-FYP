package com.up959875.mobiletraveljournal.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.Application;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.up959875.mobiletraveljournal.models.Address;
import com.up959875.mobiletraveljournal.models.Route;
import com.up959875.mobiletraveljournal.repository.AddressRepo;
import com.up959875.mobiletraveljournal.repository.RouteRepo;


public class RouteViewModel extends AndroidViewModel {

    private RouteRepo routeRepo;
    private MutableLiveData<String> statusLiveData;
    private MutableLiveData<Route> routeLiveData;

    public RouteViewModel(Application application){
        super(application);
        routeRepo = new RouteRepo();
    }

    public LiveData<Route> getRouteData(){
        return routeLiveData;
    }

    public void getRoute(String reference) {
        routeLiveData = routeRepo.getRoute(reference);
    }

    public LiveData<String> getStatus() {
        return statusLiveData;
    }

    public void saveRoute(Route route, String reference){
        statusLiveData = routeRepo.saveRoute(route, reference);
    }

}
