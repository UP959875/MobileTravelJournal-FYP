package com.up959875.mobiletraveljournal.activities;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.viewmodel.SplashViewModel;
import static com.up959875.mobiletraveljournal.other.Constants.USER;

//Class to initiate the splash (starting) screen on startup
public class SplashActivity extends AppCompatActivity {

    //View model for the splash screen.
    private SplashViewModel splashViewModel;


    //Function called when view is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSplashViewModel();
        checkCurrentUserAuth();
    }


    //Initializes the view model from the class.
    private void initSplashViewModel() {
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
    }


    /**
     * Check if the user is authenticated, if they are, get the user from the database, if they aren't,
     * start the main activity and finish the current activity.
     */
    private void checkCurrentUserAuth() {
        splashViewModel.checkCurrentUserAuth();
        splashViewModel.getIsUserAuthLiveData().observe(this, user -> {
            if (user.isAuthenticated()) {
                getUserFromDatabase(user.getData().getUid());
            } else {
                startMainActivity(null);
                finish();
            }
        });
    }


    /**
     * Get the user from the database, and when you get it, start the main activity and finish this
     * activity.
     * 
     * @param uid The user's unique ID.
     */
    private void getUserFromDatabase(String uid) {
        splashViewModel.setUid(uid);
        splashViewModel.getUserLiveData().observe(this, user -> {
            startMainActivity(user.getData());
            finish();
        });
    }


    /**
     * this function takes a User object and puts it into an Intent object, which is then used to start the
     * MainActivity
     * 
     * @param user The user object that contains the user's information.
     */
    private void startMainActivity(User user) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
    }

}
