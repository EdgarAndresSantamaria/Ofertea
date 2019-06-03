package com.example.proyecto_1;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FCM extends FirebaseMessagingService {
    /**
     * public constructor
     */
    public FCM() {

    }

    /**
     * This method handles incomming FCM mesage from Google Firebase
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // get activity mnager
        ActivityManager am= (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        // check sdk versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // check background usage status
            if (am.isBackgroundRestricted()==true){
                // alert we could not notify unless background usage is activated
                showNotification(getString(R.string.warn_text), getString(R.string.warn_subtext), getString(R.string.warn_title));
            }
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            // not implemented any data serve
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            // server sends a notification but we redefine for maintain language consistency
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String user =  prefs.getString("user","invitado");
            if(!user.equals("invitado")) {
                // user not identified
                showNotification(getString(R.string.done_text), getString(R.string.done_subtext), getString(R.string.done_title));
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int favs = prefs.getInt("favourites", 0);
                favs ++;
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                // stablish a writer
                SharedPreferences.Editor editor = prefs.edit();
                // redefine style flag
                editor.putInt("favourites",favs);
                editor.commit();
            }else{
                // user identified
                showNotification(getString(R.string.reject_text), getString(R.string.reject_subtext), getString(R.string.reject_title));
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
       // nothing to do
    }

    /**
     * This method shows a notification made with a title 'text' and a description as 'subText'
     * @param text
     * @param subText
     */
    public void showNotification(String text, String subText, String title){
        // retrieve Notification Manager
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Start a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "IdCanal");
        // personalize notification
        builder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(title)
                .setContentText(text)
                .setSubText(subText)
                .setVibrate(new long[]{0, 1000, 500, 1000}).setAutoCancel(true);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            // for API >=  Oreo (8)
            // build notification
            NotificationChannel channel = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
            // create channel
            manager.createNotificationChannel(channel);

        }
        // show notification
        manager.notify(1, builder.build());
    }
}
