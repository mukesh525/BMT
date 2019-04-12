package vmc.in.mrecorder.gcm;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by gousebabjan on 17/3/16.
 */
public class PushNotificationService extends GcmListenerService implements vmc.in.mrecorder.callbacks.TAG {
    private NotificationManager mNotificationManager;
    // public static int NOTIFICATION_ID = 1;
    private String TAG = "GCMPRO";
    private String url;
    private String message = "n/a";
    private String enable = "n/a";
    private boolean recording, monitor, both;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        //by admin to enable disable
        if (data.containsKey("enable")) {
            enable = data.getString("enable");

            recording = true;
            monitor = false;
            both = false;

            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor ed = sharedPrefs.edit();


            if (recording) {
                CallApplication.getInstance().startRecording();
                ed.putBoolean("prefRecording", true);
                ed.putBoolean("prefCallUpdate", false);
                ed.commit();
            } else if (monitor) {
                CallApplication.getInstance().startRecording();
                ed.putBoolean("prefRecording", false);
                ed.putBoolean("prefCallUpdate", true);
                ed.commit();
            } else if (both) {
                ed.putBoolean("prefRecording", false);
                ed.putBoolean("prefCallUpdate", false);
                CallApplication.getInstance().stopRecording();
            }


        }

        if (data.containsKey("message")) {
            message = data.getString("message");

            Log.d(TAG, message);
            //   sendStickyNotification(message);
//            if (message != null && message.length() > 5) {
//                if (Utils.isLogin(getApplicationContext())) {
//                    sendStickyNotification(message);
//                    CallApplication.getWritabledatabase().DeleteAllData();
//                    CallApplication.getInstance().stopRecording();
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    prefs.edit().clear().commit();
//                    Log.d("Logout", "Logout on gcm");
//
//                }
//
//            }
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String msg) {
        Intent resultIntent = new Intent(this, Login.class);
        TaskStackBuilder TSB = TaskStackBuilder.create(this);
        TSB.addParentStack(Login.class);
        TSB.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                TSB.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //Create notification object and set the content.
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
        nb.setSmallIcon(R.drawable.mcube);
        nb.setContentTitle("Set your title");
        nb.setContentText("Set Content text");
        nb.setTicker("Set Ticker text");
        nb.addAction(R.drawable.mcube, "Share", resultPendingIntent);
        //  nb.setContent(new RemoteViews(new Tex))
        nb.setContentText(msg);

        //get the bitmap to show in notification bar
        // Bitmap bitmap_image = BitmapFactory.decodeResource(this.getResources(), R.drawable.drawerr);
        // Bitmap bitmap_image = getBitmapFromURL("http://images.landscapingnetwork.com/pictures/images/500x500Max/front-yard-landscaping_15/front-yard-hillside-banyon-tree-design-studio_1018.jpg");
        Bitmap bitmap_image = getBitmapFromURL(url);
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap_image);
        s.setSummaryText("Summary text appears on expanding the notification");
        nb.setStyle(s);

//        Intent resultIntent = new Intent(this, MainActivity.class);
//        TaskStackBuilder TSB = TaskStackBuilder.create(this);/home/gousebabjan/Desktop/mcube.png
//        TSB.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack


        nb.setContentIntent(resultPendingIntent);
        nb.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(11221, nb.build());


    }

    private void sendNotification1(String message) {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mcube)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendStickyNotification(String message) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle s = new NotificationCompat.BigTextStyle();
        s.setBigContentTitle("MTracker");
        //  s.bigText("You have been logout by Admin.");
        s.bigText(message);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSound(defaultSoundUri)
                .setLargeIcon(bm)
                .setStyle(s)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, Login.class), 0));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
