package com.up959875.mobiletraveljournal.repository;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.up959875.mobiletraveljournal.models.Route;
import com.up959875.mobiletraveljournal.other.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RouteRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference routesRef = rootRef.collection(Constants.ROUTES);

    public MutableLiveData<String> saveRoute(Route route, String reference) {
        MutableLiveData<String> statusData = new MutableLiveData<>();

        DocumentReference routeRef = reference == null ? routesRef.document() : routesRef.document(reference);
        if (route == null) route = new Route();
        route.setId(routeRef.getId());

        routeRef.set(route).addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                statusData.setValue(routeRef.getId());
            } else if (uidTask.getException() != null) {
                statusData.setValue("Error: " + uidTask.getException().getMessage());
            }
        });
        return statusData;


    }

    public MutableLiveData<Route> getRoute(String reference) {
        MutableLiveData<Route> routeData = new MutableLiveData<>();
        DocumentReference routeRef = routesRef.document(reference);
        routeRef.get().addOnCompleteListener(uidTask  -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (document != null && document.exists()) {
                    Route route = document.toObject(Route.class);
                    routeData.setValue(route);
                }
            } else if (uidTask.getException() != null) {
                routeData.setValue(null);
            }
        });
        return routeData;
    }

}
