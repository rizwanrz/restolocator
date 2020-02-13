package com.example.rizwan.restolocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.rizwan.restolocator.geofence.GeoFenceHelper;
import com.example.rizwan.restolocator.model.Restaurant;
import com.example.rizwan.restolocator.model.RestaurantInfo;

import java.util.List;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    public static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "BootCompletedIntentRece";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_COMPLETED_ACTION.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: ");
            List<Restaurant> favRestaurants = Utils.getFavRestaurants();
            GeoFenceHelper geoFenceHelper = new GeoFenceHelper();
            for (Restaurant favRestaurant : favRestaurants) {
                RestaurantInfo restaurant = favRestaurant.getRestaurant();
                geoFenceHelper.addGeoFence(restaurant.getId(), Double.valueOf(restaurant.getLocation().getLatitude()), Double.valueOf(restaurant.getLocation().getLongitude()));
            }
        }
    }
}
