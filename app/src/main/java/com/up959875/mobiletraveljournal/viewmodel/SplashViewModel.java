package com.up959875.mobiletraveljournal.viewmodel;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.up959875.mobiletraveljournal.models.DataWrapper;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.repository.SplashRepo;

public class SplashViewModel extends AndroidViewModel {

    private SplashRepo splashRepo;
    private LiveData<DataWrapper<User>> isUserAuthLiveData;
    private LiveData<DataWrapper<User>> userLiveData;

    public SplashViewModel(Application application) {
        super(application);
        splashRepo = new SplashRepo();
    }

    public void checkCurrentUserAuth() {
        isUserAuthLiveData = splashRepo.checkUserIsAuth();
    }

    public void setUid(String uid) {
        userLiveData = splashRepo.getUserFromDatabase(uid);
    }

    public LiveData<DataWrapper<User>> getIsUserAuthLiveData() {
        return isUserAuthLiveData;
    }

    public void setIsUserAuthLiveData(LiveData<DataWrapper<User>> isUserAuthLiveData) {
        this.isUserAuthLiveData = isUserAuthLiveData;
    }

    public LiveData<DataWrapper<User>> getUserLiveData() {
        return userLiveData;
    }

    public void setUserLiveData(LiveData<DataWrapper<User>> userLiveData) {
        this.userLiveData = userLiveData;
    }

}
