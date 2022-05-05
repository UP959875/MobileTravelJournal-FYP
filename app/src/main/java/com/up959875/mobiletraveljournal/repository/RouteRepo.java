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

//Handles getting and saving the route data from the Firebase database.
public class RouteRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference routesRef = rootRef.collection(Constants.ROUTES);

    /**
     * "This function takes a route object and a reference string, and returns a MutableLiveData object
     * containing a string."
     * 
     * The first line of the function is a comment. Comments are ignored by the compiler, but they are
     * useful for explaining what the code does
     * 
     * @param route The route object that contains the data to be saved.
     * @param reference The document reference of the route to be updated.
     * @return A MutableLiveData object.
     */
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

    /**
     * "This function takes a reference to a route and returns a MutableLiveData object that contains
     * the route."
     * 
     * The function takes a reference to a route and returns a MutableLiveData object that contains the
     * route
     * 
     * @param reference The reference of the route you want to get.
     * @return A MutableLiveData object that contains a Route object.
     */
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
