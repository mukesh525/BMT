package vmc.in.mrecorder.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by gousebabjan on 9/3/16.
 */
public class BootBroadcastReceiver extends BroadcastReceiver implements vmc.in.mrecorder.callbacks.TAG {
    private static final long SPLASH_TIME_OUT = 10000;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private boolean debugEnable;

    public BootBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean sync = ContentResolver.getMasterSyncAutomatically();
        if(!sync)
            ContentResolver.setMasterSyncAutomatically(true);
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        debugEnable = sharedPrefs.getBoolean("prefDebug", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.wtf("DEVICEID verification"," "+Utils.getFromPrefs(context,DEVICE_ID,UNKNOWN)+"==="+ CallApplication.getInstance().getDeviceId());
                if(CallApplication.getInstance().getDeviceId().equals(Utils.getFromPrefs(context,DEVICE_ID,UNKNOWN))) {

                    if (Utils.isLogin(context)) {
                        if(debugEnable){
                            Toast.makeText(context,"MTracker is Logged in.",Toast.LENGTH_SHORT).show();
                        }
                        CallApplication.getInstance().startRecording();
                        Log.d("Alarm" ,"BootBroadcast Triggered start Recording");
                        Log.d("SyncAdapter", "BootBroadcastReceiver");
                    } else {
                        CallApplication.getInstance().stopRecording();
                        Log.d("Alarm" ,"BootBroadcast Triggered stop Recording");
                    }
                }else {
                    if(debugEnable){
                        Toast.makeText(context,"DeviceId changed Logged out.",Toast.LENGTH_SHORT).show();
                    }

                    Utils.isLogoutBackground(context,"Login to MTracker.");

                }


            }
        }, SPLASH_TIME_OUT);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = 10;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }
}
