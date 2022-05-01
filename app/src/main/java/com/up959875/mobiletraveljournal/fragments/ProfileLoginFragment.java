package com.up959875.mobiletraveljournal.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.widget.TextView;
import com.up959875.mobiletraveljournal.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.gms.common.SignInButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.up959875.mobiletraveljournal.databinding.FragmentProfileLoginBinding;
import androidx.fragment.app.Fragment;
import com.up959875.mobiletraveljournal.other.FormHandler;
import android.content.Intent;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.R;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
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
import com.up959875.mobiletraveljournal.models.DataWrapper;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.other.Status;
import com.up959875.mobiletraveljournal.other.Constants;
import com.up959875.mobiletraveljournal.viewmodel.AuthViewModel;
import java.util.Objects;
import com.up959875.mobiletraveljournal.other.GoogleService;

public class ProfileLoginFragment extends BaseFragment implements View.OnClickListener{

    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private TextInputLayout layoutInputEmail;
    private TextInputLayout layoutInputPassword;
    private TextView buttonForgotPassword;
    private MaterialButton buttonLogIn;
    private SignInButton buttonGoogleSignIn;
    private TextView buttonSignUp;
    private UserViewModel userViewModel;
    private FragmentProfileLoginBinding binding;
    private AuthViewModel authViewModel;
    private GoogleService googleService;


    public static ProfileLoginFragment newInstance() {

        return new ProfileLoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       binding = FragmentProfileLoginBinding.inflate(inflater, container, false);
       View view = binding.getRoot();

       initAuthViewModel();
       setListeners();
       initGoogleService();
       return view;
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    private void initGoogleService() {
        googleService = new GoogleService();
        googleService.initGoogleSignInClient(binding.getRoot().getContext());
    }




    private void setListeners() {

        new FormHandler().addWatcher(binding.loginEmailInput, binding.loginEmailLayout);
        new FormHandler().addWatcher(binding.loginPwInput, binding.loginPwLayout);
        binding.loginForgot.setOnClickListener(this);
        binding.loginLoginButton.setOnClickListener(this);
        binding.loginGoogleButton.setOnClickListener(this);
        binding.loginSignUpButton.setOnClickListener(this);
    }


    private boolean validateEmail() {
        return new FormHandler().validateInput(binding.loginEmailInput, binding.loginEmailLayout);
    }

    private void findViews(View view) {
        inputEmail = view.findViewById(R.id.login_email_input);
        layoutInputEmail = view.findViewById(R.id.login_email_layout);
        inputPassword = view.findViewById(R.id.login_pw_input);
        layoutInputPassword = view.findViewById(R.id.login_pw_layout);
        buttonForgotPassword = view.findViewById(R.id.login_forgot);
        buttonLogIn = view.findViewById(R.id.login_login_button);
        buttonGoogleSignIn = view.findViewById(R.id.login_google_button);
        buttonSignUp = view.findViewById(R.id.login_sign_up_button);
    }





    private boolean validatePassword() {


    return new FormHandler().validateInput(binding.loginPwInput, binding.loginPwLayout);
    }



    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void logInWithEmail() {
        if (validateEmail() && validatePassword()) {
            startProgressBar();

            String email = Objects.requireNonNull(binding.loginEmailInput.getText()).toString();
            String password = Objects.requireNonNull(binding.loginPwInput.getText()).toString();

            authViewModel.logInWithEmail(email, password);
            authViewModel.getUserLiveData().observe(this, user -> {
                if (user.getError() == Status.SUCCESS) {
                    if (user.isVerified() && !user.isAdded()) {
                        addNewUser(user);
                    } else if (!user.isVerified()){
                        showSnackBar("Error: this account is not verified. Please check your email", Snackbar.LENGTH_SHORT);
                        resendVerificationMail();
                    } else {
                        stopProgressBar();
                        showSnackBar(user.getMessage(), Snackbar.LENGTH_LONG);
                        getNavigationInteractions().changeNavigationBarItem(2, ProfileFragment.newInstance());
                    }
                } else {
                    stopProgressBar();
                    showSnackBar(user.getMessage(), Snackbar.LENGTH_LONG);
                }
            });
        }
    }

    private void resendVerificationMail() {
        authViewModel.sendVerificationMail();
        authViewModel.getUserVerificationLiveData().observe(this, verificationUser -> {
            stopProgressBar();
            if (verificationUser.getError() == Status.SUCCESS) {
                stopProgressBar();
                showSnackBar("A verification email has been sent. Check your email and verify your account to log in", Snackbar.LENGTH_LONG);
            } else {
                showSnackBar(verificationUser.getMessage(), Snackbar.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_button:
                logInWithEmail();
                return;
            case R.id.login_google_button:
                //Toast.makeText(getContext(), "Google sign in button", Toast.LENGTH_SHORT).show();
                logInWithGoogle();
                return;
            case R.id.login_forgot:
                changeFragment(ResetPWFragment.newInstance());
                return;
            case R.id.login_sign_up_button:
                //Toast.makeText(getContext(), "Sign up button", Toast.LENGTH_SHORT).show();

                changeFragment(SignUpFragment.newInstance());

        }
    }

    private void logInWithGoogle() {
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
                    getUserData(user);
                }
            } else {
                stopProgressBar();
                showSnackBar(user.getMessage(), Snackbar.LENGTH_LONG);
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


    private void addNewUser(DataWrapper<User> user) {
        authViewModel.addUser(user);
        authViewModel.getAddedUserLiveData().observe(this, newUser -> {
            if (newUser.getError() == Status.SUCCESS && newUser.isAdded()) {
                getUserData(newUser);
            } else {
                stopProgressBar();
                showSnackBar(newUser.getMessage(), Snackbar.LENGTH_LONG);
            }
        });
    }


    private void changeFragment(Fragment next) {
       clearInputs();
        getNavigationInteractions().changeFragment(this, next, true);
    }



    private void clearInputs() {
        new FormHandler().clearInput(binding.loginEmailInput, binding.loginEmailLayout);
        new FormHandler().clearInput(binding.loginPwInput, binding.loginPwLayout);
    }

    private void startProgressBar() {
        binding.loginProgressbarLayout.setVisibility(View.VISIBLE);
        binding.loginProgressbar.start();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), false);
    }


    private void stopProgressBar() {
        binding.loginProgressbarLayout.setVisibility(View.INVISIBLE);
        binding.loginProgressbar.stop();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), true);
    }


    private void showSnackBar(String message, int duration) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, duration);
        snackbar.setAnchorView(Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_navigation_view));
        TextView textView = snackbar.getView().findViewById(R.id.snackbar_text);
        textView.setMaxLines(3);
        snackbar.show();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
