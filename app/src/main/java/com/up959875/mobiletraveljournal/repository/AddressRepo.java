package com.up959875.mobiletraveljournal.repository;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.up959875.mobiletraveljournal.models.Address;
import com.up959875.mobiletraveljournal.other.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddressRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference addressesRef = rootRef.collection(Constants.ADDRESSES);

    public MutableLiveData<String> saveAddress(Address address, String reference) {
        MutableLiveData<String> statusData = new MutableLiveData<>();

        DocumentReference addressRef = reference == null ? addressesRef.document() : addressesRef.document(reference);
        if (address == null) address = new Address();
        address.setId(addressRef.getId());

        addressRef.set(address).addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                statusData.setValue(addressRef.getId());
            } else if (uidTask.getException() != null) {
                statusData.setValue("Error: " + uidTask.getException().getMessage());
            }
        });
        return statusData;
    }

    public MutableLiveData<Address> getAddress(String reference) {
        MutableLiveData<Address> addressData = new MutableLiveData<>();
        DocumentReference addressRef = addressesRef.document(reference);
        addressRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (document != null && document.exists()) {
                    Address address = document.toObject(Address.class);
                    addressData.setValue(address);
                }
            } else if (uidTask.getException() != null) {
                addressData.setValue(null);
            }
        });
        return addressData;
    }

    public MutableLiveData<FindCurrentPlaceResponse> detectAddress(PlacesClient placesClient, FindCurrentPlaceRequest request) {
        MutableLiveData<FindCurrentPlaceResponse> detectedAddress = new MutableLiveData<>();
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                detectedAddress.setValue(task.getResult());
                Log.d("addrepo", String.valueOf(task.getResult()));
            } else if(task.getException() != null) {
                detectedAddress.setValue(null);
                Log.d("addrepo", "null");
            }
        });
        return detectedAddress;
    }
}
