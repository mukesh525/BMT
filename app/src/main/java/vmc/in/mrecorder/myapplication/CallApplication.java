package vmc.in.mrecorder.myapplication;


import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.UUID;

import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.MDatabase;
import vmc.in.mrecorder.provider.GPSTracker;
import vmc.in.mrecorder.receiver.AlarmReceiver;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.service.TelephonyInfo;
import vmc.in.mrecorder.syncadapter.SyncUtils;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.SessionIdentifierGenerator;
import vmc.in.mrecorder.util.Utils;

public class CallApplication extends Application implements TAG, SharedPreferences.OnSharedPreferenceChangeListener{
    public static CallApplication mApplication;
    public static SharedPreferences sp;//to prevent concurrent creation of shared pref and editor
    public static Editor e;
    private static MDatabase mdatabase;
    //    public Intent all;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private Intent all;
    private GPSTracker mGPS;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mApplication = this;
        setAlarm();
        boolean sync = ContentResolver.getMasterSyncAutomatically();
        if (!sync)
            ContentResolver.setMasterSyncAutomatically(true);
        SyncUtils.CreateSyncAccount(getBaseContext());
       /* if (checkIfNumberExist()) {*/
            startRecording();
        /*}else{
            Log.wtf("Number","Not found");
        }*/
        Log.d("Alarm" ,"Call Application Triggered start Recording");
      //  mGPS = new GPSTracker(getAplicationContext());
    }


    public synchronized String getDeviceId() {

        return GetDeviceId();

    }

    public synchronized String getSessionID() {

        return new SessionIdentifierGenerator().nextSessionId();

    }


    public static Context getAplicationContext() {
        return mApplication.getApplicationContext();
    }


    public  GPSTracker getLocation(){
        return mGPS;
    }

    public static CallApplication getInstance() {
        return mApplication;
    }


    public void resetServicee() {
        try {
            Intent all = new Intent(this, CallRecorderServiceAll.class);
            // Intent opt = new Intent(this, CallRecorderServiceOptional.class);
            all.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            CallRecorderServiceAll.IS_SERVICE_RUNNING = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopService(all);
            } else {
                stopService(all);
            }
            // stopService(opt);
            if (sp.getInt("type", 0) == 0) {
                startService(all);
            } else if (sp.getInt("type", 0) == 1) {
                stopService(all);
                //  stopService(opt);
            } else if (sp.getInt("type", 0) == 2) {
                //  startService(opt);
            }
        } catch (Exception e) {
            Log.e("application", "reset service");
        }
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


    public synchronized String  GetDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        Log.wtf("android device_id", deviceId);
        return deviceId;

    }


    public void setAlarm() {
        Intent alarmIntent = new Intent(getAplicationContext(), AlarmReceiver.class);
        pendingIntent = getPendinIntent(alarmIntent);
        manager = (AlarmManager) getAplicationContext().getSystemService(Context.ALARM_SERVICE);
        int interval = 20; // 0.02 seconds
        boolean alarmUp = (PendingIntent.getBroadcast(getAplicationContext(), 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmUp) {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
    }

    public PendingIntent getPendinIntent(Intent intent) {
        return PendingIntent.getBroadcast(getAplicationContext(), 0, intent, 0);
    }

    public void startRecording() {
        Intent service = new Intent(this, CallRecorderServiceAll.class);
        if (!CallRecorderServiceAll.IS_SERVICE_RUNNING) {
            service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            CallRecorderServiceAll.IS_SERVICE_RUNNING = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
           //startService(service);
        }
    }


    public void stopRecording() {
        Intent service = new Intent(this, CallRecorderServiceAll.class);
        if (CallRecorderServiceAll.IS_SERVICE_RUNNING) {
            /*service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            CallRecorderServiceAll.IS_SERVICE_RUNNING = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopService(service);
            } else {
                startService(service);
            }*/
            startService(service);
        }
    }


    public String appVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String ver = pInfo.versionName;
//            int version = pInfo.versionCode;
            return ver;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return UNKNOWN;
        }

    }


    public synchronized static MDatabase getWritabledatabase() {
        if (mdatabase == null) {
            mdatabase = new MDatabase(getAplicationContext());
        }
        return mdatabase;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SyncUtils.CreateSyncAccount(getBaseContext());
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
           }

    public static String getSimId() {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getAplicationContext());
        String sim;
        if (telephonyInfo.isDualSIM()) {
            String sim1 = telephonyInfo.isSIM1Ready() ? telephonyInfo.getImsiSIM1() : UNKNOWN;
            String sim2 = telephonyInfo.isSIM2Ready() ? telephonyInfo.getImsiSIM1() : UNKNOWN;
            sim = sim1 + "," + sim2;
        } else {
            sim = telephonyInfo.isSIM1Ready() ? telephonyInfo.getImsiSIM1() : UNKNOWN;
        }

        return sim;
    }

    public static boolean isSimChanged() {
        String sim1 = getSimId();
        String sim2 = Utils.getFromPrefs(getAplicationContext(), SIM, UNKNOWN);
        return !sim1.equals(sim2);

    }


}
