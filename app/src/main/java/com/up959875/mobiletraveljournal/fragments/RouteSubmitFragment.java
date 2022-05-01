package com.up959875.mobiletraveljournal.fragments;

import static android.app.Activity.RESULT_OK;
import static com.up959875.mobiletraveljournal.fragments.MapsActivity.fMarker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.app.Activity;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.databinding.FragmentProfileSettingsBinding;
import com.up959875.mobiletraveljournal.models.Route;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.other.Constants;
import com.up959875.mobiletraveljournal.viewmodel.RouteViewModel;
import com.up959875.mobiletraveljournal.databinding.FragmentRouteSubmitBinding;
import com.up959875.mobiletraveljournal.viewmodel.StorageViewModel;
import com.up959875.mobiletraveljournal.viewmodel.UserViewModel;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class RouteSubmitFragment extends BaseFragment implements View.OnClickListener {



    ListView lv_savedLocationsSubmit;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference routesRef = rootRef.collection(Constants.ROUTES);
    private Bitmap compressor;
    private RouteViewModel routeViewModel;
    private UserViewModel userViewModel;
    private StorageViewModel storageViewModel;
    private User user;
    private Route completeRoute;
    private FragmentRouteSubmitBinding binding;
    private Uri file;
    DocumentReference routeUpdate;
    public String photoLink = "link";
    public static RouteSubmitFragment newInstance() {
        return new RouteSubmitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRouteSubmitBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //lv_savedLocationsSubmit = (ListView) view.findViewById(R.id.lv_waypoints);
        //lv_savedLocationsSubmit.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Collections.singletonList(fMarker.get(0).getSnippet())));
        Log.d("l59", "initview");
        setListeners();
        initUserViewModel();
        initLoggedInUser();
        //submitAddress();
        return view;

    }

    private void setListeners() {
        binding.routeSubmitSaveButton.setOnClickListener(this);
        binding.routeSubmitPictureSelection.setOnClickListener(this);


    }

    private void initUserViewModel() {
        if (RouteSubmitFragment.this != null) {
            routeViewModel = new ViewModelProvider(RouteSubmitFragment.this).get(RouteViewModel.class);
            storageViewModel = new ViewModelProvider(getActivity()).get(StorageViewModel.class);
            userViewModel = new ViewModelProvider(RouteSubmitFragment.this).get(UserViewModel.class);
        }
    }

    private void submitAddress() {
        routesRef.document();
        routeViewModel.saveRoute(completeRoute, routesRef.document().getId());
        routeViewModel.getStatus().observe(getViewLifecycleOwner(), status -> {
            Map<String, Object> changes = new HashMap<>();
            changes.put(Constants.ROUTE, status);
            Log.d("cha", String.valueOf(changes));
            String idToBeUpdated = String.valueOf(changes);
            String idUpdated1 = idToBeUpdated.replace("{Route=", "");
            String idUpdated2 = idUpdated1.replace("}", "");
            Log.d("cha1.5", idUpdated2);
            Log.d("cha2", String.valueOf(fMarker));
            Log.d("cha3", routesRef.document().getId());
            //updateUser(changes);
            routeUpdate = rootRef.collection(Constants.ROUTES).document(idUpdated2);//.collection("markerOptions").document("LatLng");
            if(!status.contains("Error")) {
                for (int i = 0; i < (fMarker.size()); i++){
                    Log.d("size", String.valueOf(fMarker.size()));

                //routeUpdate.c
                int itest = 1;
                Log.d("cha4", (routeUpdate.getPath()));
                LatLng myLatLng = new LatLng(fMarker.get(i).getPosition().latitude, fMarker.get(i).getPosition().longitude);
                routeUpdate.update("markerOptions.marker" + i + ".LatLng", myLatLng);
                routeUpdate.update("markerOptions.marker" + i + ".snippet", Collections.singletonList(fMarker.get(i).getSnippet()));
                routeUpdate.update("desc", Objects.requireNonNull(binding.routeSubmitDescInput.getText().toString()));
                routeUpdate.update("title", Objects.requireNonNull(binding.routeSubmitTitleInput.getText().toString()));
                savePhotoToStorage(changes);


                //routeUpdate.
                //routeUpdate.update("markerOptions.LatLng", Collections.singletonList(fMarker.get(0).getSnippet()));

                //new HashMap<String, Object>(){{put(Constants.ROUTES, fMarker);}}
            }}

        });
    }

    private void savePhotoToStorage(Map<String, Object> changes) {
        //startProgressBar();

            //file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

                    //
            File folder = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), file.getLastPathSegment());
            folder.mkdir();
            Log.d("ok", String.valueOf(folder.mkdir()));
            folder.mkdirs();
            Log.d("ok", String.valueOf(folder.mkdirs()));
            file.getLastPathSegment();
            Log.d("ok", file.getLastPathSegment());
            //File newFile = new File(folder, newImageUri.getPath());
            //Log.d("ok", String.valueOf(newFile));

            try {
                compressor = new Compressor(getContext())
                        .setMaxHeight(150)
                        .setMaxWidth(150)
                        .setQuality(100)
                        .compressToBitmap(folder);
            } catch (IOException e) {
                //showSnackBar("ERROR: " + e.getMessage(), Snackbar.LENGTH_LONG);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Log.d("ok", String.valueOf(byteArrayOutputStream));
            compressor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] thumb = byteArrayOutputStream.toByteArray();

            storageViewModel.saveToStorageRoute(thumb, String.valueOf(getId()));
            Log.d("svm", String.valueOf(getId()));
                    //saveToStorageRoute(thumb, user.getUid());
            storageViewModel.getStorageStatus().observe(getViewLifecycleOwner(), status -> {
                if(status.contains("ERROR")) {
                    //showSnackBar(status, Snackbar.LENGTH_LONG);
                    //stopProgressBar();
                } else {
                    changes.put(Constants.PHOTO, status);
                    photoLink = status;
                    Log.d("photolink", photoLink);
                    routeUpdate.update("image", photoLink);
                }

            });

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.d("ok", String.valueOf(result));
            if (resultCode == RESULT_OK && result != null) {
                file = result.getUri();
                //result.uriContent();
                User.loadImage(binding.routeSubmitPictureSelection, file.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE && result != null) {
                //showSnackBar(result.getError().getMessage(), Snackbar.LENGTH_LONG);

            }
        }
    }

    private void initLoggedInUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {

            userViewModel.getUserData(firebaseAuth.getCurrentUser().getUid());
            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    this.user = user;
                } else {
                    //showSnackBar(getResources().getString(R.string.message_user_not_available), Snackbar.LENGTH_LONG);
                    Log.d("l63", String.valueOf(R.string.message_user_not_available));
                }
            });
        }
    }

    private void updateUser(Map<String, Object> changes) {
        userViewModel.updateUser(user, changes);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                userViewModel.setUser(user);

            } else {
            }

        });
    }

    private void changePicture() {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.route_submit_save_button:
                submitAddress();
                return;
            case R.id.route_submit_picture_selection:
                changePicture();
                return;
        }

    }
}
