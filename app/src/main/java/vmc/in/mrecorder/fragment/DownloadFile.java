package vmc.in.mrecorder.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.google.android.gms.gcm.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.entity.LoginData;
import vmc.in.mrecorder.entity.OTPData;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Parser;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.util.SingleTon;

/**
 * Created by gousebabjan on 23/8/16.
 */
public class DownloadFile extends Fragment implements vmc.in.mrecorder.callbacks.TAG {

    private DownloadFileTask DCallbacks;
    private Download mTask;
    private String urlfile;
    private Home downloadTask;

    public DownloadFile() {
    }

    public interface DownloadFileTask {

        void ondownloadFilePreExecute();

        void ondownloadFileProgressUpdate(int percent);

        void ondownloadFileCancelled();

        void ondownloadFilePostExecute(File file);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            DCallbacks = (DownloadFileTask) context;
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        urlfile = getArguments().getString("FILE");

        mTask= new Download();
        mTask.execute();

    }


    @Override
    public void onDetach() {
        super.onDetach();
        DCallbacks = null;
    }
    public void onCancelTask () {

       // DCallbacks = null;
        if(!mTask.isCancelled()) {
            mTask.cancel(true);
           // Log.d("SHARE","DOWNLOAD CANCELLED");
        }
        DCallbacks.ondownloadFileCancelled();
    }


    private class Download extends AsyncTask<Void, String, File> {

        @Override
        protected void onPreExecute() {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFilePreExecute();
            }
        }


        @Override
        protected File doInBackground(Void... ignore) {
            File sample=null;
            int count;
            try {

                URL url = new URL(STREAM_TRACKER + urlfile);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                File sampleDir;
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                String selectedFolder = sharedPrefs.getString("store_path", "null");
                if (selectedFolder.equals("null")) {
                    sampleDir = Environment.getExternalStorageDirectory();
                    sample = new File(sampleDir.getAbsolutePath() + "/data/share/");
                    if (!sample.exists()) sample.mkdirs();

                } else {
                    sampleDir = new File(selectedFolder);
                    sample = new File(sampleDir.getAbsolutePath() + "/data/share/");
                    if (!sample.exists()) sample.mkdirs();
                }

                int lenghtOfFile = conexion.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(sample.getAbsolutePath() + "/" + urlfile);

                Log.d("PATH", sample.getAbsolutePath() + "/" + urlfile);
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                 // Thread.sleep(500);
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.d("FAIL DOWNLOAD", e.getMessage().toString());
            }

            return new File(sample.getAbsolutePath() + "/" + urlfile);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFileProgressUpdate(Integer.parseInt(progress[0]));
            }
        }

        @Override
        protected void onCancelled() {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFileCancelled();
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFilePostExecute(file);
            }

        }

    }
}
