package com.example.rizwan.restolocator;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.rizwan.restolocator.geofence.GeoFenceHelper;
import com.example.rizwan.restolocator.model.Restaurant;
import com.example.rizwan.restolocator.model.RestaurantInfo;
import com.example.rizwan.restolocator.notification.StatusManager;
import com.example.rizwan.restolocator.widget.RestaurantInfoWidgetManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.rizwan.restolocator.MainActivityFragment.RESTAURANT;
import static com.example.rizwan.restolocator.SearchActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.example.rizwan.restolocator.widget.RestaurantInfoWidget.POSITION;
import static com.example.rizwan.restolocator.widget.RestaurantInfoWidget.RESTAURANT_LIST;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DetailActivity";

    @BindView(R.id.backdrop)
    ImageView backDropView;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.fab_fav)
    FloatingActionButton favourite;
    Restaurant restaurant;;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi(savedInstanceState);
        loadUI();
        loadFragment(savedInstanceState);
    }

    private void loadUI() {
        if (restaurant != null) {
            loadBackDrop();
            initFav();
        }
    }

    private void loadFragment(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        if (savedInstanceState == null) {
            fragment = new DetailActiviyFragment();
            Bundle args = new Bundle();
            args.putParcelable(RESTAURANT, restaurant);
            fragment.setArguments(args);
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }

    private void initFav() {
        if (Utils.isFavorite(this, restaurant.getRestaurant())) {
            favourite.setImageResource(R.drawable.ic_bookmark);
        } else {
            favourite.setImageResource(R.drawable.ic_bookmark_border);
        }
    }

    private void loadBackDrop() {
        String featuredImage = restaurant.getRestaurant().getFeaturedImage();
        if (featuredImage.isEmpty()) {
            backDropView.setImageResource(R.drawable.recipe_icon_md);
            return;
        }
        Picasso.get()
                .load(featuredImage)
                .error(R.drawable.recipe_icon_md)
                .placeholder(R.drawable.recipe_icon_md)
                .into(backDropView);
    }

    private void initUi(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar_details_activity);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        restaurant = getRestaurant();
        collapsingToolbar.setTitle(restaurant.getRestaurant().getName());
        favourite.setOnClickListener(this);
    }

    public Restaurant getRestaurant() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new IllegalStateException();
        }
        if (extras.containsKey(RESTAURANT_LIST)) {
            String list = extras.getString(RESTAURANT_LIST);
            int pos = extras.getInt(POSITION);
            return new RestaurantInfoWidgetManager().getInfo(list, pos);
        }
        if (!extras.containsKey(RESTAURANT)) {
            throw new IllegalStateException();
        }
        return (Restaurant) extras.get(RESTAURANT);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick() called with: view = [" + view + "]");
        setFavorite();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geoFence();
        } else {
            permissionCheck();
        }
    }

    private void geoFence() {
        RestaurantInfo restaurantInfo = restaurant.getRestaurant();
        GeoFenceHelper geoFenceHelper = new GeoFenceHelper();
        if (Utils.isFavorite(this, restaurantInfo)) {
            //add geofence
            geoFenceHelper.addGeoFence(restaurantInfo.getId(),Double.valueOf(restaurantInfo.getLocation().getLatitude()),Double.valueOf(restaurantInfo.getLocation().getLongitude()));
        } else {
            //remove geofence
            geoFenceHelper.removeGeoFence(restaurantInfo.getId());
            StatusManager.removeNotification(restaurantInfo.getId());
        }
    }

    private void setFavorite() {
        RestaurantInfo restaurantInfo = restaurant.getRestaurant();
        if (Utils.isFavorite(this, restaurantInfo)) {
            Utils.removeFromFavorites(this, restaurantInfo.getId());
            favourite.setImageResource(R.drawable.ic_bookmark_border);
        } else {
            Utils.addToFavorites(this, restaurantInfo);
            favourite.setImageResource(R.drawable.ic_bookmark);
        }
        new RestaurantInfoWidgetManager().updateWidgetRecipe(Utils.getFavRestaurants());
    }

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(findViewById(android.R.id.content), R.string.grant_permission_for_geofence, Snackbar.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    geoFence();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }
}
