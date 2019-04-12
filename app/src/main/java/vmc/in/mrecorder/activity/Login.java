package vmc.in.mrecorder.activity;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.LoginData;
import vmc.in.mrecorder.entity.OTPData;
import vmc.in.mrecorder.fragment.LoginTask;
import vmc.in.mrecorder.gcm.GCMClientManager;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.Utils;

public class Login extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,
        View.OnClickListener, TAG, LoginTask.TaskCallbacks {

    private static Login inst;
    Button btn_login, btn_getOtp;
    EditText et_email, et_password;
    TextView tv_otp, tv_mobile, privacy;
    String email, password, msgprogress;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private String OTP_Sms = "N/A", OTP_resp = "0000", gcmkey;
    private ProgressDialog progressDialog;
    public static final String DEAFULT = "";
    private boolean first = true;
    private LoginTask mTaskFragment;
    private Boolean showDialog = false;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private android.app.AlertDialog.Builder alertDialog;
    public DialogInterface Dialog;
    public ArrayList<OTPData> otps = new ArrayList<>();
    private CheckBox check_box;
    private String sessionID;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionID = CallApplication.getInstance().getSessionID();
        if (Utils.tabletSize(Login.this) < 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordi_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CallApplication.getInstance().stopRecording();
        Log.d("Alarm", "Login Triggered stop Recording");
        tv_otp = (TextView) findViewById(R.id.input_OTP);
        privacy=(TextView)findViewById(R.id.privacy);
        tv_mobile = (TextView) findViewById(R.id.mobile_OTP);
        btn_getOtp = (Button) findViewById(R.id.btn_get_otp);
        btn_login = (Button) findViewById(R.id.btn_login);
        check_box = (CheckBox) findViewById(R.id.checkBox_forgot);
        et_email = (EditText) findViewById(R.id.input_email);
        et_password = (EditText) findViewById(R.id.input_password);
        cancelNotification(this, NOTIFICATION_ID);
        mTaskFragment = (LoginTask) getSupportFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);
        Log.d("SESSION_ID", "Login " + sessionID);
        if (savedInstanceState != null) {
            OTP_resp = savedInstanceState.getString("OTP");
            OTP_Sms = savedInstanceState.getString("OTPS");
            String btn = savedInstanceState.getString("btn");
            Boolean show = savedInstanceState.getBoolean("show");
            otps = savedInstanceState.getParcelableArrayList("otps");
            sessionID = savedInstanceState.getString(SESSION_ID);
            first = savedInstanceState.getBoolean("first");
            if (show) {
                showTermsAlert();
                showDialog = show;
            }
            btn_login.setText(btn);
            if (OTP_resp != null && OTP_resp.length() > 5) {
                if (tv_otp.getVisibility() == View.GONE) {
                    tv_otp.setVisibility(View.VISIBLE);
                    tv_mobile.setVisibility(View.VISIBLE);
                    if (!OTP_Sms.equals("N/A")) {
                        tv_otp.setText(OTP_Sms);
                        tv_mobile.setText("");
                        tv_mobile.setVisibility(View.GONE);
                    } else {
                        tv_otp.setHint("Waiting for OTP..");
                    }

                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT > 19) {
            btn_login.setBackgroundResource(R.drawable.button_background);

        }
        btn_login.setOnClickListener(this);
        btn_getOtp.setOnClickListener(this);
        load();
        cancelNotification(Login.this, NOTIFICATION_ID);

        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d("Registration id", registrationId);
                gcmkey = registrationId;
                if (isNewRegistration) {
                    onRegisterGcm(registrationId);

                }
            }


        });


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.vmctechnologies.com/privacy-policy.html"));
                startActivity(browserIntent);
            }
        });


        et_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_password.getRight() - et_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (et_password.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // show password
                            et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                            et_password.clearFocus();
                            et_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.show, 0);
                        } else {
                            // hide password
                            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            et_password.clearFocus();
                            et_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.invisible, 0);

                        }


                        return false;
                    }
                }
                return false;
            }
        });
        et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Drawable drawable = ContextCompat.getDrawable(Login.this, R.drawable.error);
                drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
                String email = et_email.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (email.isEmpty() || (email.length() < 8 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                        et_email.setError("Enter a valid email address.", drawable);
                    }
                    return false;
                }
                return false;
            }
        });
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Drawable drawable = ContextCompat.getDrawable(Login.this, R.drawable.error);
                drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validateOTP()) {
                        hideKeyboard();
                    }
                    return true;
                }
                return false;
            }
        });

        logoAnimation();

