package com.up959875.mobiletraveljournal.fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.up959875.mobiletraveljournal.R;

public class MapDetailsActivityFragment extends AppCompatActivity {

    TextView markerText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_details_fragment);
        markerText = findViewById(R.id.markerActivityText);
        String title = getIntent().getStringExtra("Title");
        markerText.setText(title);

        TextView markerAddressText = findViewById(R.id.addressText);
        String address = getIntent().getStringExtra("Address");
        markerAddressText.setText(address);

    }
}
