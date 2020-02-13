package com.example.rizwan.restolocator.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class RestaurantContract {

    public static final String AUTHORITY = "com.example.rizwan.restolocator";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_RESTAURANTS = "restaurants";

    public static final class RestaurantEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANTS).build();

        public static final String TABLE_NAME = "restaurants";
        public static final String RESTAURANT_ID = "id";
        public static final String RESTAURANT_BACKDROP_URI = "backdrop_path";
        public static final String RESTAURANT_NAME = "name";
        public static final String RESTAURANT_CUISINES = "cuisines";
        public static final String RESTAURANT_COST = "averageCostForTwo";
        public static final String RESTAURANT_CURRENCY = "currency";
        public static final String RESTAURANT_RATING = "rating";
        public static final String RESTAURANT_ONLINE = "online";
        public static final String RESTAURANT_ADDRESS = "address";
        public static final String RESTAURANT_CITY = "city";
        public static final String RESTAURANT_LAT = "latitude";
        public static final String RESTAURANT_LON = "longitude";
    }
}
