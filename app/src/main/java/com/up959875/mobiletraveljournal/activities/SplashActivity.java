package com.up959875.mobiletraveljournal.activities;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.viewmodel.SplashViewModel;
import static com.up959875.mobiletraveljournal.other.Constants.USER;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel splashViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSplashViewModel();
        checkCurrentUserAuth();
    }


    private void initSplashViewModel() {
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
    }


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


    private void getUserFromDatabase(String uid) {
        splashViewModel.setUid(uid);
        splashViewModel.getUserLiveData().observe(this, user -> {
            startMainActivity(user.getData());
            finish();
        });
    }


    private void startMainActivity(User user) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
    }

}
