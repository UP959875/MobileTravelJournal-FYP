package com.up959875.mobiletraveljournal.adapters;

import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import com.up959875.mobiletraveljournal.databinding.WindowMarkerDescBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap;

//Class for the markers, helps display the data on the app.
public class MarkerDescAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    //Constructor for the class
    public MarkerDescAdapter(Context context){
        this.context = context;
    }

   /**
    * If the marker has a title, then return the view, otherwise return null
    * 
    * @param marker The marker that was clicked.
    * @return The view that is returned is the view that is displayed in the info window.
    */
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
