package vmc.in.mrecorder.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.Utils;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		boolean sync = ContentResolver.getMasterSyncAutomatically();
		if(!sync)
			ContentResolver.setMasterSyncAutomatically(true);
		if (Utils.isLogin(context)) {
			CallApplication.getInstance().startRecording();
			Log.d("Alarm" ,"Alaram Triggered start Recording");
		}else {
            CallApplication.getInstance().stopRecording();
			Log.d("Alarm" ,"Alaram Triggered stop Recording");

		}

	}

}
