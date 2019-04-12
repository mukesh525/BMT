package vmc.in.mrecorder.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.syncadapter.SyncUtils;

import static android.provider.Telephony.ThreadsColumns.ERROR;

/**
 * Created by gousebabjan on 17/3/16.
 */
public class Utils implements TAG {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static String callDuration;
    private static String callType;
    private static String hrSize = "";
    private static String callStatus;
    private String code;
    private String msg;
    private static String recording;
    private static String mcubeRecording;
    private static String workhour;
    private String isLogg;


    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }


    public static boolean hasWIFIConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
                // Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                // Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            // not connected to the internet
            return false;
        }
        return false;

    }

    public static String getContactName(String snumber, Context context) throws Exception {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(snumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = "";
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }


    public static void uploadMultipartData(Model model, boolean fileExist, Context context) throws Exception {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sharedPrefs.edit();

        String TAG1 = "Sync in background";

        Log.d(TAG1, "uploadMultipartData:" + model.getPhoneNumber());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SS");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        int duration = 0;
        long fileSize = 0;
        if (fileExist) {
            try {
                MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(model.getFile()));
                duration = mp.getDuration();
            } catch (Exception e) {
                duration = 0;
            }
            model.setDuration(String.valueOf(duration));
            boolean notifyMode = sharedPrefs.getBoolean("prefRecording", false);
            Log.d(TAG1, "Calls No Recording" + notifyMode);
            if (notifyMode) {
                fileSize = model.getFile().length();
                size(fileSize);
                builder.addPart(UPLOADEDFILE, new FileBody(model.getFile()));
                Log.d(TAG1, UPLOADEDFILE + ":" + model.getFile().getName());
            }

            Long time = Long.valueOf(model.getTime()).longValue();
            long endtime = time + duration;
            builder.addPart(ENDTIME, new StringBody(sdf.format(new Date(endtime)), ContentType.TEXT_PLAIN));
            Log.d(TAG1, ENDTIME + ":" + sdf.format(new Date(endtime)));
        }
        callDuration = model.getDuration() != null ? model.getDuration() : "0";
        Log.e("call_duration: ", " sync_adapter_time: &&&& " + callDuration);
        int test = Integer.parseInt(callDuration);
        int test1 = test * 1000;
        String durationMillis = String.valueOf(test1);
        builder.addPart(DURATION, new StringBody(durationMillis + "", ContentType.TEXT_PLAIN));

        Log.e("call_duration: ", " sync_adapter_time: $$$$$$ " + DURATION);
        String callState = sharedPrefs.getString("callStatus", "");
        int totalduration = 0;

        Log.e("Call duration:", callDuration + " Call Type:" + callType + " File size:" + fileSize + " File size in MB/KB:" + hrSize);
        switch (model.getCallType()) {
            case "Outbound":
                if (!callDuration.equals("0")) {
                    callStatus = "ANSWER";
                } else {
                    callStatus = "CANCEL";
                }
                break;
            case "Inbound":
                if (callState.equals("MISSED")) {
                    callStatus = "CANCEL";
                } else {
                    callStatus = "ANSWER";
                }
                break;
            default:
                callStatus = "CANCEL";
                break;
        }

        builder.addPart(CALLSTATUS, new StringBody((callStatus), ContentType.TEXT_PLAIN));
        builder.addPart(AUTHKEY, new StringBody(Utils.getFromPrefs(context, AUTHKEY, "n"), ContentType.TEXT_PLAIN));
        Log.d(TAG1, AUTHKEY + ":" + Utils.getFromPrefs(context, AUTHKEY, "n"));
        builder.addPart(DEVICE_ID, new StringBody(Utils.getFromPrefs(context, SESSION_ID, UNKNOWN), ContentType.TEXT_PLAIN));
        Log.d(TAG1, DEVICE_ID + ":" + Utils.getFromPrefs(context, SESSION_ID, UNKNOWN));
        builder.addPart(CALLTO, new StringBody(model.getPhoneNumber(), ContentType.TEXT_PLAIN));
        Log.d(TAG1, CALLTO + ":" + model.getPhoneNumber());
        builder.addPart(STARTTIME, new StringBody(sdf.format(new Date(Long.parseLong(model.getTime()))), ContentType.TEXT_PLAIN));
        Log.d(TAG1, STARTTIME + ":" + sdf.format(new Date(Long.parseLong(model.getTime()))));
        Log.e("call_duration: ", " start_time: &&&& " + STARTTIME);
        builder.addPart(CALLTYPEE, new StringBody(model.getCallType(), ContentType.TEXT_PLAIN));
        builder.addPart(CONTACTNAME, new StringBody(getContactName(model.getPhoneNumber(), context), ContentType.TEXT_PLAIN));
        Log.wtf(TAG1, CALLTYPEE + ":" + model.getCallType());
        Log.d(TAG1, "CONTACTNAME" + ":" + getContactName(model.getPhoneNumber(), context));
        builder.addPart(LOCATION, new StringBody(model.getLocation(), ContentType.TEXT_PLAIN));
        Log.d(TAG1, LOCATION + ":" + model.getLocation());
        if (!fileExist) {
            builder.addPart(ENDTIME, new StringBody(sdf.format(new Date(Long.parseLong(model.getTime()))), ContentType.TEXT_PLAIN));
            Log.d(TAG1, ENDTIME + ":" + sdf.format(new Date(Long.parseLong(model.getTime()))));
            Log.e("call_duration: ", " end_time: &&&& " + ENDTIME);
        }

        HttpEntity entity = builder.build();
        URL url = null;
        url = new URL(UPLOAD_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.addRequestProperty("Content-length", entity.getContentLength() + "");
        urlConnection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
        OutputStream os = urlConnection.getOutputStream();
        entity.writeTo(urlConnection.getOutputStream());
        os.close();
        urlConnection.connect();
        int status = 0;
        status = urlConnection.getResponseCode();
        Log.d("SERVER_RESPONCE", status + " CODE");
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s = "";
            StringBuilder stringBuilder = new StringBuilder("");
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }
            String serverResponseMessage = stringBuilder.toString();
            Log.d(TAG1, "RESPONSE:" + serverResponseMessage);
            Log.d("OnCal", "RESPONSE:" + serverResponseMessage);
            String code, msg, callId = null;
            JSONObject responseobj = new JSONObject(serverResponseMessage);
            if (responseobj.has(CODE)) {
                code = responseobj.getString(CODE);
                msg = responseobj.getString(MESSAGE);
                if (!responseobj.optString(CALLID).equals("")) {
                    callId = responseobj.getString(CALLID);
                    Log.wtf("Uploading logData to server:", code + ", " + msg + ", " + callId);
                }

                if (responseobj.has(RECORDING)) {
                    recording = responseobj.getString(RECORDING);
                    if (recording.equals("1")) {
                        ed.putBoolean("prefRecording", true);
                    } else {
                        ed.putBoolean("prefRecording", false);
                    }

                }
                if (responseobj.has(MCUBECALLS)) {
                    mcubeRecording = responseobj.getString(MCUBECALLS);
                    if (mcubeRecording.equals("1")) {
                        ed.putBoolean("prefMcubeRecording", true);
                    } else {
                        ed.putBoolean("prefMcubeRecording", false);
                    }

                }
                if (responseobj.has(WORKHOUR)) {
                    workhour = responseobj.getString(WORKHOUR);
                    if (workhour.equals("1")) {
                        ed.putBoolean("prefOfficeTimeRecording", true);
                    } else {
                        ed.putBoolean("prefOfficeTimeRecording", false);
                    }
                }


                ed.commit();
                Log.d(TAG1, "RESPONSE CODE:" + code);
                if (code.equals("400")) {
                    CallApplication.getWritabledatabase().delete(model.getId());//from database
                    if (new File(model.getFilePath()).exists()) {
                        Log.d(TAG1, "FILE DELETED" + ":" + model.getFile().getName());
                        Log.d("OnCal", "RECODRD DELETED" + ":" + model.getFile().getName());
                        new File(model.getFilePath()).delete();//from external storage
                    }
                    Log.d(TAG1, "RECODRD DELETED" + ":" + model.getFile().getName());


                }
                if (!code.equals("400")) {
                    if (responseobj.has(MESSAGE))
                        Utils.isLogoutBackground(context, responseobj.getString(MESSAGE));
                }


            }
        }

    }

    public static String size(long size) {

        //double m = size/1024.0;
        float m = ((float) Math.round((size / (1024 * 1024)) * 10) / 10);
        Log.wtf("In M", m + "");
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
            Log.wtf("MB file size", hrSize);
        } else {
            hrSize = dec.format(size).concat(" KB");
            Log.wtf("KB file size", hrSize);
        }
        return hrSize;
    }


    public static boolean onlineStatus2(Context activityContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }
        }
        //  Toast.makeText(mContext,mContext.getString(R.string.please_connect_to_internet),Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void makeAcall(String number, final Activity mActivity) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));
       /* callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            showMessageOKCancel("You need to allow access to Calls",
                    new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                                    MY_PERMISSIONS_CALL);
                        }
                    }, mActivity);
            return;

        }*/
        mActivity.startActivity(callIntent);

    }

    private static void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, Context mActivity) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static double tabletSize(Context context) {

        double size = 0;
        try {

            // Compute screen size

            DisplayMetrics dm = context.getResources().getDisplayMetrics();

            float screenWidth = dm.widthPixels / dm.xdpi;

            float screenHeight = dm.heightPixels / dm.ydpi;

            size = Math.sqrt(Math.pow(screenWidth, 2) +

                    Math.pow(screenHeight, 2));

        } catch (Throwable t) {

        }

        return size;

    }

    public static void sendSms(String number, Activity mActivity) {
        try {
            Uri uri = Uri.parse("smsto:" + number);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", "Enter SMS text");
            mActivity.startActivity(it);
        } catch (Exception e) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", " ");
            sendIntent.putExtra("address", number);
            sendIntent.setType("vnd.android-dir/mms-sms");
            mActivity.startActivity(sendIntent);
        }

    }

    public static void sendSMS(Context context, String number, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, null, null);
            Toast.makeText(context, "MTracker Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }


    }

    public static void sendSms(final Context context, String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "MTracker SMS Sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "MTracker was not unable to send SMS check balance ",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "MTracker SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "MTracker SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

//        String callFrom="";
//        if (phoneNumber.length()  > 10) {
//            callFrom = phoneNumber.substring(phoneNumber.length()-10, phoneNumber.length());
//        }
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        } catch (Exception e) {
            Log.d("EXCEPTION", e.getMessage().toString());
        }

    }


    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences prefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, boolean value) {
        // SharedPreferences prefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void isLogout(Context context) {
        //SharedPreferences sharedPrefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        File sampleDir;
        File sample;
        String selectedFolder = sharedPrefs.getString("store_path", "null");
        if (selectedFolder.equals("null")) {
            sampleDir = Environment.getExternalStorageDirectory();
            sample = new File(sampleDir.getAbsolutePath() + "/data/.tracker");
            if (!sample.exists()) sample.mkdirs();

        } else {
            sampleDir = new File(selectedFolder);
            sample = new File(sampleDir.getAbsolutePath() + "/.tracker");
            if (!sample.exists()) sample.mkdirs();
        }
        sharedPrefs.edit().clear().commit();
        //Welcome Pager on installation only
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();


        editor.putBoolean("is_first_run", false);
        editor.commit();
        List<File> files = getListFiles(sample);
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        CallApplication.getWritabledatabase().DeleteAllData();
        CallApplication.getInstance().stopRecording();
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d("Logout", "User Logout ");


    }

    //Delete false calls which creats 0 bytes record
    public static void deleteFalseFiles(Context context) {
        if (CallApplication.getWritabledatabase().getAllOfflineCalls().size() == 0) {
            // SharedPreferences sharedPrefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            File sampleDir, sample;
            String selectedFolder = sharedPrefs.getString("store_path", "null");
            if (selectedFolder.equals("null")) {
                sampleDir = Environment.getExternalStorageDirectory();
                sample = new File(sampleDir.getAbsolutePath() + "/data/.tracker");
                if (!sample.exists()) sample.mkdirs();

            } else {
                sampleDir = new File(selectedFolder);
                sample = new File(sampleDir.getAbsolutePath() + "/.tracker");
                if (!sample.exists()) sample.mkdirs();
            }

            List<File> files = Utils.getListFiles(sample);
            for (int i = 0; i < files.size(); i++) {
                files.get(i).delete();
                Log.d("FOLDERSIZE", sample.getAbsolutePath());
            }


        }
    }

    public static void isSimLogout(Context context) {
        //SharedPreferences sharedPrefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        File sampleDir;
        File sample;
        String selectedFolder = sharedPrefs.getString("store_path", "null");
        if (selectedFolder.equals("null")) {
            sampleDir = Environment.getExternalStorageDirectory();
            sample = new File(sampleDir.getAbsolutePath() + "/data/.tracker");
            if (!sample.exists()) sample.mkdirs();

        } else {
            sampleDir = new File(selectedFolder);
            sample = new File(sampleDir.getAbsolutePath() + "/.tracker");
            if (!sample.exists()) sample.mkdirs();
        }
        sharedPrefs.edit().clear().commit();
        List<File> files = getListFiles(sample);
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        CallApplication.getWritabledatabase().DeleteAllData();
        // CallApplication.getInstance().stopRecording();
        Log.d("Logout", "Logout onSim Changed");


    }


    public static void isLogoutBackground(Context context, String msg) {
        //SharedPreferences prefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
        File sampleDir = Environment.getExternalStorageDirectory();
        List<File> files = getListFiles(new File(sampleDir.getAbsolutePath() + "/Call Recorder"));
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        CallApplication.getWritabledatabase().DeleteAllData();
        CallApplication.getInstance().stopRecording();
        Log.d("Logout", "Background Logout");
        // Log.d("Logout", "Logout on Utils");
        showRecordNotification(context, msg);

    }

