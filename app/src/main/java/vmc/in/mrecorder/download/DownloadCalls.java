package vmc.in.mrecorder.download;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.ArrayList;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.MDatabase;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.provider.GPSTracker;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.SingleTon;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by gousebabjan on 15/9/16.
 */
public class DownloadCalls extends AsyncTask<Void, Void, ArrayList<CallData>> implements vmc.in.mrecorder.callbacks.TAG {

    private final GPSTracker mGPS;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private CallReportFinish downloadFininshed;
    private String type, offset, limit, sessionId, authkey;
    private ArrayList<CallData> callDataArrayList;
    private boolean isMore;
    private Context context;
    private boolean isOnline;
    private String code,msg;

    public interface CallReportFinish {
        void onCallReportDownLoadFinished(ArrayList<CallData> result, boolean isMore,String code,String msg);
    }


    public DownloadCalls(CallReportFinish downloadFininshed, Context context, String type, String offset, boolean isMore) {
        this.downloadFininshed = downloadFininshed;
        this.context = context;
        this.type = type;
        this.offset = offset;
        this.isMore = isMore;
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        mGPS = new GPSTracker(context);
    }


    @Override
    protected ArrayList<CallData> doInBackground(Void... params) {
        JSONObject response = null;
        callDataArrayList = new ArrayList<CallData>();
        isOnline = ConnectivityReceiver.isOnline();
        //if(isOnline) {
            try {

                response = Requestor.requestGetCalls(requestQueue, GET_CALL_LIST, Utils.getFromPrefs(context, AUTHKEY, "N/A"), "10", offset + "",
                        Utils.getFromPrefs(context, SESSION_ID, UNKNOWN), type,mGPS);
                Log.d(TAG, response.toString());
                if (response != null) {
                    callDataArrayList = vmc.in.mrecorder.parser.Parser.ParseData(response);
                    if (response.has(CODE)) {
                        code=response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg=response.getString(MESSAGE);
                    }
                }
            } catch (Exception e) {
                // Log.d("ERROR", e.getMessage().toString());
            }
       // }
        return callDataArrayList;
    }


    @Override
    protected void onPostExecute(ArrayList<CallData> callData) {
        super.onPostExecute(callData);

        if (callData != null && callData.size() > 0) {
            CallApplication.getWritabledatabase().insertCallRecords(type.equals(TYPE_ALL) ? MDatabase.ALL :
                    type.equals(TYPE_INCOMING) ? MDatabase.INBOUND :
                            type.equals(TYPE_OUTGOING) ? MDatabase.OUTBOUND :
                                    MDatabase.MISSED, callData, !isMore);
        }
        if (downloadFininshed != null)
            downloadFininshed.onCallReportDownLoadFinished(callData, isMore,code,msg);
    }
}

