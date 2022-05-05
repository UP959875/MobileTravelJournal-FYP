package com.up959875.mobiletraveljournal.fragments;

import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.textfield.TextInputEditText;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import com.up959875.mobiletraveljournal.databinding.ActivityMapsBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.models.Markers;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.adapters.MarkerDescAdapter;
import com.up959875.mobiletraveljournal.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.core.content.ContextCompat;
//import static com.up959875.mobiletraveljournal.fragments.RouteFragment.addresses;
import static com.up959875.mobiletraveljournal.fragments.RouteFragment.savedLocations;

//Activity used to open Google Maps and place down the markers from the automatic GPS tracking. Allows users to add a description to each marker before going to the submission page.
public class MapsActivity extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private User user;
    Button submit_btn;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    private ActivityMapsBinding activityMapsBinding;
    String description = "null";
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    //List<Location> savedLocations;
    static List<MarkerOptions> fMarker = new ArrayList<MarkerOptions>();
    int clickCount = 0;

    public static MapsActivity newInstance() {
        return new MapsActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityMapsBinding = ActivityMapsBinding.inflate(inflater, container, false);
        View view = activityMapsBinding.getRoot();
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        submit_btn = (Button) view.findViewById(R.id.submit_btn);
        Log.d("cbutton", String.valueOf(submit_btn));
        activityMapsBinding.submitBtn.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        List<Location> savedLocations = RouteFragment.savedLocations;
        //List<Address> savedAddresses = RouteFragment.addresses;
        return view;
    }

    /*submit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                changeFragment(RouteSubmitFragment.newInstance());
            }});*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    };
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MarkerDescAdapter(getActivity()));
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // two second intervals
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
        Log.d("l137", "l137");
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Geocoder geocoder = new Geocoder(getActivity());
        int i = 0;
        IconGenerator icg = new IconGenerator(getActivity());

        LatLng lastLocationPlaced = sydney;
        for (Location location: savedLocations){
            i = ++i;


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            icg.setStyle(IconGenerator.STYLE_RED);

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                markerOptions.snippet("\n" + "Address: " + addresses.get(0).getAddressLine(0) + "\n" + "Description: " + description);
                Log.d("desc", description);

            } catch (IOException e) {
                e.printStackTrace();
            }
            markerOptions.position(latLng);
            markerOptions.anchor(0.5f, 1);
            markerOptions.title(String.format("Marker %d", i));
            Bitmap bm = icg.makeIcon(String.format("Marker %d", i));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bm));

            //markerOptions.title("Lat: " + location.getLatitude()+" Lon: " + location.getLongitude());

            mMap.addMarker(markerOptions);
            lastLocationPlaced = latLng;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced, 12.0f));




        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {



                        Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.dialog_add_marker);
                        dialog.findViewById(R.id.dialog_add_place_cancel_button).setOnClickListener(v -> dialog.dismiss());
                        dialog.findViewById(R.id.dialog_add_place_add_button).setOnClickListener(v -> {
                            String description2 = Objects.requireNonNull(((TextInputEditText) dialog
                                    .findViewById(R.id.dialog_add_place_input)).getText()).toString();
                            dialog.dismiss();
                            Log.d("ok", description2);
                            marker.hideInfoWindow();

                            placeMarkers(description2);
                            marker.showInfoWindow();
                        });
                        dialog.show();
                return false;
                    };



        });
    }

    public void placeMarkers(String description){
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Geocoder geocoder = new Geocoder(getActivity());
        int i = 0;
        IconGenerator icg = new IconGenerator(getActivity());

        LatLng lastLocationPlaced = sydney;
        MarkerOptions markerOptions = new MarkerOptions();
        for (Location location: savedLocations){
            i = ++i;


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            icg.setStyle(IconGenerator.STYLE_RED);

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                markerOptions.snippet("\n" + "Address: " + addresses.get(0).getAddressLine(0) + "\n" + "Description: " + description);
                Log.d("desc", description);

            } catch (IOException e) {
                e.printStackTrace();
            }
            markerOptions.position(latLng);
            markerOptions.anchor(0.5f, 1);
            markerOptions.title(String.format("Marker %d", i));
            Bitmap bm = icg.makeIcon(String.format("Marker %d", i));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bm));

            //markerOptions.title("Lat: " + location.getLatitude()+" Lon: " + location.getLongitude());

            mMap.addMarker(markerOptions);
            lastLocationPlaced = latLng;

        }
        fMarker.add(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced, 12.0f));
    }

    private void changeFragment(Fragment next) {
        getNavigationInteractions().changeFragment(getParentFragment(), next, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_btn:
                Log.d("button", "yes");
                Log.d("id", String.valueOf(((ViewGroup)getView().getParent()).getId()));
                changeFragment(RouteSubmitFragment.newInstance());
        }};


}
