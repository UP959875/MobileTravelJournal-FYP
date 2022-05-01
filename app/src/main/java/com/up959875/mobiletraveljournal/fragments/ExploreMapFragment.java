package com.up959875.mobiletraveljournal.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.Window;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import android.view.LayoutInflater;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.EditText;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.app.Dialog;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.viewmodel.UserViewModel;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.models.Markers;
import com.up959875.mobiletraveljournal.other.Constants;
import com.up959875.mobiletraveljournal.models.Address;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.viewmodel.AddressViewModel;
import com.up959875.mobiletraveljournal.databinding.FragmentExploreMapBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.up959875.mobiletraveljournal.adapters.MarkerDescAdapter;

public class ExploreMapFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback {

    private LatLng currentPlace;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private MarkerOptions currentMarkers;
    private com.google.android.gms.maps.model.Marker currentMarkerGoogle;
    private com.google.android.gms.maps.model.Marker clickedMarkerGoogle;
    private GoogleMap map;
    private FragmentExploreMapBinding binding;
    private User user;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private FindCurrentPlaceRequest request;
    private PlacesClient placesClient;
    private AddressViewModel addressViewModel;
    private UserViewModel userViewModel;
    View mapView;
    private Address markerAddress;
    Activity context;

    public static ExploreMapFragment newInstance() {
        return new ExploreMapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExploreMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initViewModels();

        initLoggedInUser();
        initGoogleMaps();
        initGooglePlaces();
        setListener();
        disableButtons();

        return view;
    }

