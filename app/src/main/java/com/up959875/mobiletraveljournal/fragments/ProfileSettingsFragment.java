package com.up959875.mobiletraveljournal.fragments;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.adapters.HashtagAdapter;
import com.up959875.mobiletraveljournal.databinding.FragmentProfileSettingsBinding;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.other.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.viewmodel.UserViewModel;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
import android.os.Build;
import android.content.Intent;
import android.graphics.Bitmap;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import static android.app.Activity.RESULT_OK;
import androidx.annotation.Nullable;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import androidx.core.content.ContextCompat;
import com.up959875.mobiletraveljournal.viewmodel.StorageViewModel;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import android.widget.EditText;
import java.io.File;
import java.io.IOException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import com.google.android.material.textfield.TextInputEditText;
import id.zelory.compressor.Compressor;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputLayout;
import com.up959875.mobiletraveljournal.other.FormHandler;
import com.up959875.mobiletraveljournal.viewmodel.AuthViewModel;

import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.up959875.mobiletraveljournal.models.Address;
import com.up959875.mobiletraveljournal.viewmodel.AddressViewModel;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import com.up959875.mobiletraveljournal.interfaces.BackPress;

//Fragment for the user's profile settings page. Allows them to change the bio, location, privacy settings, as well as change account details (password, etc.)
public class ProfileSettingsFragment extends BaseFragment implements View.OnClickListener, BackPress{

    private FragmentProfileSettingsBinding binding;
    private UserViewModel userViewModel;
    private User user;
    private Uri newImageUri;
    private Bitmap compressor;
    private AutocompleteSupportFragment autocompleteFragment;
    private AddressViewModel addressViewModel;
    private AuthViewModel authViewModel;
    private StorageViewModel storageViewModel;
    private FindCurrentPlaceRequest request;
    private PlacesClient placesClient;
    private Address newLocation;
    private Address currentLocation;

    static ProfileSettingsFragment newInstance() {
        return new ProfileSettingsFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initPrivacySelectItems();
        initGoogleSpots();
        setListeners();
        initUserViewModel();
        initUser();


        return view;
    }

