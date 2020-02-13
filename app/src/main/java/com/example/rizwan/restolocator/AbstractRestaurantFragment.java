package com.example.rizwan.restolocator;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.example.rizwan.restolocator.model.Restaurant;

import androidx.annotation.Nullable;

import static com.example.rizwan.restolocator.MainActivityFragment.RESTAURANT;
import static com.example.rizwan.restolocator.Utils.isOnline;

public abstract class AbstractRestaurantFragment extends Fragment {
    private static final String TAG = "MoviesFragment";
    Restaurant restaurant;

    public void getRestaurantFromBundle() {
        restaurant = getArguments().getParcelable(RESTAURANT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline()) {
                onlineViewChanges();
            } else {
                offlineViewChanges();
            }
        }
    };

    protected void offlineViewChanges() {
    }

    protected void onlineViewChanges() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getActivity().unregisterReceiver(connectivityChangeReceiver);
        } catch (Exception e) {
            Log.i(TAG, "Exception occurred during un-registering network receiver");
        }
    }
}
