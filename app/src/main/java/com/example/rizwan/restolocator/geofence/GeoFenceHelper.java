package com.example.rizwan.restolocator.geofence;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.example.rizwan.restolocator.App.RestaurantApp;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Collections;

import androidx.annotation.NonNull;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class GeoFenceHelper{

    private static final String TAG = "GeoFenceHelper";
    public static final int METERS = 250;
    private GeofencingClient geofenceClient;
    private PendingIntent mGeofencePendingIntent;
    Geofence geofence;

    public GeoFenceHelper() {
        geofenceClient = LocationServices.getGeofencingClient(RestaurantApp.getAppContext());
    }

    @SuppressLint("MissingPermission")
    public void addGeoFence(String restaurantId, double latitude, double longitude) {
        geofence = buildGeoFence(restaurantId, latitude, longitude);
        GeofencingRequest geoFencingRequest = getGeoFencingRequest(geofence);
        geofenceClient.addGeofences(geoFencingRequest, getGeofencePendingIntent())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "addGeoFence: success");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "addGeoFence: ", e);
                });
    }

    @NonNull
    private Geofence buildGeoFence(String restaurantId, double latitude, double longitude) {
        return new Geofence.Builder().setRequestId(restaurantId)
                .setCircularRegion(
                        latitude, longitude, METERS
                )
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest getGeoFencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(RestaurantApp.getAppContext(), GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(RestaurantApp.getAppContext(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    public void removeGeoFence(String id) {
        geofenceClient.removeGeofences(Collections.singletonList(id)).
                addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "removeGeoFence: success");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "removeGeoFence: ", e);
                });
    }
}
