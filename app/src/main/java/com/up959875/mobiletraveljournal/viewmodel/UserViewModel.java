package com.up959875.mobiletraveljournal.viewmodel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.repository.UserRepo;
import java.util.Map;

public class UserViewModel extends AndroidViewModel {

    private UserRepo userRepo;
    private MutableLiveData<User> userLiveData;
    private final MutableLiveData<User> user;
    private LiveData<List<User>> usersListLiveData;

    public UserViewModel(Application application) {
        super(application);
        userRepo = new UserRepo();
        user = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void getUserData (String uid) {
        userLiveData = userRepo.getUserData(uid);
    }

    public void updateUser(User user, Map<String, Object> map) {
        userRepo.updateUser(user, map);
        userLiveData = userRepo.getUserData(user.getUid());
    }

    public void getUsersListData(List<String> usersIds) {
        usersListLiveData = userRepo.getUsers(usersIds);
    }

    public LiveData<List<User>> getUsersList() {
        return usersListLiveData;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public LiveData<User> getUser() {
        return user;
    }




}
