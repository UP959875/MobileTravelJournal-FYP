package com.up959875.mobiletraveljournal.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.Application;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.up959875.mobiletraveljournal.models.Address;
import com.up959875.mobiletraveljournal.repository.AddressRepo;
import com.up959875.mobiletraveljournal.repository.StorageRepo;

public class AddressViewModel extends AndroidViewModel {


    private AddressRepo addressRepo;
    private MutableLiveData<String> statusLiveData;
    private MutableLiveData<Address> addressLiveData;
    private MutableLiveData<FindCurrentPlaceResponse> detectedAddressLiveData;

    public AddressViewModel(Application application) {
        super(application);
        addressRepo = new AddressRepo();
    }

    public void saveAddress(Address address, String reference) {
        statusLiveData = addressRepo.saveAddress(address, reference);
    }

    public LiveData<Address> getAddressData() {
        return addressLiveData;
    }

    public void getAddress(String reference) {
        addressLiveData = addressRepo.getAddress(reference);
    }

    public LiveData<String> getStatus() {
        return statusLiveData;
    }

    public LiveData<FindCurrentPlaceResponse> getDetectedAddress() {
        return detectedAddressLiveData;
    }

    public void detectAddress(PlacesClient placesClient, FindCurrentPlaceRequest request) {
        detectedAddressLiveData = addressRepo.detectAddress(placesClient, request);
    }

}
