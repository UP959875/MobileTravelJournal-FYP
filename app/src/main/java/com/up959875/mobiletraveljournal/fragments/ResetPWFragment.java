package com.up959875.mobiletraveljournal.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.R;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.up959875.mobiletraveljournal.databinding.FragmentResetPwBinding;
import com.up959875.mobiletraveljournal.other.FormHandler;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import com.up959875.mobiletraveljournal.other.Status;
import com.up959875.mobiletraveljournal.viewmodel.AuthViewModel;
import java.util.Objects;

public class ResetPWFragment extends BaseFragment implements View.OnClickListener {

    private FragmentResetPwBinding binding;
    private AuthViewModel authViewModel;

    static ResetPWFragment newInstance() {
        return new ResetPWFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResetPwBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setListeners();
        return view;
    }

    private void setListeners() {
        new FormHandler().addWatcher(binding.forgotPasswordEmailInput, binding.forgotPasswordEmailLayout);
        binding.forgotPwArrowButton.setOnClickListener(this);
        binding.forgotPasswordBackButton.setOnClickListener(this);
        binding.forgotPasswordSendButton.setOnClickListener(this);
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_pw_arrow_button:
            case R.id.forgot_password_back_button:
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0)
                    getFragmentManager().popBackStack();
                return;
            case R.id.forgot_password_send_button:
                if (validateEmail())
                    sendResetPasswordMail();
        }
    }

    private boolean validateEmail() {
        return new FormHandler().validateInput(binding.forgotPasswordEmailInput, binding.forgotPasswordEmailLayout);
    }

    private void startProgressBar() {
        binding.forgotPasswordProgressbarLayout.setVisibility(View.VISIBLE);
        binding.forgotPasswordProgressbar.start();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), false);
    }


    private void stopProgressBar() {
        binding.forgotPasswordProgressbarLayout.setVisibility(View.INVISIBLE);
        binding.forgotPasswordProgressbar.stop();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), true);
    }


    private void showSnackBar(String message, int duration) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, duration);
        snackbar.setAnchorView(Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_navigation_view));
        snackbar.show();
    }

    private void sendResetPasswordMail() {
        if (validateEmail()) {
            startProgressBar();
            String email = Objects.requireNonNull(binding.forgotPasswordEmailInput.getText()).toString();

            authViewModel.sendPasswordResetEmail(email);
            authViewModel.getUserForgotPasswordLiveData().observe(this, user -> {
                stopProgressBar();
                if (user.getError() == Status.SUCCESS) {
                    showSnackBar(user.getMessage(), Snackbar.LENGTH_LONG);
                    getParentFragmentManager().popBackStack();
                } else {
                    showSnackBar(user.getMessage(), Snackbar.LENGTH_LONG);
                }
            });
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
