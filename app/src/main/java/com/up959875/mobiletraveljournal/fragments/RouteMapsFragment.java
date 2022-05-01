package com.up959875.mobiletraveljournal.fragments;

import static com.up959875.mobiletraveljournal.fragments.MapsActivity.fMarker;

import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.adapters.MarkerDescAdapter;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.databinding.ActivityMapsBinding;
import com.up959875.mobiletraveljournal.models.Route;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.databinding.ActivityMapsRouteBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteMapsFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private User user;
    Button submit_btn;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    private ActivityMapsRouteBinding activityMapsRouteBinding;
    String description = "null";
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    //List<Location> savedLocations;
    static List<MarkerOptions> fMarker = new ArrayList<MarkerOptions>();
    int clickCount = 0;
    private Route route;





    public static RouteMapsFragment newInstance(Route route) {
        return new RouteMapsFragment(route);
    }

    private RouteMapsFragment(Route route) {
        super();
        this.route = route;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        activityMapsRouteBinding = ActivityMapsRouteBinding.inflate(inflater, container, false);
        View view = activityMapsRouteBinding.getRoot();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

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
        //int i = 0;
        IconGenerator icg = new IconGenerator(getActivity());

        LatLng lastLocationPlaced = sydney;
        Log.d("route", route.getTitle());
        List<Object> list = new ArrayList<Object>(route.getMarkerOptions().values());

        int i = list.size()-1;
        Log.d("log", String.valueOf(list.size()));
        for(i = 0; i < (list.size()); i++){

            String addressText1 = String.valueOf(list.get(list.size()-1-i).toString().split("]")[0]);
            String addressText2 = addressText1.replace("{snippet=[", "");
            String locText1 = String.valueOf(list.get(list.size()-1-i).toString().replace(addressText2, ""));
            String locText2 = locText1.replace("{snippet=[], LatLng={", "");
            String locText3 = locText2.replace("}}", "");
            String locText4 = locText3.replace("latitude=", "");
            String locText5 = locText4.replace("longitude=", "");
            ArrayList<String> locList = new ArrayList<>(Arrays.asList(locText5.split(",")));
            Log.d("routeAdd", addressText2);
            Log.d("routeLoc", locText5);
            Log.d("long", locList.get(0));
            Log.d("lat", locList.get(1));
            LatLng myLatLng = new LatLng(Double.parseDouble(locList.get(0)), Double.parseDouble(locList.get(1)));
            icg.setStyle(IconGenerator.STYLE_RED);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLatLng);
            markerOptions.title(String.format("Marker %d", i+1));
            markerOptions.snippet(addressText2);
            Bitmap bm = icg.makeIcon(String.format("Marker %d", i+1));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bm));
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 12.0f));
        }

    }
}
