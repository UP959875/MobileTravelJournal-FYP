package com.up959875.mobiletraveljournal.adapters;

import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import com.up959875.mobiletraveljournal.databinding.WindowMarkerDescBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap;

public class MarkerDescAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public MarkerDescAdapter(Context context){
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        WindowMarkerDescBinding binding = WindowMarkerDescBinding.inflate(LayoutInflater.from(context));

        binding.markerTitle.setText(marker.getTitle());

        if (marker.getSnippet() != null && !marker.getSnippet().isEmpty())
            binding.markerDesc.setText(marker.getSnippet());
        else binding.markerDesc.setVisibility(View.GONE);

        View view;
        if (marker.getTitle() != null && !marker.getTitle().isEmpty())
            view = binding.getRoot();
        else view = null;

        return view;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