//        if(Constants.isLogout &&!Utils.isMyServiceRunning(CallRecorderServiceAll.class,Login.this)){
//           Utils.sendSms("8296442713","MTracker Logged out.");
//            Constants.isLogout=false;
//
//        }


    }

    public void logoAnimation() {
        TranslateAnimation translation;
        translation = new TranslateAnimation(0f, 0F, 100f, 0f);
        translation.setStartOffset(500);
        translation.setDuration(2000);
        translation.setFillAfter(true);
        translation.setInterpolator(new BounceInterpolator());
        findViewById(R.id.logo).startAnimation(translation);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Dialog != null && showDialog) {
            Dialog.cancel();
        }
        outState.putString("OTP", OTP_resp);
        outState.putString(SESSION_ID, sessionID);
        outState.putString("OTPS", OTP_Sms);
        outState.putBoolean("show", showDialog);
        outState.putBoolean("first", first);
        outState.putParcelableArrayList("otps", otps);
        outState.putString("btn", btn_login.getText().toString());

    }

    public void onRegisterGcm(final String regid) {

        if (ConnectivityReceiver.isConnected()) {
            new RegisterGcm(regid).execute();
        } else {
            Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPreExecute() {

        progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msgprogress);
        progressDialog.setCancelable(false);
        progressDialog.show();


    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }


    @Override
    public void onTimerFinish() {
        btn_login.setEnabled(true);
        btn_login.setText("Resend OTP");
        Log.d("timer", "Btn enable true");
    }

    @Override
    public void onTimerUpdate(String time) {
        btn_login.setEnabled(false);
        btn_login.setText(time);
        Log.d("timer", "Time :" + time);
        Log.d("timer", "Btn enable false");
    }

    @Override
    public void onPostExecute(OTPData otpData) {
        if (otpData != null) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (tv_otp.getVisibility() == View.GONE) {
                tv_otp.setVisibility(View.VISIBLE);
                tv_mobile.setVisibility(View.VISIBLE);
            }

            tv_otp.setHint("Waiting for OTP");
          /*  if (otpData.getMobileNumber() != null) {

              *//*  TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();
                String mPhoneSerialNo = tMgr.getSimSerialNumber();
                String mPhoneIMEI = tMgr.getImei();
                Log.wtf("Status", "SIM Number Found " + mPhoneNumber + " " + mPhoneIMEI + " " + mPhoneSerialNo);*//*

                SharedPreferences sharedPrefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPrefs.edit();
                ed.putString("MNumber",otpData.getMobileNumber());
                ed.apply();
            }*/
            if (otpData.getMobileNumber() != null)
            tv_mobile.setText("OTP sent to " + otpData.getMobileNumber().substring(0, 2) + "*****" + otpData.getMobileNumber().substring(7));
            OTP_resp = otpData.getOtp();
            if (otpData.getCode().equals("202")) {
                btn_login.setText("Get OTP");
                Snackbar.make(coordinatorLayout, otpData.getMsg(), Snackbar.LENGTH_LONG).show();
                if (tv_otp.getVisibility() == View.VISIBLE) {
                    tv_otp.setVisibility(View.GONE);
                    tv_mobile.setVisibility(View.GONE);
                }

            } else {
                OTP_resp = otpData.getOtp();
                otps.add(otpData);

            }

        } else {
            Snackbar.make(coordinatorLayout, "Something went wrong try again later", Snackbar.LENGTH_SHORT).show();

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


    @Override
    public void onPostExecute(LoginData data) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (data != null) {
            Log.d("LOG", data.toString());

            btn_login.setEnabled(true);

            if (data.getCode().equals("202")) {
                btn_login.setText("Get OTP");

                Snackbar.make(coordinatorLayout, data.getMessage(), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StartLogin();

                            }
                        }).
                        setActionTextColor(ContextCompat.getColor(Login.this, R.color.accent)).show();
            }
            if (data.getCode().equals("400")) {

                save();

                Utils.saveToPrefs(Login.this, SESSION_ID, sessionID);
                Utils.saveToPrefs(Login.this, DEVICE_ID, CallApplication.getInstance().getDeviceId());
                Utils.saveToPrefs(Login.this, AUTHKEY, data.getAuthcode());
                Utils.saveToPrefs(Login.this, NAME, data.getUsername());
                Utils.saveToPrefs(Login.this, EMAIL, email);
                Utils.saveToPrefs(Login.this, USERTYPE, data.getUsertype());
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor ed = sharedPrefs.edit();
                if (data.getRecording().equals("1")) {
                    ed.putBoolean("prefRecording", true);
                    Log.d("LOG", "Recording" + data.getRecording());
                } else {
                    ed.putBoolean("prefRecording", false);
                }
                if (data.getWorkhour().equals("1")) {
                    ed.putBoolean("prefOfficeTimeRecording", true);
                    Log.d("LOG", "OfficeRecording" + data.getRecording());
                } else {
                    ed.putBoolean("prefOfficeTimeRecording", false);
                }
                if (data.getMcuberecording().equals("1")) {
                    ed.putBoolean("prefMcubeRecording", true);
                } else {
                    ed.putBoolean("prefMcubeRecording", false);
                }

                ed.commit();
                CallApplication.getInstance().startRecording();

                cancelNotification(this, NOTIFICATION_ID);

                Intent intent = new Intent(Login.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                overridePendingTransition(0, 0);
                Login.this.startActivity(intent);

            }


            if (data.getCode().equals("n")) {
                Snackbar.make(coordinatorLayout, "No Response From Server", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StartLogin();

                            }
                        }).
                        setActionTextColor(ContextCompat.getColor(Login.this, R.color.primary_dark)).show();
            }
        } else {
            Snackbar.make(coordinatorLayout, "Something went wrong try again later", Snackbar.LENGTH_SHORT).show();
        }
    }


    class RegisterGcm extends AsyncTask<Void, Void, String> {
        private String regid;

        public RegisterGcm(String regid) {
            this.regid = regid;
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public static Login instance() {
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    public void updateList(final String smsMessage) {
        if (smsMessage.substring(9, 15).matches("[0-9]+")) {

            btn_login.setEnabled(true);
            if (mTaskFragment != null) {
                mTaskFragment.cancelTimer();
            }
            OTP_Sms = smsMessage.substring(9, 15);
            //  String OTP1=smsMessage.split(": ")[0];
            Log.d("OTP test", OTP_resp);
            Log.d("SMS", OTP_Sms + " " + OTP);
            tv_otp.setText(OTP_Sms);
            tv_mobile.setText("");
            if (tv_mobile.getVisibility() == View.VISIBLE)
                tv_mobile.setVisibility(View.GONE);
            if (tv_otp.getVisibility() == View.GONE) {
                tv_otp.setVisibility(View.VISIBLE);
                // tv_mobile.setVisibility(View.VISIBLE);
            }
            // Log.d("OTP", tv_otp.getText().toString());
            btn_login.setText("Login");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        Utils.checkAndRequestPermissions(Login.this);
        tv_mobile.setText("");
        et_password.clearFocus();
        et_email.clearFocus();
        switch (v.getId()) {
            case R.id.btn_login:
                if (first && validateOTP()) {
                    showTermsAlert();
                    msgprogress = "Generating OTP..";
                    first = false;
                } else {
                    msgprogress = "Authenticating..";
                    startLogin();

                }
                break;

        }
    }

    private void startLogin() {
        if (btn_login.getText().toString().equals("Login")) {
            msgprogress = "Authenticating..";
            Login();
        } else if (validateOTP()) {
            msgprogress = "Generating OTP..";
            GetOtp();

        }
    }

    public void showTermsAlert() {
        showDialog = true;

        alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setTitle("MTracker");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        // Setting Dialog Message
        alertDialog.setMessage("You agree that MTracker will record all calls made through this device. ");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // dialog.cancel();
                startLogin();
                showDialog = false;

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                first = true;
                //  showDialog=true;
                Dialog = dialog;
                dialog.cancel();
                // Utils.isLogout(Login.this);
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    private void Login() {

        if (validate()) {

            //  if (OTP_resp != null && OTP_resp.equals(OTP_Sms)) {
            if (OTP_resp != null && otps.size() > 0) {
                for (OTPData data : otps) {
                    if (data.getOtp().equals(OTP_Sms)) {
                        StartLogin();
                    }

                }
            } else {

                btn_login.setEnabled(false);
                onLoginFailed();
            }
        } else {
            // Toast.makeText(getBaseContext(), "validate failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Invalid OTP try again", Toast.LENGTH_SHORT).show();
        btn_login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String OTP = tv_otp.getText().toString();

        Drawable drawable = ContextCompat.getDrawable(Login.this, R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));


        if (email.isEmpty() || (email.length() < 8 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            et_email.setError("Enter a valid email address.", drawable);
            //errormsg = "Enter a valid email address";
            valid = false;
        } else {
            et_email.setError(null);
        }

        if (password.isEmpty()) {
            et_password.setError("Password must not be empty.", drawable);
            // errormsg = "Password must be between 4 and 10 alphanumeric characters";
            valid = false;
        } else {
            et_password.setError(null);
        }
        if (OTP.isEmpty() || OTP.length() < 6) {
            // tv_otp.setError("Wait for OTP Message", drawable);
            // errormsg = "Password must be between 4 and 10 alphanumeric characters";
            valid = false;
        } else {
            // tv_otp.setError(null);
        }

//        if (!terms.isChecked()) {
//            Snackbar snack = Snackbar.make(mroot, "Accept terms and condition to proceed", Snackbar.LENGTH_SHORT)
//                    .setAction(null, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    })
//                    .setActionTextColor(getResources().getColor(R.color.accent));
//            TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
//            tv.setTextColor(Color.WHITE);
//            snack.show();
//            valid = false;
//        } else {
//            _repassword.setError(null);
//        }

        return valid;
    }


    public boolean validateOTP() {
        boolean valid = true;

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String OTP = tv_otp.getText().toString();

        Drawable drawable = ContextCompat.getDrawable(Login.this, R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));

        if (email.isEmpty() || (email.length() < 8 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            et_email.setError("Enter a valid email address", drawable);
            //errormsg = "Enter a valid email address";

            valid = false;
        } else {
            et_email.setError(null);
        }

        if (password.isEmpty()) {
            et_password.setError("Password must not be empty.", drawable);
            // errormsg = "Password must be between 4 and 10 alphanumeric characters";
            valid = false;
        } else {
            et_password.setError(null);
        }
        return valid;
    }


    public void load() {
        SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
        String email = pref.getString("email", DEAFULT);
        String password = pref.getString("password", DEAFULT);

        if (!email.equals(DEAFULT) || !password.equals(DEAFULT)) {
            et_email.setText(email);
            et_password.setText(password);
            et_email.clearFocus();
            et_password.clearFocus();
        }
        if (password.equals("")) {
            //  et_password.requestFocus();
            //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void save() {
        if (check_box.isChecked()) {
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();
            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.commit();

        } else {
//            SharedPreferences prefs = this.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
//            prefs.edit().clear().commit();
            String email = et_email.getText().toString();
            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("email", "");
            editor.putString("password", "");
            editor.commit();
        }
    }


    public synchronized void GetOtp() {
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();
        if (ConnectivityReceiver.isConnected()) {
            if (tv_otp.getText().toString().length() == 0 || tv_otp.getText().toString().equals("")) {
                //btn_login.setText("Resend OTP");

            }
            // new GetOtp(email, password).execute();

            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("password", password);
            bundle.putBoolean("ISOTP", true);
            bundle.putString(SESSION_ID, sessionID);
            mTaskFragment = new LoginTask();
            mTaskFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();

        } else {
            Snackbar snack = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetOtp();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(Login.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }


    public void StartLogin() {

        if (ConnectivityReceiver.isConnected()) {
            // new StartLogin().execute();
            email = et_email.getText().toString().trim();
            password = et_password.getText().toString().trim();
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("password", password);
            bundle.putString("gcm", gcmkey);
            bundle.putBoolean("ISOTP", false);
            bundle.putString(SESSION_ID, sessionID);
            mTaskFragment = new LoginTask();
            mTaskFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();

        } else {

            Snackbar snack = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            StartLogin();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(Login.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }


    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        CallApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Toast.makeText(Login.this, "Exit Login", Toast.LENGTH_SHORT).show();
        System.exit(0);
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//        this.doubleBackToExitPressedOnce = true;
//
//        //Toasty.custom(Home.this, "Press again to exit.", null, ContextCompat.getColor(Home.this, R.color.black), ContextCompat.getColor(Home.this, R.color.white), 20, false, true).show();
//        Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);

    }

}