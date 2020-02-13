package com.example.rizwan.restolocator.notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import com.example.rizwan.restolocator.App.RestaurantApp;
import com.example.rizwan.restolocator.DetailActivity;
import com.example.rizwan.restolocator.MainActivity;
import com.example.rizwan.restolocator.R;
import com.example.rizwan.restolocator.Utils;
import com.example.rizwan.restolocator.model.Restaurant;

import androidx.core.app.NotificationCompat;

import static com.example.rizwan.restolocator.MainActivityFragment.RESTAURANT;

public class StatusManager {

    private static final Context context = RestaurantApp.getAppContext();
    public static final String CHANNEL_ID = "com.example.rizwan.restolocator";

    static NotificationManager mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    static {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    public static void createChannel() {

        // The id of the channel.
        String id = CHANNEL_ID;
        // The user-visible name of the channel.
        CharSequence name = context.getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);
    }

    public static void addNotification(String id) {
        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, DetailActivity.class);
        Restaurant favRestaurant = Utils.getFavRestaurant(id);
        intent.putExtra(RESTAURANT, favRestaurant);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(RestaurantApp.getAppContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_bookmark)
                        .setContentTitle(context.getString(R.string.fav_restaurant_nearby))
                        .setContentText(context.getString(R.string.checkout, favRestaurant == null ? context.getString(R.string.your_fav) : favRestaurant.getRestaurant().getName()));
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(Integer.valueOf(id), mBuilder.build());
    }

    public static void removeNotification(String id) {
        mNotificationManager.cancel(Integer.valueOf(id));
    }
}