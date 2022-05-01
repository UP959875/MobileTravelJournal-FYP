package com.up959875.mobiletraveljournal.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.databinding.FragmentShowSavedLocationsListBinding;

import java.util.List;
import java.util.Objects;

public class ShowSavedLocationsListFragment extends AppCompatActivity {


    ListView lv_savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_saved_locations_list);

        lv_savedLocations = findViewById(R.id.lv_waypoints);
        RouteGlobal routeGlobal = new RouteGlobal();
        List<Location> savedLocations = RouteFragment.savedLocations;
        lv_savedLocations.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocations));

    }
}