    private void initPrivacySelectItems() {
        binding.profileSettingsPrivacyEmailSelect.setItems(Constants.PUBLIC, Constants.FRIENDS, Constants.ONLY_ME);
        binding.profileSettingsPrivacyLocationSelect.setItems(Constants.PUBLIC, Constants.FRIENDS, Constants.ONLY_ME);
        binding.profileSettingsPrivacyPreferencesSelect.setItems(Constants.PUBLIC, Constants.FRIENDS, Constants.ONLY_ME);
        List<String> prefs= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.prefs)));
        final HashtagAdapter adapter = new HashtagAdapter(getContext(), prefs);
        binding.profileSettingsPersonalPreferencesInput.setAdapter(adapter);
        binding.profileSettingsPersonalPreferencesInput.setThreshold(1);
    }


    private void setListeners() {
        binding.profileSettingsArrowButton.setOnClickListener(this);
        binding.profileSettingsPersonalPictureSelection.setOnClickListener(this);
        binding.profileSettingsPersonalLocationButton.setOnClickListener(this);
        binding.profileSettingsPersonalSaveButton.setOnClickListener(this);
        binding.profileSettingsAccountUsernameSaveButton.setOnClickListener(this);
        new FormHandler().addWatcher(binding.profileSettingsPersonalUsernameInput, binding.profileSettingsPersonalUsernameLayout);
        binding.profileSettingsAccountEmailSaveButton.setOnClickListener(this);
        new FormHandler().addWatcher(binding.profileSettingsAccountEmailPasswordCurrentInput, binding.profileSettingsAccountEmailLayout);
        new FormHandler().addWatcher(binding.profileSettingsAccountEmailInput, binding.profileSettingsAccountEmailWithpwLayout);
        new FormHandler().addWatcher(binding.profileSettingsAccountEmailConfirmInput, binding.profileSettingsAccountEmailConfirmLayout);

        binding.profileSettingsAccountPasswordSaveButton.setOnClickListener(this);
        new FormHandler().addWatcher(binding.profileSettingsAccountPasswordCurrentInput, binding.profileSettingsAccountPasswordCurrentLayout);
        new FormHandler().addWatcher(binding.profileSettingsAccountPasswordInput, binding.profileSettingsAccountPasswordLayout);
        new FormHandler().addWatcher(binding.profileSettingsAccountPasswordConfirmInput, binding.profileSettingsAccountPasswordConfirmLayout);

        binding.profileSettingsPrivacySaveButton.setOnClickListener(this);

        if (autocompleteFragment != null && autocompleteFragment.getView() != null) {
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    newLocation = new Address(place.getName(), place.getAddress(),
                            place.getLatLng().latitude, place.getLatLng().longitude, place.getAddressComponents());
                    Log.d("place select", newLocation.getAddress());
                    autocompleteFragment.setText(newLocation.getAddress());
                }

                @Override
                public void onError(@NonNull Status status) {
                }
            });
            autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(view -> {
                newLocation = null;
                autocompleteFragment.setText("");
            });
        }
    }


    private void initUserViewModel() {
        if (getActivity() != null) {
            userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
            authViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
            storageViewModel = new ViewModelProvider(getActivity()).get(StorageViewModel.class);
            addressViewModel = new ViewModelProvider(getActivity()).get(AddressViewModel.class);
        }
    }

    private void initUser() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable(Constants.USER);
            binding.setUser(user);
            initUserData();
        } else {
            getCurrentUser();
        }
    }

    private void getCurrentUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startProgressBar();
            userViewModel.getUserData(firebaseAuth.getCurrentUser().getUid());
            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    this.user = user;
                    binding.setUser(user);
                    initUserData();
                } else {
                    this.user = new User();
                    showSnackBar("ERROR: No such user in the database, try again later", Snackbar.LENGTH_LONG);
                }
                stopProgressBar();
            });
        } else {
            showSnackBar("ERROR: Current user is not available, try again later", Snackbar.LENGTH_LONG);
        }
    }

    private void initUserData() {
        int index = Objects.requireNonNull(user.getPrivacy().get(Constants.EMAIL));
        binding.profileSettingsPrivacyEmailSelect.setSelectedIndex(index);

        index = Objects.requireNonNull(user.getPrivacy().get(Constants.LOCATION));
        binding.profileSettingsPrivacyLocationSelect.setSelectedIndex(index);

        index = Objects.requireNonNull(user.getPrivacy().get(Constants.PREFERENCES));
        binding.profileSettingsPrivacyPreferencesSelect.setSelectedIndex(index);
    }

    private void initGoogleSpots() {
        if (getContext() != null) {
            Places.initialize(getContext(), getString(R.string.google_maps_key));
            Log.d("l213", "l213");
            placesClient  = Places.createClient(getContext());
            autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.profile_settings_personal_location_autocomplete);
            if (autocompleteFragment != null && autocompleteFragment.getView() != null) {
                ((EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input))
                        .setTextSize(14.0f);
                autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button)
                        .setVisibility(View.GONE);
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
                Log.d("l222", String.valueOf(autocompleteFragment));
                request = FindCurrentPlaceRequest.newInstance(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
                Log.d("l224", String.valueOf(Place.Field.NAME));
                //autocompleteFragment.setText(("aaa"));

            }
        }
    }

    private void initLocation() {
        if (user.getLocation() != null && !user.getLocation().equals("")) {
            startProgressBar();
            addressViewModel.getAddress(user.getLocation());
            addressViewModel.getAddressData().observe(getViewLifecycleOwner(), address -> {
                if (address != null) {
                    currentLocation = address;
                    currentLocation.setId(user.getLocation());
                    newLocation = currentLocation;
                    autocompleteFragment.setText(currentLocation.getAddress());
                }
                stopProgressBar();
            });
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_settings_arrow_button:
                if (!areAnyChanges()) {
                    //hideKeyboard();
                    if (getParentFragmentManager().getBackStackEntryCount() > 0)
                        getParentFragmentManager().popBackStack();
                } else showUnsavedChangesDialog();
                return;
            case R.id.profile_settings_personal_picture_selection:
                changeProfilePhoto();
                return;
            case R.id.profile_settings_personal_location_button:
                detectLocation();
                return;
            case R.id.profile_settings_personal_save_button:
                savePersonalChanges();
                return;
            case R.id.profile_settings_account_username_save_button:
                changeUsername();
                return;
            case R.id.profile_settings_account_email_save_button:
                changeEmail();
                return;
            case R.id.profile_settings_account_password_save_button:
                changePassword();
                return;
            case R.id.profile_settings_privacy_save_button:
                savePrivacyChanges();
        }
    }

    private void changeUsername() {
        if (validateUsername()) {
            if (isUsernameChanged()) {
                startProgressBar();
                String newUsername = Objects.requireNonNull(binding.profileSettingsPersonalUsernameInput.getText()).toString();
                authViewModel.changeUsername(newUsername);
                authViewModel.getChangesStatus().observe(this, status -> {
                    if (!status.contains("ERROR")) {
                        Map<String, Object> changes = new HashMap<>();
                        changes.put(Constants.USERNAME, newUsername);
                        updateUser(changes);
                    } else
                        showSnackBar(status, Snackbar.LENGTH_LONG);
                    stopProgressBar();
                });
            }
        }
    }


    private boolean validateUsername() {
        FormHandler formHandler = new FormHandler();
        TextInputEditText input = binding.profileSettingsPersonalUsernameInput;
        TextInputLayout layout = binding.profileSettingsPersonalUsernameLayout;
        int minLength = 4;

        return formHandler.validateInput(input, layout);
    }

    private void changeEmail() {
        if(validateChangeEmail()) {
            if (!user.getEmail().equals(Objects.requireNonNull(binding.profileSettingsAccountEmailInput.getText()).toString())) {
                String currentPassword = Objects.requireNonNull(binding.profileSettingsAccountEmailPasswordCurrentInput.getText()).toString();
                String newEmail = Objects.requireNonNull(binding.profileSettingsAccountEmailInput.getText()).toString();
                authViewModel.changeEmail(currentPassword, newEmail);
                authViewModel.getChangesStatus().observe(this, status -> {
                    if (!status.contains("ERROR")) {
                        Map<String, Object> changes = new HashMap<>();
                        changes.put(Constants.EMAIL, newEmail);
                        updateUser(changes);
                    } else
                        showSnackBar(status, Snackbar.LENGTH_LONG);
                    stopProgressBar();
                });
            } else {
                showSnackBar("ERROR: you cannot use the same email", Snackbar.LENGTH_LONG);
            }
        }
    }


    private boolean validateChangeEmail() {
        FormHandler formHandler = new FormHandler();
        TextInputEditText currentPasswordInput = binding.profileSettingsAccountEmailPasswordCurrentInput;
        TextInputEditText newEmailInput = binding.profileSettingsAccountEmailInput;
        TextInputEditText confirmEmailInput = binding.profileSettingsAccountEmailConfirmInput;
        TextInputLayout currentPasswordLayout = binding.profileSettingsAccountEmailLayout;
        TextInputLayout newEmailLayout = binding.profileSettingsAccountEmailWithpwLayout;
        TextInputLayout confirmEmailLayout = binding.profileSettingsAccountEmailConfirmLayout;

        return formHandler.validateInput(currentPasswordInput, currentPasswordLayout)
                && formHandler.validateInput(newEmailInput, newEmailLayout)
                && formHandler.validateInput(confirmEmailInput, confirmEmailLayout)
                && formHandler.validateInputsEquality(newEmailInput, confirmEmailInput, newEmailLayout);
    }

    private void changeProfilePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity() != null && getContext() != null) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.RC_EXTERNAL_STORAGE);
            } else {
                selectImage();
            }
        } else {
            selectImage();
        }
    }

    private void selectImage() {
        if (getActivity() != null && getContext() != null) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }
    }


    private void savePersonalChanges() {
        Map<String, Object> changes = new HashMap<>();
        if (isBioChanged()) {
            changes.put(Constants.BIO, Objects.requireNonNull(binding.profileSettingsPersonalBioInput.getText()).toString());
        }
        if(isPreferenceChanged()) {
            changes.put(Constants.PREFERENCES, getUniquePrefs());
        }
        if (isImageChanged()) {
            savePhotoToStorage(changes);
        } else if (!changes.isEmpty()) {

            updateUser(changes);
        }

        if (isLocationChanged()) {
            addAddress();
        }

    }


    private void savePrivacyChanges() {
        Map<String, Object> changes = new HashMap<>();
        if (isPrivacyEmailChanged()) {
            changes.put(Constants.PRIVACY + "." + Constants.EMAIL, binding.profileSettingsPrivacyEmailSelect.getSelectedIndex());
        }
        if (isPrivacyLocationChanged()) {
            changes.put(Constants.PRIVACY + "." + Constants.LOCATION, binding.profileSettingsPrivacyLocationSelect.getSelectedIndex());
        }
        if (isPrivacyPreferencesChanged()) {
            changes.put(Constants.PRIVACY + "." + Constants.PREFERENCES, binding.profileSettingsPrivacyPreferencesSelect.getSelectedIndex());
        }

        if (!changes.isEmpty()) {
            updateUser(changes);
        }
    }

    private void savePhotoToStorage(Map<String, Object> changes) {
        startProgressBar();
        if (newImageUri.getPath() != null && getContext() != null) {
            File folder = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), newImageUri.getLastPathSegment());
            folder.mkdir();
            Log.d("ok", String.valueOf(folder.mkdir()));
            folder.mkdirs();
            Log.d("ok", String.valueOf(folder.mkdirs()));
            newImageUri.getLastPathSegment();
            Log.d("ok", newImageUri.getLastPathSegment());
            //File newFile = new File(folder, newImageUri.getPath());
            //Log.d("ok", String.valueOf(newFile));

            try {
                compressor = new Compressor(getContext())
                        .setMaxHeight(150)
                        .setMaxWidth(150)
                        .setQuality(100)
                        .compressToBitmap(folder);
            } catch (IOException e) {
                showSnackBar("ERROR: " + e.getMessage(), Snackbar.LENGTH_LONG);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Log.d("ok", String.valueOf(byteArrayOutputStream));
            compressor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] thumb = byteArrayOutputStream.toByteArray();

            storageViewModel.saveToStorage(thumb, user.getUid());
            storageViewModel.getStorageStatus().observe(getViewLifecycleOwner(), status -> {
                if(status.contains("ERROR")) {
                    showSnackBar(status, Snackbar.LENGTH_LONG);
                    stopProgressBar();
                } else {
                    changes.put(Constants.PHOTO, status);
                    updateUser(changes);
                }
            });
        }
    }


    private void updateUser(Map<String, Object> changes) {
        startProgressBar();
        userViewModel.updateUser(user, changes);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                userViewModel.setUser(user);
                binding.setUser(user);
                showSnackBar("Changes saved successfully", Snackbar.LENGTH_LONG);
                newImageUri = null;
            } else {
                showSnackBar("ERROR: Failed to update, try again later", Snackbar.LENGTH_LONG);
            }
            stopProgressBar();
        });
    }

    private void changePassword() {
        if(validatePasswords()) {
            startProgressBar();
            String currentPassword = Objects.requireNonNull(binding.profileSettingsAccountPasswordCurrentInput.getText()).toString();
            String newPassword = Objects.requireNonNull(binding.profileSettingsAccountPasswordInput.getText()).toString();
            authViewModel.changePassword(currentPassword, newPassword);
            authViewModel.getChangesStatus().observe(this, status -> {
                if (!status.contains("ERROR")) {
                    clearInputs();
                }
                showSnackBar(status, Snackbar.LENGTH_LONG);
                stopProgressBar();
            });
        }
    }

    private void addAddress() {
        startProgressBar();
        addressViewModel.saveAddress(newLocation, user.getLocation());
        addressViewModel.getStatus().observe(getViewLifecycleOwner(), status -> {
            if (!status.contains("ERROR")) {
                Map<String, Object> changes = new HashMap<>();
                changes.put(Constants.LOCATION, status);
                Log.d("cha", String.valueOf(changes));
                updateUser(changes);
                if (newLocation == null) newLocation = new Address(status);
                currentLocation = newLocation;
                if (currentLocation.getName() != null)
                    autocompleteFragment.setText(currentLocation.getAddress());
            } else {
                showSnackBar(status, Snackbar.LENGTH_LONG);
                stopProgressBar();
            }
        });
    }



    private void clearInputs() {
        new FormHandler().clearInput(binding.profileSettingsAccountPasswordCurrentInput, binding.profileSettingsAccountPasswordCurrentLayout);
        new FormHandler().clearInput(binding.profileSettingsAccountPasswordInput, binding.profileSettingsAccountPasswordLayout);
        new FormHandler().clearInput(binding.profileSettingsAccountPasswordConfirmInput, binding.profileSettingsAccountPasswordConfirmLayout);
    }


    private boolean validatePasswords() {
        FormHandler formHandler = new FormHandler();
        TextInputEditText currentInput = binding.profileSettingsAccountPasswordCurrentInput;
        TextInputEditText passInput = binding.profileSettingsAccountPasswordInput;
        TextInputEditText confirmInput = binding.profileSettingsAccountPasswordConfirmInput;
        TextInputLayout currentLayout = binding.profileSettingsAccountPasswordCurrentLayout;
        TextInputLayout passLayout = binding.profileSettingsAccountPasswordLayout;
        TextInputLayout confirmLayout = binding.profileSettingsAccountPasswordConfirmLayout;
        int minLength = 8;

        return formHandler.validateInput(currentInput, currentLayout)
                && formHandler.validateInput(passInput, passLayout)
                && formHandler.validateInput(confirmInput, confirmLayout)
                && formHandler.validateInputsEquality(passInput, confirmInput, confirmLayout);
    }

    private void showUnsavedChangesDialog() {
        if (getContext() != null && getActivity() != null) {
            final AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                    .setTitle(getString(R.string.dialog_button_unsaved_changes_title))
                    .setMessage(getString(R.string.dialog_button_unsaved_changes_desc))
                    .setPositiveButton(getString(R.string.dialog_button_yes), (dialogInterface, i) -> {
                        //hideKeyboard();
                        dialogInterface.cancel();
                        if (getParentFragmentManager().getBackStackEntryCount() > 0)
                            getParentFragmentManager().popBackStack();
                    })
                    .setNegativeButton(getString(R.string.dialog_button_no), null)
                    .show();
            ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void detectLocation() {
        if(getContext() != null) {
            if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startProgressBar();
                addressViewModel.detectAddress(placesClient, request);
                addressViewModel.getDetectedAddress().observe(getViewLifecycleOwner(), response -> {
                    if (response != null) {
                        Place place = response.getPlaceLikelihoods().get(0).getPlace();
                        double lat = place.getLatLng() != null ? place.getLatLng().latitude : 0;
                        double lon = place.getLatLng() != null ? place.getLatLng().longitude : 0;
                        newLocation = new Address(place.getName(), place.getAddress(), lat, lon, place.getAddressComponents());
                        //Log.d("ok111", newLocation.getAddressList());
                        autocompleteFragment.setText(newLocation.getAddress());
                        stopProgressBar();
                    } else
                        showSnackBar("ERROR: Cannot detect location", Snackbar.LENGTH_LONG);
                });
            } else if (getActivity() != null){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 8008);
            }
        }
    }




    private boolean isImageChanged() {
        return newImageUri != null;
    }


    private boolean isUsernameChanged() {
        return !user.getUsername().equals(Objects.requireNonNull(binding.profileSettingsPersonalUsernameInput.getText()).toString());
    }


    private boolean isBioChanged() {
        if (binding.profileSettingsPersonalBioInput.getText() != null) {
            return (user.getBio() == null && !binding.profileSettingsPersonalBioInput.getText().toString().equals(""))
                    || (user.getBio() != null && !user.getBio().equals(binding.profileSettingsPersonalBioInput.getText().toString()));
        }
        return false;
    }


    private boolean isPreferenceChanged() {
        if (binding.profileSettingsPersonalPreferencesInput.getText() != null) {
            return (user.getPrefs() == null && !binding.profileSettingsPersonalPreferencesInput.getText().toString().equals(""))
                    || (user.getPrefs() != null && !user.getPrefs().equals(getUniquePrefs()));
        }
        return false;
    }


    private boolean isEmailChanged() {
        if (binding.profileSettingsAccountEmailPasswordCurrentInput.getText() != null
                && binding.profileSettingsAccountEmailInput.getText() != null
                && binding.profileSettingsAccountEmailConfirmInput.getText() != null) {
            return !binding.profileSettingsAccountEmailPasswordCurrentInput.getText().toString().equals("")
                    || !binding.profileSettingsAccountEmailInput.getText().toString().equals("")
                    || !binding.profileSettingsAccountEmailConfirmInput.getText().toString().equals("");
        }
        return false;
    }


    private boolean isPasswordChanged() {
        if (binding.profileSettingsAccountPasswordCurrentInput.getText() != null
                && binding.profileSettingsAccountPasswordInput.getText() != null
                && binding.profileSettingsAccountPasswordConfirmInput.getText() != null) {
            return !binding.profileSettingsAccountPasswordCurrentInput.getText().toString().equals("")
                    || !binding.profileSettingsAccountPasswordInput.getText().toString().equals("")
                    || !binding.profileSettingsAccountPasswordConfirmInput.getText().toString().equals("");
        }
        return false;
    }


    @SuppressWarnings("ConstantConditions")
    private boolean isPrivacyEmailChanged() {
        return user.getPrivacy().get(Constants.EMAIL) != binding.profileSettingsPrivacyEmailSelect.getSelectedIndex();
    }


    @SuppressWarnings("ConstantConditions")
    private boolean isPrivacyLocationChanged() {
        return user.getPrivacy().get(Constants.LOCATION) != binding.profileSettingsPrivacyLocationSelect.getSelectedIndex();
    }


    @SuppressWarnings("ConstantConditions")
    private boolean isPrivacyPreferencesChanged() {
        return user.getPrivacy().get(Constants.PREFERENCES) != binding.profileSettingsPrivacyPreferencesSelect.getSelectedIndex();
    }


    private boolean isPrivacyChanged() {
        return isPrivacyEmailChanged() || isPrivacyLocationChanged() || isPrivacyPreferencesChanged();
    }

    private boolean isLocationChanged() {
        if (autocompleteFragment != null) {
            return (user.getLocation() == null && newLocation != null)
                    || (user.getLocation() != null && currentLocation!= null && newLocation != null && !currentLocation.equals(newLocation))
                    || (user.getLocation() != null && newLocation == null);
        }
        return false;
    }


    private boolean areAnyChanges() {
        return (isImageChanged() || isPreferenceChanged() || isBioChanged() || isUsernameChanged()
                || isEmailChanged() || isPasswordChanged() || isPrivacyChanged() || isLocationChanged());
    }



    private void startProgressBar() {

    }


    private void stopProgressBar() {

    }


    private void showSnackBar(String message, int duration) {

    }

    private List<String> getUniquePrefs() {
        List<String> preferences = binding.profileSettingsPersonalPreferencesInput.getChipValues();
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(preferences);
        return new ArrayList<>(hashSet);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.d("ok", String.valueOf(result));
            if (resultCode == RESULT_OK && result != null) {
                newImageUri = result.getUri();
                        //result.uriContent();
                User.loadImage(binding.profileSettingsPersonalPictureSelection, newImageUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE && result != null) {
                //showSnackBar(result.getError().getMessage(), Snackbar.LENGTH_LONG);

            }
        }
    }



    @Override
    public boolean whenBackPressed() {
        if (areAnyChanges()) {
            showUnsavedChangesDialog();
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}
