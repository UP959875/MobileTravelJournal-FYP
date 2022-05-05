package com.up959875.mobiletraveljournal.repository;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.lifecycle.MutableLiveData;

//Handles saving data locally, related to saving photos.
public class StorageRepo {

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    /**
     * It takes a byte array and a userUid, and returns a MutableLiveData object that will eventually
     * contain a string that is either the URL of the image that was uploaded, or an error message
     * 
     * @param bytes The byte array of the image you want to upload.
     * @param userUid The user's unique ID.
     * @return A MutableLiveData object.
     */
    public MutableLiveData<String> saveToStorage(byte[] bytes, String userUid) {
        MutableLiveData<String> statusData = new MutableLiveData<>();
        StorageReference ref = storageReference.child("images/profiles").child(userUid + ".jpg");
        ref.putBytes(bytes).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ref.getDownloadUrl().addOnCompleteListener(uri -> {
                    if (uri.isSuccessful() && uri.getResult() != null) {
                        statusData.setValue(uri.getResult().toString());
                    } else if (uri.getException() != null){
                        statusData.setValue("ERROR: " + uri.getException().getMessage());
                    }
                });
            } else if (task.getException() != null){
                statusData.setValue("ERROR: " + task.getException().getMessage());
            }
        });
        return statusData;
    }

    /**
     * It takes a byte array and a string, uploads the byte array to Firebase Storage, and returns a
     * MutableLiveData object containing the URL of the uploaded image
     * 
     * @param bytes the byte array of the image
     * @param imageUid a unique identifier for the image
     * @return A MutableLiveData object.
     */
    public MutableLiveData<String> saveToStorageRoute(byte[] bytes, String imageUid) {
        MutableLiveData<String> statusData = new MutableLiveData<>();
        StorageReference ref = storageReference.child("images/route").child(imageUid + ".jpg");
        ref.putBytes(bytes).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ref.getDownloadUrl().addOnCompleteListener(uri -> {
                    if (uri.isSuccessful() && uri.getResult() != null) {
                        statusData.setValue(uri.getResult().toString());
                    } else if (uri.getException() != null){
                        statusData.setValue("ERROR: " + uri.getException().getMessage());
                    }
                });
            } else if (task.getException() != null){
                statusData.setValue("ERROR: " + task.getException().getMessage());
            }
        });
        return statusData;
    }

}
