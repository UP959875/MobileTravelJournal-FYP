package com.up959875.mobiletraveljournal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.other.FormHandler;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.databinding.FragmentSignupBinding;
import android.content.Intent;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.other.FormHandler;
import com.up959875.mobiletraveljournal.viewmodel.AuthViewModel;
import java.util.Objects;
import com.up959875.mobiletraveljournal.models.DataWrapper;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.other.Status;
import com.up959875.mobiletraveljournal.other.Constants;
import com.up959875.mobiletraveljournal.other.GoogleService;
import com.up959875.mobiletraveljournal.viewmodel.UserViewModel;

//Used when the user needs to sign up to the app for the first time. Involves them entering their username and password
public class SignUpFragment extends BaseFragment implements View.OnClickListener {

    private UserViewModel userViewModel;
    private FragmentSignupBinding binding;
    private AuthViewModel authViewModel;
    private GoogleService googleService;

    static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setListeners();
        initAuthViewModel();
        initGoogleService();

        return view;
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void initGoogleService() {
        googleService = new GoogleService();
        googleService.initGoogleSignInClient(binding.getRoot().getContext());
    }



    private void setListeners() {
        new FormHandler().addWatcher(binding.signupUsernameInput, binding.signupUsernameLayout);
        new FormHandler().addWatcher(binding.signupEmailInput, binding.signupEmailLayout);
        new FormHandler().addWatcher(binding.signupPasswordInput, binding.signupPasswordLayout);
        new FormHandler().addWatcher(binding.signupRepeatPasswordInput, binding.signupRepeatPasswordLayout);
        binding.signupArrowButton.setOnClickListener(this);
        binding.signupSignUpButton.setOnClickListener(this);
        binding.signupGoogleButton.setOnClickListener(this);
        binding.signupLogInButton.setOnClickListener(this);
    }

    private boolean validateEmail() {
        return new FormHandler().validateInput(binding.signupEmailInput, binding.signupEmailLayout);
    }


    private boolean validateUsername() {
        return new FormHandler().validateInput(binding.signupUsernameInput, binding.signupUsernameLayout);
    }


    private boolean validatePasswords() {
        return new FormHandler().validateInput(binding.signupPasswordInput, binding.signupPasswordLayout)
                && new FormHandler().validateInput(binding.signupRepeatPasswordInput, binding.signupRepeatPasswordLayout)
                && new FormHandler().validateInputsEquality(binding.signupPasswordInput, binding.signupRepeatPasswordInput, binding.signupRepeatPasswordLayout);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_arrow_button:
            case R.id.signup_log_in_button:
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0)
                    getFragmentManager().popBackStack();
                return;
            case R.id.signup_google_button:
                //Toast.makeText(getContext(), "Google button", Toast.LENGTH_SHORT).show();
                signUpWithGoogle();
                return;
            case R.id.signup_sign_up_button:
                signUpWithEmail();
        }
    }


    private void signUpWithEmail() {
        if (validateUsername() && validateEmail() && validatePasswords()) {
            startProgressBar();

            String email = Objects.requireNonNull(binding.signupEmailInput.getText()).toString();
            String username = Objects.requireNonNull(binding.signupUsernameInput.getText()).toString();
            String password = Objects.requireNonNull(binding.signupPasswordInput.getText()).toString();
            signUpWithEmailAuthCredential(email, password, username);
        }
    }


    private void signUpWithEmailAuthCredential(String email, String password, String username) {
        authViewModel.signUpWithEmail(email, password, username);
        authViewModel.getUserLiveData().observe(this, user -> {
            if (user.getError() == Status.LOADING) {
                sendVerificationMail();
            } else {
                stopProgressBar();
                showSnackBar(user.getMessage(), Snackbar.LENGTH_LONG);
            }
        });
    }

    private void sendVerificationMail() {
        authViewModel.sendVerificationMail();
        authViewModel.getUserVerificationLiveData().observe(this, verificationUser -> {
            stopProgressBar();
            if (verificationUser.getError() == Status.SUCCESS) {
                showSnackBar(verificationUser.getMessage(), Snackbar.LENGTH_LONG);
                getParentFragmentManager().popBackStack();
            } else {
                showSnackBar(verificationUser.getMessage(), Snackbar.LENGTH_LONG);
            }
        });
    }


    private void startProgressBar() {
        binding.signupProgressbarLayout.setVisibility(View.VISIBLE);
        binding.signupProgressbar.start();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), false);
    }


    private void stopProgressBar() {
        binding.signupProgressbarLayout.setVisibility(View.INVISIBLE);
        binding.signupProgressbar.stop();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), true);
    }


    private void showSnackBar(String message, int duration) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, duration);
        snackbar.setAnchorView(Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_navigation_view));
        snackbar.show();
    }


    private void signUpWithGoogle() {
        Intent signInIntent = googleService.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
        startProgressBar();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String status = googleService.getGoogleSignInAccount(requestCode, resultCode, data);
        if (status.equals(Constants.SUCCESS)) {
            getGoogleAuthCredential(googleService.getGoogleSignInAccount());
        } else {
            showSnackBar(status, Snackbar.LENGTH_LONG);
            stopProgressBar();
        }
    }


    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = googleService.getGoogleAuthCredential(googleSignInAccount);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }


    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential);
        authViewModel.getUserLiveData().observe(this, user -> {
            if (user.getError() == Status.SUCCESS) {
                if (!user.isAdded()) {
                    addNewUser(user);
                } else {
                    stopProgressBar();
                    showSnackBar(user.getMessage(), Snackbar.LENGTH_SHORT);

                }
            } else {
                showSnackBar(user.getMessage(), Snackbar.LENGTH_LONG);
                stopProgressBar();
                getNavigationInteractions().changeNavigationBarItem(2, ProfileFragment.newInstance());
            }
        });
    }

    private void addNewUser(DataWrapper<User> user) {
        authViewModel.addUser(user);
        authViewModel.getAddedUserLiveData().observe(this, newUser -> {
            if (newUser.getError() == Status.SUCCESS && newUser.isAdded()) {
                getUserData(user);
            } else {
                stopProgressBar();
                showSnackBar(newUser.getMessage(), Snackbar.LENGTH_LONG);
                getNavigationInteractions().changeNavigationBarItem(2, ProfileFragment.newInstance());
            }
        });
    }

    private void getUserData(DataWrapper<User> userData) {
        userViewModel.getUserData(userData.getData().getUid());
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            stopProgressBar();
            showSnackBar(userData.getMessage(), Snackbar.LENGTH_SHORT);
            getNavigationInteractions().changeNavigationBarItem(2, ProfileFragment.newInstance());
        });
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }



}
