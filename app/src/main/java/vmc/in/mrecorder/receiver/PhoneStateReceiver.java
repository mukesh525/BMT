package vmc.in.mrecorder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by gousebabjan on 5/4/16.
 */
public class PhoneStateReceiver extends BroadcastReceiver {
    //This Method automatically Executed when Phone State Change is Detected
    public void onReceive(Context context, Intent intent) {
        Log.d("SERVICE", "PhoneStateReceiver");
        if (Utils.isLogin(context)) {
            CallApplication.getInstance().startRecording();

            Log.d("Alarm" ,"PhoneStateReceiver Triggered start Recording");

            Log.d("SERVICE", "PhoneStateReceiver Login");

        } else {
            CallApplication.getInstance().stopRecording();
            Log.d("Alarm" ,"PhoneStateReceiver Triggered stop Recording");
        }

    }
}