    private void initViewModels() {
        if (getActivity() != null) {
            userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
            addressViewModel = new ViewModelProvider(getActivity()).get(AddressViewModel.class);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        map.setInfoWindowAdapter(new MarkerDescAdapter(getContext()));
        setMapListener();
        addMarkers();
    }


    private void initLoggedInUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {

            userViewModel.getUserData(firebaseAuth.getCurrentUser().getUid());
            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    this.user = user;
                    initLocation();
                    addMarkers();
                } else {
                    //showSnackBar(getResources().getString(R.string.message_user_not_available), Snackbar.LENGTH_LONG);
                    Log.d("l63", String.valueOf(R.string.message_user_not_available));
                }
            });
        }
    }

    private void updateUser(Map<String, Object> changes, boolean add) {
        userViewModel.updateUser(user, changes);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                String message = add
                        ? getResources().getString(R.string.add_marker_work)
                        : getResources().getString(R.string.remove_marker_work);
                refreshMap();
            } else {
                Log.d("error", "failure 111");
            }
        });
    }

    private void setMapListener() {
        map.setOnMapClickListener(latLng -> {
            addTemporaryMarkerOnMap(latLng);
            autocompleteSupportFragment.setText("");
        });
        map.setOnMarkerClickListener(marker -> {
            if (!marker.equals(currentMarkerGoogle)) {
                autocompleteSupportFragment.setText("");
                if (currentMarkerGoogle != null)
                    currentMarkerGoogle.remove();
                if(clickedMarkerGoogle != null && clickedMarkerGoogle.equals(marker)) {
                    marker.hideInfoWindow();
                    clickedMarkerGoogle = null;
                    binding.exploreMapAddPlaceButton.setEnabled(false);
                    binding.exploreMapRemovePlaceButton.setEnabled(false);
                } else {
                    marker.showInfoWindow();
                    clickedMarkerGoogle = marker;
                    binding.exploreMapAddPlaceButton.setEnabled(false);
                    binding.exploreMapRemovePlaceButton.setEnabled(true);
                }
                return true;
            } else return false;
        });
    }

    private void addMarkers() {
        if(user != null && user.getMarkers() != null && !user.getMarkers().isEmpty() && map != null) {
            Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
            for (Markers marker : user.getMarkers()) {

                addressViewModel.getAddress(user.getLocation());
                List<android.location.Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(marker.getLatitude(), marker.getLongitude(), 1);

                } catch (IOException e) {
                    
                }
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                        .title("Title: " + marker.getDescription())
                        .snippet("\n" + "Address: " + addresses.get(0).getAddressLine(0));
                Log.d("l188", marker.getDescription());
                map.addMarker(options);
            }
        }
    }

    private void initGoogleMaps() {
        ((SupportMapFragment) Objects.requireNonNull(getChildFragmentManager().findFragmentById(R.id.explore_map_google_map))).getMapAsync(this);
    }

    private void addTemporaryMarkerOnMap(LatLng place) {
        refreshMap();
        currentPlace = new LatLng(place.latitude, place.longitude);
        currentMarkers = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .position(currentPlace);
        currentMarkerGoogle = map.addMarker(currentMarkers);
        binding.exploreMapAddPlaceButton.setEnabled(true);
        binding.exploreMapRemovePlaceButton.setEnabled(false);
    }

    private void addMarker(String description) {
        List<Markers> newMarkersList = user.getMarkers() != null
                ? new ArrayList<>(user.getMarkers()) : new ArrayList<>();
        newMarkersList.add(new Markers(currentPlace.latitude, currentPlace.longitude, description));

        updateUser(new HashMap<String, Object>(){{put(Constants.MARKERS, newMarkersList);}}, true);
    }


    private void setAddressOnMap(Address address) {
        if (map != null) {
            LatLng place = new LatLng(address.getLatitude(), address.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(place, 8.0f));
        }
    }

    private void initGooglePlaces() {
        if (getContext() != null) {
            Places.initialize(getContext(), getString(R.string.google_maps_key));
            placesClient  = Places.createClient(getContext());
            autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager()
                    .findFragmentById(R.id.explore_map_search_view);
            if (autocompleteSupportFragment != null && autocompleteSupportFragment.getView() != null) {
                ((EditText) autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input))
                        .setTextSize(14.0f);
                ((EditText) autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_input))
                        .setTypeface(ResourcesCompat.getFont(getContext(), R.font.raleway_medium));
                autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_search_button)
                        .setVisibility(View.GONE);
                autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                        Place.Field.ADDRESS, Place.Field.LAT_LNG));
                request = FindCurrentPlaceRequest.newInstance(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                        Place.Field.ADDRESS, Place.Field.LAT_LNG));
            }
        }
    }


    private void showRemoveMarkerMessage() {
        if (getContext() != null) {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_contact);

            TextView title = dialog.findViewById(R.id.dialog_contact_title);
            TextView message = dialog.findViewById(R.id.dialog_contact_desc);
            MaterialButton buttonPositive = dialog.findViewById(R.id.dialog_contact_button_positive);
            MaterialButton buttonNegative = dialog.findViewById(R.id.dialog_contact_button_negative);

            title.setText(getResources().getString(R.string.remove_marker_title));
            message.setText(getResources().getString(R.string.remove_marker_desc));
            buttonPositive.setText(getResources().getString(R.string.remove_marker_work));
            buttonPositive.setOnClickListener(v -> {
                dialog.dismiss();
                if (clickedMarkerGoogle != null)
                    removeMarker();
            });
            buttonNegative.setText(getResources().getString(R.string.dialog_button_cancel));
            buttonNegative.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
    }

    private void removeMarker() {
        List<Markers> filtered = new ArrayList<>();
        for (Markers obj : user.getMarkers())
            if (!obj.equals(new Markers(
                    clickedMarkerGoogle.getPosition().latitude,
                    clickedMarkerGoogle.getPosition().longitude,
                    clickedMarkerGoogle.getTitle())))
                filtered.add(obj);
        updateUser(new HashMap<String, Object>(){{put(Constants.MARKERS, filtered);}}, false);
    }


    private void showAddMarkerMessage() {
        if (getContext() != null) {
            if (user != null) {
                Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_add_marker);
                dialog.findViewById(R.id.dialog_add_place_cancel_button).setOnClickListener(v -> dialog.dismiss());
                dialog.findViewById(R.id.dialog_add_place_add_button).setOnClickListener(v -> {
                    String description = Objects.requireNonNull(((TextInputEditText) dialog
                            .findViewById(R.id.dialog_add_place_input)).getText()).toString();
                    dialog.dismiss();
                    addMarker(description);
                });
                dialog.show();
            } else {
                Log.d("error", "error156");
            }
        }
    }


    private void setListener() {
        binding.exploreMapArrowButton.setOnClickListener(this);
        binding.exploreMapAddPlaceButton.setOnClickListener(this);
        binding.exploreMapRemovePlaceButton.setOnClickListener(this);
        if (autocompleteSupportFragment != null && autocompleteSupportFragment.getView() != null) {
            autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
                        if (!user.getMarkers().contains(new Markers(latLng.latitude, latLng.longitude, "")))
                            addTemporaryMarkerOnMap(latLng);
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                }
            });
            autocompleteSupportFragment.getView()
                    .findViewById(R.id.places_autocomplete_clear_button)
                    .setOnClickListener(view -> autocompleteSupportFragment.setText(""));
        }
    }

    private void initLocation() {
        if(user.getLocation() != null && !user.getLocation().isEmpty()) {

            addressViewModel.getAddress(user.getLocation());
            addressViewModel.getAddressData().observe(getViewLifecycleOwner(), address -> {
                if (address != null) {
                    setAddressOnMap(address);

                }

            });
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.explore_map_arrow_button:
                if (getParentFragmentManager().getBackStackEntryCount() > 0)
                    getParentFragmentManager().popBackStack();
                break;
            case R.id.explore_map_add_place_button:
                showAddMarkerMessage();
                break;
            case R.id.explore_map_remove_place_button:
                showRemoveMarkerMessage();
                break;
        }
    }
    private void refreshMap() {
        currentMarkerGoogle = null;
        map.clear();
        addMarkers();
    }

    private void disableButtons() {
        binding.exploreMapAddPlaceButton.setEnabled(false);
        binding.exploreMapRemovePlaceButton.setEnabled(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
