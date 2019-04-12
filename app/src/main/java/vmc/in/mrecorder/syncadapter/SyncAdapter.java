/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vmc.in.mrecorder.syncadapter;

import android.Manifest;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.RequestQueue;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.callbacks.CallList;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.MDatabase;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Parser;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.provider.GPSTracker;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.ErrorReport;
import vmc.in.mrecorder.util.SingleTon;
import vmc.in.mrecorder.util.Utils;


public class SyncAdapter extends AbstractThreadedSyncAdapter implements TAG {

    private static CallList mListener;
    private final ContentResolver mContentResolver;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            //Toast.makeText(getContext(), mGPS.getLatitude() + "," + mGPS.getLongitude(), Toast.LENGTH_LONG).show();
        }
    };
    public String TAG1 = "sync_adapter";
    private Context mContext;
    private String sessionID;
    private ArrayList<Model> callList;
    private JSONObject response;
    private String authkey;
    private int offset = 0;
    private ArrayList<CallData> callDataArrayList;
    private String code, msg, recording, mcubeRecording, workhour, isLogg;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private boolean debugEnable;
    private GPSTracker mGPS;
    private String callStatus;
    private int count = 0;
    private String callDuration, callType;
    private String hrSize = "";


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mContext = context;
        sessionID = Utils.getFromPrefs(context, SESSION_ID, UNKNOWN);
        //  mGPS = CallApplication.getInstance().getLocation();
        mGPS = new GPSTracker(mContext);

        Log.d("SESSION_ID", "Syncadapeter Constructor " + sessionID);

    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    public static void bindListener(CallList listener) {
        mListener = listener;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        sessionID = Utils.getFromPrefs(getContext(), SESSION_ID, UNKNOWN);
        debugEnable = sharedPrefs.getBoolean("prefDebug", false);

        Message msg = handler.obtainMessage();
        msg.obj = "LATLONG";
        handler.sendMessage(msg);
        Log.d(TAG1, "Beginning network synchronization");
        if (Utils.isLogin(getContext())) {
            CallApplication.getInstance().startRecording();
            Log.d("Alarm", "Sync Adapter Triggered start Recording");
        } else {
            CallApplication.getInstance().stopRecording();
            Log.d("Alarm", "Sync Adapter Triggered stop Recording");
        }
        if (!CallRecorderServiceAll.recording && Utils.isLogin(getContext())) {
            LoadCalls();
        }
        callList = CallApplication.getWritabledatabase().getAllOfflineCalls();
        Log.e("sync_adapter: ", "call_list_size: " + callList);
        if (callList != null & callList.size() > 0) {
            for (Model model : callList) {
                boolean isValid = !CallRecorderServiceAll.recording || !model.getPhoneNumber().equals(CallRecorderServiceAll.onCallRecordNumber);
                if (isValid)
                    new LongOperation().execute(model);

            }
        }


    }

    private synchronized void LoadCalls() {
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor ed = sharedPrefs.edit();
        CallApplication.getInstance().startRecording();
        Log.d("Alarm", "SyncAdapter LoadCalls Triggered start Recording");
        callDataArrayList = new ArrayList<CallData>();
        authkey = Utils.getFromPrefs(getContext(), AUTHKEY, "N/A");
        Log.wtf("Auth key", authkey);
        try {
            response = Requestor.requestGetCalls(requestQueue, GET_CALL_LIST, authkey, "10", offset + "", sessionID, TYPE_ALL, mGPS);
            CallApplication.getWritabledatabase().insertCallRecords(MDatabase.ALL, Parser.ParseData(response), true);

            response = Requestor.requestGetCalls(requestQueue, GET_CALL_LIST, authkey, "10", offset + "",
                    sessionID, TYPE_INCOMING, mGPS);
            CallApplication.getWritabledatabase().insertCallRecords(MDatabase.INBOUND, Parser.ParseData(response), true);

            response = Requestor.requestGetCalls(requestQueue, GET_CALL_LIST, authkey, "10", offset + "",
                    sessionID, TYPE_OUTGOING, mGPS);
            CallApplication.getWritabledatabase().insertCallRecords(MDatabase.OUTBOUND, Parser.ParseData(response), true);

            response = Requestor.requestGetCalls(requestQueue, GET_CALL_LIST, authkey, "10", offset + "",
                    sessionID, TYPE_MISSED, mGPS);
            CallApplication.getWritabledatabase().insertCallRecords(MDatabase.MISSED, Parser.ParseData(response), true);


            if (response != null) {
                if (response.has(CODE)) {
                    code = response.getString(CODE);
                    if (response.has(RECORDING)) {
                        recording = response.getString(RECORDING);
                        if (recording.equals("1")) {
                            ed.putBoolean("prefRecording", true);
                        } else {
                            ed.putBoolean("prefRecording", false);
                        }


                    }
                    if (response.has(MCUBECALLS)) {
                        mcubeRecording = response.getString(MCUBECALLS);
                        if (mcubeRecording.equals("1")) {
                            ed.putBoolean("prefMcubeRecording", true);
                        } else {
                            ed.putBoolean("prefMcubeRecording", false);
                        }

                    }
                    if (response.has(WORKHOUR)) {
                        workhour = response.getString(WORKHOUR);
                        if (workhour.equals("1")) {
                            ed.putBoolean("prefOfficeTimeRecording", true);
                        } else {
                            ed.putBoolean("prefOfficeTimeRecording", false);
                        }
                    }

                    if (response.has(DEBUG)) {
                        if (response.getString(DEBUG).equals("1")) {
                            ed.putBoolean("prefDebug", true);
                        } else {
                            ed.putBoolean("prefDebug", false);
                        }

                    }

                    ed.commit();
                    if (!code.equals("400")) {
                        if (response.has(MESSAGE))
                            Utils.isLogoutBackground(getContext(), response.getString(MESSAGE));
                    }
                    if (code.equals("203")) {
                        // Utils.isLogoutBackground(getContext());
                        Log.d("NORECORD", "Record is not in Mcube contacts.");
                    }

                }


//              if (callDataArrayList.size() > 0 & mListener != null) {
//                    mListener.allCalls(callDataArrayList);
//                }


            }
        } catch (Exception e) {
            Log.d(TAG1, "Error " + e.getMessage().toString());
            StringWriter sw = new StringWriter();
            // PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(new PrintWriter(sw));
            sw.toString(); // stack trace as a string
            //Send Error report to server
            String error = "\r\n SyncAdapter getlistAll  Line 272 " + "\r\n " + sw.toString();
            Log.d("exp", " startRecording Method exp " + error);
            if (Utils.isLogin(mContext) && debugEnable)
                new ErrorReport().sendError(error, mContext);
        }

    }

    private void StartOrStopRecording() {
        if (Utils.isLogin(getContext())) {
            if (!Utils.isMyServiceRunning(CallRecorderServiceAll.class, getContext())) {
                CallApplication.getInstance().startRecording();
                Log.d(TAG1, "service started");
            } else {
                Log.d(TAG, "service already started");
            }
        } else {
            if (Utils.isMyServiceRunning(CallRecorderServiceAll.class, getContext())) {
                CallApplication.getInstance().stopRecording();
                Log.d(TAG, "service stopped");
            } else {
                Log.d(TAG, "service already stopped");
            }
        }
    }

    private void uploadMultipartData(Model model, boolean fileExist) throws Exception {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor ed = sharedPrefs.edit();

        Log.d(TAG1, "uploadMultipartData:" + model.getPhoneNumber());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SS");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        int duration = 0;
        long fileSize = 0;
        if (fileExist) {
            try {
                MediaPlayer mp = MediaPlayer.create(getContext(), Uri.fromFile(model.getFile()));
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
        builder.addPart(AUTHKEY, new StringBody(Utils.getFromPrefs(getContext(), AUTHKEY, "n"), ContentType.TEXT_PLAIN));
        Log.d(TAG1, AUTHKEY + ":" + Utils.getFromPrefs(getContext(), AUTHKEY, "n"));
        builder.addPart(DEVICE_ID, new StringBody(Utils.getFromPrefs(getContext(), SESSION_ID, UNKNOWN), ContentType.TEXT_PLAIN));
        Log.d(TAG1, DEVICE_ID + ":" + Utils.getFromPrefs(getContext(), SESSION_ID, UNKNOWN));
        builder.addPart(CALLTO, new StringBody(model.getPhoneNumber(), ContentType.TEXT_PLAIN));
        Log.d(TAG1, CALLTO + ":" + model.getPhoneNumber());
        builder.addPart(STARTTIME, new StringBody(sdf.format(new Date(Long.parseLong(model.getTime()))), ContentType.TEXT_PLAIN));
        Log.d(TAG1, STARTTIME + ":" + sdf.format(new Date(Long.parseLong(model.getTime()))));
        Log.e("call_duration: ", " start_time: &&&& " + STARTTIME);
        builder.addPart(CALLTYPEE, new StringBody(model.getCallType(), ContentType.TEXT_PLAIN));
        builder.addPart(CONTACTNAME, new StringBody(getContactName(model.getPhoneNumber()), ContentType.TEXT_PLAIN));
        Log.wtf(TAG1, CALLTYPEE + ":" + model.getCallType());
        Log.d(TAG1, "CONTACTNAME" + ":" + getContactName(model.getPhoneNumber()));
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
                if (response.has(DEBUG)) {
                    if (response.getString(DEBUG).equals("1")) {
                        ed.putBoolean("prefDebug", true);
                    } else {
                        ed.putBoolean("prefDebug", false);
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
                        Utils.isLogoutBackground(getContext(), responseobj.getString(MESSAGE));
                }


            }
        } else {
            String error = "\r\n Server response code error " + "\r\n " + status;
            if (Utils.isLogin(mContext) && debugEnable)
                new ErrorReport().sendError(error, mContext);
        }


    }

    private void alertDialog(final Model model) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete a file.!!");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteRecordedFileFromLocal(model);
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                count = 0;
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //outgoing

    private void deleteRecordedFileFromLocal(Model model) {
        CallApplication.getWritabledatabase().delete(model.getId());//from database
        if (new File(model.getFilePath()).exists()) {
            Log.d(TAG1, "FILE DELETED" + ":" + model.getFile().getName());
            Log.d("OnCal", "RECODRD DELETED" + ":" + model.getFile().getName());
            new File(model.getFilePath()).delete();//from external storage
        }
        Log.d(TAG1, "RECODRD DELETED" + ":" + model.getFile().getName());
    }

    private void updateFilenameToServer(String name, String authKey, String callId) {
        //Call server to update a file path
        Log.wtf("Update Recording URL Parameters:", name + ", " + authKey + ", " + callId);
        try {
            response = Requestor.updateRecordingFile(requestQueue, UPDATE_RECORDING_FILE, name, authKey, callId);
            Log.d(TAG, response.toString());
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage().toString());
        }
        if (response != null) {

            try {

                if (response.has(CODE)) {
                    code = response.getString(CODE);
                    Log.wtf("update recording file:", code);
                }
                if (response.has(MESSAGE)) {
                    msg = response.getString(MESSAGE);
                    Log.wtf("update recording file:", msg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Model getFilterCallDetai(Model model1) {
        String whereClause = CallLog.Calls.NUMBER + " = " + model1.getPhoneNumber() + " AND " + CallLog.Calls.TYPE + "=" + CallLog.Calls.OUTGOING_TYPE;
        StringBuffer sb = new StringBuffer();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // return ;
        }
        Cursor managedCursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, whereClause,
                null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        ArrayList<Model> templog = new ArrayList<Model>();

        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            Model model = new Model();
            String phNumber = managedCursor.getString(number);
            model.setPhoneNumber(phNumber);
            String callType = managedCursor.getString(type);
            model.setCallType(callType);
            String callDate = managedCursor.getString(date);
            model.setTime(callDate);
            Date callDayTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
            String exacttime = sdf.format(callDayTime);
            String callDuration = managedCursor.getString(duration);
            Log.e("call_duration: ", " ### " + callDuration);
            model.setDuration(callDuration);
            templog.add(model);

        }
        for (int i = 0; i < templog.size(); i++) {

            Model model = templog.get(i);
            if (model.getPhoneNumber().equals(model1.getPhoneNumber())) {
                //     Log.d("Numbers", "Number equal" + model1.getPhoneNumber());
                Date callDayTime = new Date(Long.valueOf(model.getTime()));
                Date callDayTime1 = new Date(Long.valueOf(model1.getTime()));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
                String time1 = sdf.format(callDayTime);
                String time2 = sdf.format(callDayTime1);
                long seconds = (callDayTime1.getTime() - callDayTime.getTime()) / 1000;
                // Log.d("Numbers1", model1.getPhoneNumber() + " " +"Actual Time"+ time2 +" Log Time " +time1 +" Diffrence in Sec " +seconds);

                if (seconds < 5) {
                    model1.setDuration(model.getDuration());
                    Log.e("call_duration: ", " $$$ " + model.getDuration());
                    if (Integer.parseInt(model.getDuration()) == 0) {
                        if (new File(model1.getFilePath()).exists()) {
                            Log.d("Numbers1", "Call To be Deleted");
                            Log.d("Numbers1", model1.getPhoneNumber() + " " + "Actual Time" + time2 + " Log Time " + time1 + " Diffrence in Sec " + seconds);
                            Log.d("Numbers1", "Duration " + model.getDuration());
                            new File(model1.getFilePath()).delete();//from internal storage
                            Log.d("Numbers1", "FILE DELETED" + ":" + model1.getFile().getName());
                        }
                    }
                }


            }


        }
        managedCursor.close();


        return model1;

    }

    public String getContactName(String snumber) throws Exception {
        ContentResolver cr = getContext().getContentResolver();
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

    private void retriveCallSummary() {
        Log.wtf("**CallSummary**", "");

        // Uri contacts = CallLog.Calls.CONTENT_URI;
        //Cursor managedCursor = mContext.getContentResolver().query(
        //contacts, null, null, null, null);

        StringBuffer sb = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
        }
        Cursor managedCursor = null;
        managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null,
                null, CallLog.Calls.DATE + " DESC");

        //int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        long dur = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        Log.e("call_duration: ", " tim****" + dur);

        if (managedCursor.moveToFirst() == true) {
            //String phNumber = managedCursor.getString( number );
            callType = managedCursor.getString(type);
            callDuration = managedCursor.getString(duration1);
            String dir = null;
            /*sb.append( "\nPhone Number:--- "+phNumber +" \nCall duration in sec :--- "+callDuration );
            sb.append("\n----------------------------------");
           // Log.wtf("*****Call Summary******","Call Duration is:-------"+sb);
            Log.wtf("Duration in method",callDuration);*/

        }
        managedCursor.close();
    }

    public String size(long size) {

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


    private class LongOperation extends AsyncTask<Model, Void, Model> {

        @Override
        protected Model doInBackground(Model... params) {
            try {
                uploadMultipartData(params[0], params[0].getFile().exists() && params[0].getFile().canRead());
            } catch (Exception e) {
                Log.d(TAG1, e.getMessage());
                CallApplication.getWritabledatabase().delete(params[0].getId());//from database
                if (new File(params[0].getFilePath()).exists()) {
                    Log.d(TAG1, "FILE DELETED" + ":" + params[0].getFile().getName());
                    new File(params[0].getFilePath()).delete();//from external storage
                }
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(Model result) {
            Log.d(TAG1, "TASK FINSIHED " + result.getPhoneNumber());
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG1, "TASK EXCECUTED");
        }


    }
}
