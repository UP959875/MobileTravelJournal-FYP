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


//Handles the address section of the database. gets the address through a snapshot and detects and address based on what is given.
public class AddressRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference addressesRef = rootRef.collection(Constants.ADDRESSES);

   /**
    * It saves an address to the database
    * 
    * @param address The address object to be saved.
    * @param reference The document reference of the address to be updated.
    * @return A MutableLiveData object.
    */
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

    /**
     * It returns a MutableLiveData object that contains an Address object
     * 
     * @param reference The reference of the address document in the database.
     * @return A MutableLiveData object that contains an Address object.
     */
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

    /**
     * It takes a PlacesClient and a FindCurrentPlaceRequest as parameters, and returns a
     * MutableLiveData of FindCurrentPlaceResponse
     * 
     * @param placesClient The PlacesClient object that you created in the previous step.
     * @param request FindCurrentPlaceRequest
     * @return A MutableLiveData object that contains a FindCurrentPlaceResponse object.
     */
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
