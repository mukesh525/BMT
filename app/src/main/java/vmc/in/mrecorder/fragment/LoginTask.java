package vmc.in.mrecorder.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONException;
import org.json.JSONObject;

import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.LoginData;
import vmc.in.mrecorder.entity.OTPData;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Parser;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.SingleTon;

/**
 * Created by gousebabjan on 19/8/16.
 */
public class LoginTask extends Fragment implements vmc.in.mrecorder.callbacks.TAG {

    private final long startTime = 60000;
    private final long interval = 1000;
    private RequestQueue requestQueue;
    private TaskCallbacks mCallbacks;
    private Login mTask;
    private SingleTon volleySingleton;

    private String email, password, gcmkey;
    private LoginData loginData;
    private OTPData otpData;
    private boolean isOTP;
    private JSONObject response;
    private Timer countDownTimer;
    private String sessionID;
    private boolean isOnline;

    public interface TaskCallbacks {
        void onPreExecute();

        void onProgressUpdate(int percent);

        void onCancelled();

        void onTimerFinish();

        void onTimerUpdate(String time);

        void onPostExecute(OTPData otp);

        void onPostExecute(LoginData loginData);

    }

    public LoginTask() {
    }


    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mCallbacks = (TaskCallbacks) context;
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        email = getArguments().getString("email");
        password = getArguments().getString("password");
        isOTP = getArguments().getBoolean("ISOTP");
        gcmkey = getArguments().getString("gcm");
        sessionID = getArguments().getString(SESSION_ID);
        mTask = new Login();
        countDownTimer = new Timer(startTime, interval);
        mTask.execute();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    public class Timer extends CountDownTimer {

        public Timer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {

            mCallbacks.onTimerFinish();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //  text.setText("Time remain:" + millisUntilFinished);
            // timeElapsed = startTime - millisUntilFinished;
            if(mCallbacks!=null)
            mCallbacks.onTimerUpdate(convertMilisecToHMmSs(millisUntilFinished));
        }
    }

    public static String convertMilisecToHMmSs(long millisUntilFinished) {
        long s = (millisUntilFinished / 1000) % 60;
        long m = ((millisUntilFinished / 1000) / 60) % 60;
        long h = ((millisUntilFinished / 1000) / (60 * 60)) % 24;
        //return String.format("%d:%02d:%02d", h,m,s);
        return String.format("%02d:%02d", m, s);
    }


    private class Login extends AsyncTask<Void, Integer, Void> {


        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }


        @Override
        protected Void doInBackground(Void... ignore) {
            isOnline = ConnectivityReceiver.isOnline();
           // if (isOnline) {
                try {
                    if (isOTP) {
                        JSONObject jsonObject = null;
                        jsonObject = Requestor.requestOTP(requestQueue, GET_OTP, email, password);
                        if (jsonObject != null) {
                            otpData = Parser.ParseOTPResponse(jsonObject);
                        }
                    } else {

                        JSONObject jsonObject = null;
                        jsonObject = Requestor.requestLogin(requestQueue, LOGIN_URL, email, password, sessionID, gcmkey, DeviceName.getDeviceName());
                        if (jsonObject != null)
                            loginData = Parser.ParseLoginResponse(jsonObject);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
               // }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            if (mCallbacks != null) {
                //mCallbacks.onProgressUpdate(percent[0]);
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(Void ignore) {
            if (mCallbacks != null) {
                if (isOTP) {
                    mCallbacks.onPostExecute(otpData);

                    if (otpData != null && otpData.getCode().equals("400"))
                        countDownTimer.start();
                } else {
                    mCallbacks.onPostExecute(loginData);
                }
            }
        }

    }
}
