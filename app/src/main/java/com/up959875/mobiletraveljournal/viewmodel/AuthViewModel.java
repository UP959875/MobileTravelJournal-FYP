package com.up959875.mobiletraveljournal.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.google.firebase.auth.AuthCredential;
import com.up959875.mobiletraveljournal.models.DataWrapper;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.repository.AuthRepo;


//Has getters and setters for the data associated with the authentication, and sends data to the AuthRepo to get info back.
public class AuthViewModel extends AndroidViewModel {

    private AuthRepo authRepo;
    private LiveData<DataWrapper<User>> userLiveData;
    private LiveData<DataWrapper<User>> addedUserLiveData;
    private LiveData<DataWrapper<User>> userVerificationLiveData;
    private LiveData<DataWrapper<User>> userForgotPasswordLiveData;
    private LiveData<String> statusChange;

    public AuthViewModel(Application application) {
        super(application);
        authRepo = new AuthRepo();
    }

    public void logInWithEmail(String email, String password) {
        userLiveData = authRepo.logInWithEmail(email, password);
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        userLiveData = authRepo.signInWithGoogle(googleAuthCredential);
    }

    public void signUpWithEmail(String email, String password, String username) {
        userLiveData = authRepo.signUpWithEmail(email, password, username);
    }

    public LiveData<DataWrapper<User>> getUserLiveData() {
        return userLiveData;
    }

    public void addUser(DataWrapper<User> user) {
        addedUserLiveData = authRepo.addUserToDatabase(user);
    }

    public LiveData<DataWrapper<User>> getAddedUserLiveData() {
        return addedUserLiveData;
    }

    public void sendVerificationMail() {
        userVerificationLiveData = authRepo.sendVerificationMail();}

    public LiveData<DataWrapper<User>> getUserVerificationLiveData() {
        return userVerificationLiveData;
    }

    public void changeUsername(String newUsername) {
        statusChange = authRepo.changeUsername(newUsername);
    }

    public void changeEmail(String currentPassword, String newEmail) {
        statusChange = authRepo.changeEmail(currentPassword, newEmail);
    }

    public void sendPasswordResetEmail(String email) {
        userForgotPasswordLiveData = authRepo.sendPasswordResetEmail(email);
    }

    public LiveData<DataWrapper<User>> getUserForgotPasswordLiveData() {
        return userForgotPasswordLiveData;
    }

    public void changePassword(String currentPassword, String newPassword) {
        statusChange = authRepo.changePassword(currentPassword, newPassword);
    }

    public LiveData<String> getChangesStatus() {
        return statusChange;
    }

    public void getUserFromDatabase(String uid) {
        userLiveData = authRepo.getUser(uid);
    }



}
