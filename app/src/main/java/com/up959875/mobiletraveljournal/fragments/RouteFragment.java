package com.up959875.mobiletraveljournal.fragments;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.squareup.okhttp.Route;
import com.up959875.mobiletraveljournal.activities.MainActivity;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.databinding.FragmentRouteBinding;
import java.util.List;

//Fragment for the route tracking feature. Has multiple switches, and allows the user to toggle automatic tracking on and off. Can view the list of markers, or go to the map from there.
public class RouteFragment extends BaseFragment implements View.OnClickListener {

    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    public static final int FAST_UPDATE_INTERVAL = 2;
    private static final int PERMISSIONS_FINE_LOCATION = 100;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_waypointCount;
    Button btn_newWaypoint, btn_ShowWaypointList, btn_showMap;
    SwitchMaterial sw_locationupdates, sw_gps, sw_autotracking;


    FusedLocationProviderClient fusedLocationProviderClient;

    boolean updateOn = false;
    private static Context globalContext = null;

    Location currentLocation;
    static List<Location> savedLocations;
    static List<Address> addresses;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private FragmentRouteBinding binding;
    final Handler handler = new Handler();
    Runnable runnable;
    final int delay = 5000;



    public static RouteFragment newInstance() {
        return new RouteFragment();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRouteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        globalContext = getActivity().getApplicationContext();
        tv_lat = view.findViewById(R.id.tv_lat);
        tv_lon = view.findViewById(R.id.tv_lon);
        tv_altitude = view.findViewById(R.id.tv_altitude);
        tv_accuracy = view.findViewById(R.id.tv_accuracy);
        tv_speed = view.findViewById(R.id.tv_speed);
        tv_sensor = view.findViewById(R.id.tv_sensor);
        tv_updates = view.findViewById(R.id.tv_updates);
        tv_address = view.findViewById(R.id.tv_address);
        sw_locationupdates = view.findViewById(R.id.sw_locationsupdates);
        sw_gps = view.findViewById(R.id.sw_gps);
        sw_autotracking = view.findViewById(R.id.sw_autotracking);
        btn_newWaypoint = view.findViewById(R.id.btn_newWaypoint);
        btn_ShowWaypointList = view.findViewById(R.id.btn_showWaypointList);
        tv_waypointCount = view.findViewById(R.id.tv_countofcrumbs);
        btn_showMap = view.findViewById(R.id.btn_showMap);

        locationRequest = new LocationRequest();
        RouteGlobal routeGlobal = new RouteGlobal();
        routeGlobal.onCreate();

        locationCallback = new LocationCallback(){


            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        btn_newWaypoint.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {



                //Log.d("ok", String.valueOf(savedLocations));
                savedLocations = routeGlobal.getMyLocations();
                //Log.d("ok", String.valueOf(savedLocations));
                savedLocations.add(currentLocation);
                Log.d("ok", String.valueOf(savedLocations));
                tv_waypointCount.setText(Integer.toString(savedLocations.size()));

            }

        });

        btn_ShowWaypointList.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ShowSavedLocationsListFragment.class);
                startActivity(i);
            }
        });

        btn_showMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                changeFragment(MapsActivity.newInstance());

            }
        });



        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Mobile Signal and Wifi");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                }
                else{
                    stopLocationUpdates();
                }
            }
        });

        sw_autotracking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (sw_autotracking.isChecked()) {
                    startAutoTracking();
                }
                else{
                    stopAutoTracking();
                }
            }
        });

        updateGPS();
        return view;
    }

    private void startAutoTracking() {

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                btn_newWaypoint.performClick();
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    private void stopAutoTracking() {
        handler.removeCallbacks(runnable);
    }


    private void startLocationUpdates() {
        tv_updates.setText("Location is being recorded");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is not being recorded");
        tv_lat.setText("Nothing");
        tv_lon.setText("Nothing");
        tv_speed.setText("Nothing");
        tv_address.setText("Nothing");
        tv_accuracy.setText("Nothing");
        tv_altitude.setText("Nothing");
        tv_sensor.setText("Nothing");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    private void updateGPS() {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        updateUIValues(location);
                        Log.d("ok", "ui update");
                        currentLocation = location;
                    }
                });
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
                }
            }

        }

        private void updateUIValues (Location location) {


            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));

            if (location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            }
            else {
                tv_altitude.setText("Cannot get altitude");
            }
            if (location.hasSpeed()) {
                tv_speed.setText(String.valueOf(location.getSpeed()));
            }
            else {
                tv_speed.setText("Cannot get speed");
            }

            Geocoder geocoder = new Geocoder(getActivity());

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                tv_address.setText(addresses.get(0).getAddressLine(0));
            }
            catch (Exception e) {
                tv_address.setText("Unable to get an address");
            }









        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            }
            else {
                //Toast.makeText(this, "This app requires location permissions to work properly", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changeFragment(Fragment next) {
        getNavigationInteractions().changeFragment(this, next, false);
    }

    protected void onCreate() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_showMap:
                changeFragment(MapsActivity.newInstance());
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