//    public static void cancelNotification(Context ctx, int notifyId) {
//        String ns = Context.NOTIFICATION_SERVICE;
//        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
//        nMgr.cancel(notifyId);
//    }


    public static void showOfflineRecordNotification(Context context) {
        Intent intent = new Intent(context, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .addAction(0, "Ok", pendingIntent)
                .setLargeIcon(bm)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setContentText("MTracker ");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    public static void showRecordNotification(Context context, String msg) {
        Intent intent = new Intent(context, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.BigTextStyle s = new NotificationCompat.BigTextStyle();
        s.setBigContentTitle("MTracker");
        //  s.bigText("You have been logout by Admin.");
        s.bigText(msg);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setLargeIcon(bm)
                .setStyle(s)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setContentText(msg);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();

        File[] files = parentDir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    if (file.getName().endsWith(".3gp") || file.getName().endsWith(".amr")) {
                        inFiles.add(file);
                    }
                }
            }
        return inFiles;
    }

    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

    public static String getFromPrefs(Context context, String key, String defaultValue) {
        //SharedPreferences sharedPrefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static Boolean isLogin(Context context) {
        // SharedPreferences sharedPrefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String authKey = sharedPrefs.getString(AUTHKEY, "n");
        String email = sharedPrefs.getString(EMAIL, "n");
        String password = sharedPrefs.getString(PASSWORD, "n");
        return !(authKey.equals("n") && email.equals("n") && password.equals("n"));

    }

    public static boolean isEmpty(String msg) {
        return msg == null
                || msg.trim().equals("")
                || msg.isEmpty();
    }


    public static Boolean getFromPrefsBoolean(Context context, String key, Boolean defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences sharedPrefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);

        try {
            return sharedPrefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void setRecording(Context context) {
        CallApplication.sp = context.getApplicationContext().getSharedPreferences("com.example.call", Context.MODE_PRIVATE);
        CallApplication.e = CallApplication.sp.edit();
        final Dialog dialog = new Dialog(context, R.style.myBackgroundStyle);
        dialog.setContentView(R.layout.layout_dialog);
        // dialog.setTitle("Set Your Record Preference");
        dialog.setTitle(Html.fromHtml("<font color='black'>Set Record Preferences</font>"));
        RadioGroup group = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
        //  final RelativeLayout rl = (RelativeLayout) dialog.findViewById(R.id.ask_layout);
        final TextView tv1 = (TextView) dialog.findViewById(R.id.r0);
        final TextView tv2 = (TextView) dialog.findViewById(R.id.r1);
        switch (CallApplication.sp.getInt("type", 0)) {
            case 0:
                group.check(R.id.radio0);
                break;
            case 1:
                group.check(R.id.radio1);
                break;
            default:
                break;
        }


        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio0:
                        CallApplication.e.putInt("type", 0);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.GONE);
                        break;
                    case R.id.radio1:
                        CallApplication.e.putInt("type", 1);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.GONE);
                        tv2.setVisibility(View.VISIBLE);
                        break;


                    default:
                        break;
                }
            }
        });
        Button save = (Button) dialog.findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CallApplication.e.commit();
                CallApplication.getInstance().resetServicee();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static boolean checkAndRequestPermissions(Context context) {
        int readConractsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        int readCallLog = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG);
        int readSmsConractsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        int sendSmsConractsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        int recordAudioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int readPhoneStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int writeExternalPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int outgoingCallPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.PROCESS_OUTGOING_CALLS);
        int systemAlertWindowPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
        int smsReceivePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        int callPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readCallLog != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (smsReceivePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (readConractsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (readSmsConractsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (sendSmsConractsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (recordAudioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (outgoingCallPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS);

        }
        if (systemAlertWindowPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static boolean isLocationEnabled(Context context) {
        return getLocationMode(context) != android.provider.Settings.Secure.LOCATION_MODE_OFF;
    }

    private static int getLocationMode(Context context) {
        return android.provider.Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
    }


    public static void displayNotification(Context context) {

        Intent action1Intent = new Intent(context, NotificationActionService.class)
                .setAction("Ok");

        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setLargeIcon(bm)
                .setOngoing(true)
                .setContentText("Offline records are full please sync now ")
                .addAction(new NotificationCompat.Action(0,
                        "Sync Now", action1PendingIntent));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if ("Ok".equals(action)) {
                // TODO: handle action 1.
                // If you want to cancel the notification:
                SyncUtils.TriggerRefresh();
                NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            }
        }
    }


    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }


    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        // return formatSize(availableBlocks * blockSize);
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        // return formatSize(totalBlocks * blockSize);
        return totalBlocks * blockSize;
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


    public static String getDirRecordsSize(Context context) {
        String Filesize = null;
        //SharedPreferences sharedPrefs =context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        File sampleDir, sample;
        String selectedFolder = sharedPrefs.getString("store_path", "null");
        if (selectedFolder.equals("null")) {
            sampleDir = Environment.getExternalStorageDirectory();
            sample = new File(sampleDir.getAbsolutePath() + "/data/.tracker");
            if (!sample.exists()) sample.mkdirs();

        } else {
            sampleDir = new File(selectedFolder);
            sample = new File(sampleDir.getAbsolutePath() + "/.tracker");
            if (!sample.exists()) sample.mkdirs();
        }
        Filesize = formatSize(getFolderSize(sample));
        return Filesize;
    }

    public static int getAvailableInternalMemroyPercent() {
        long am = Utils.getAvailableInternalMemorySize();
        long tm = Utils.getTotalInternalMemorySize();
        int percentage = (int) (am * 100.0 / tm + 0.5);
        return percentage;
    }


    public static String getCallLog(final Context context) {

        StringBuffer sb = new StringBuffer();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        //  Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, phonenumber);
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        // Log.e("total count", "" + managedCursor.getCount());
        //managedCursor.moveToPosition(managedCursor.getCount() - 1);
        int currentCount = 0, lastPosition = 0;
        String callDuration = null;

        while (managedCursor.moveToNext()) {
            currentCount++;
            //managedCursor.moveToPosition(managedCursor.getCount() - 1);
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);


            switch (dircode) {

                case CallLog.Calls.OUTGOING_TYPE:
                    //  lastPosition = currentCount;
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;

            }
            lastPosition = currentCount;
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
            //  Log.e("callcancel", sb.toString());
        }

        lastPosition--;
        managedCursor.moveToPosition(lastPosition);
        int requiredNumber = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int durations = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        String phNumber = managedCursor.getString(requiredNumber);
        String dur = managedCursor.getString(durations);
        Log.d("last position number ", phNumber);
        Log.d("last call Duration ", dur);
        managedCursor.close();

        return callDuration;
    }


}
