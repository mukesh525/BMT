package vmc.in.mrecorder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.util.Utils;

public class Welcome extends AppCompatActivity implements TAG {
    private static int SPLASH_TIME_OUT = 3000;
    Boolean splashShown;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        if (Utils.tabletSize(Welcome.this) < 6.0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        intentNextActivityAfterSeconds(SPLASH_TIME_OUT);
//        splashShown = Utils.getFromPrefsBoolean(Welcome.this, SHOWN, false);
//        if (splashShown) {
//
//            if (Utils.isLogin(Welcome.this)) {
//                i = new Intent(Welcome.this, Home.class);
//            } else {
//                i = new Intent(Welcome.this, Login.class);
//            }
//            startActivity(i);
//
//        } else {
//            Utils.saveToPrefs(Welcome.this, SHOWN, true);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    if (Utils.isLogin(Welcome.this)) {
//                        i = new Intent(Welcome.this, Home.class);
//                    } else {
//                        i = new Intent(Welcome.this, Login.class);
//                    }
//                    startActivity(i);
//
//                }
//            }, SPLASH_TIME_OUT);
//
//        }

    }


    private void intentNextActivityAfterSeconds(long seconds) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences sharedPreferences =this.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);


        boolean isFirstRun = sharedPreferences.getBoolean("is_first_run", true);
        Intent intentTo;
        if (isFirstRun) {
            //SharedPreferences.Editor editor =this.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit();
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("is_first_run", false);
            editor.commit();
            intentTo = new Intent(Welcome.this, WelcomePager.class);
        } else if (Utils.isLogin(Welcome.this)) {

            intentTo = new Intent(Welcome.this, Home.class);
            intentTo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        } else {
            intentTo = new Intent(Welcome.this, Login.class);
            intentTo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        }

        //intentTo = new Intent(Welcome.this, WelcomePager.class);

        final Intent intent = intentTo;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, seconds);
    }
}
