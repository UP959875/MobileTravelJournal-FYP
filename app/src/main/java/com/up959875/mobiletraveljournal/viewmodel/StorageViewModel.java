package com.up959875.mobiletraveljournal.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.up959875.mobiletraveljournal.repository.StorageRepo;
import android.app.Application;

public class StorageViewModel extends AndroidViewModel {

    private StorageRepo storageRepo;
    private MutableLiveData<String> statusLiveData;

    public StorageViewModel(Application application) {
        super(application);
        storageRepo = new StorageRepo();
    }

    public LiveData<String> getStorageStatus() {
        return statusLiveData;
    }

    public void saveToStorage(byte[] bytes, String userUid) {
        statusLiveData = storageRepo.saveToStorage(bytes, userUid);
    }

    public void saveToStorageRoute(byte[] bytes, String routeId) {
        statusLiveData = storageRepo.saveToStorageRoute(bytes, routeId);
    }
}
