package vmc.in.mrecorder.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

import org.json.JSONObject;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.SingleTon;
import vmc.in.mrecorder.util.Utils;

public class Feedback extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, TAG,
        RatingBar.OnRatingBarChangeListener {
    @BindView(R.id.etfeedback)
    EditText etFeedback;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.root)
    RelativeLayout mroot;
    String feedbackmsg;
    private Toolbar toolbar;
    private String authkey;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    boolean isAnimated = false;
    private TextView lblHowHappy, lblWeHearFeedback, txtThanks;
    private RatingBar ratingBar;
    EditText txtComments;
    LinearLayout layoutForm;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lblHowHappy = (TextView) findViewById(R.id.lblHowHappy);
        lblWeHearFeedback = (TextView) findViewById(R.id.lblWeHearFeedback);
        txtThanks = (TextView) findViewById(R.id.lblThanksFeedback);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        layoutForm = (LinearLayout) findViewById(R.id.layoutForm);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        txtComments = (EditText) findViewById(R.id.txtComments);
        if (android.os.Build.VERSION.SDK_INT > 19) {
            btnSubmit.setBackgroundResource(R.drawable.button_background);

        }
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        initializeUI();
        authkey = Utils.getFromPrefs(this, AUTHKEY, "N/A");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                feedbackmsg = txtComments.getText().toString();
                if (!(feedbackmsg.length() == 0 || feedbackmsg.isEmpty() || feedbackmsg.equals(""))) {
                    UpdateFeedBack();

                } else {
                    Toast.makeText(getApplication(), "Enter Your Comments", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void initializeUI() {
        // Setting Initial Settings for UIs
        ratingBar.setRating(0);
        layoutForm.setVisibility(View.INVISIBLE);

        // Setting listeners
        ratingBar.setOnRatingBarChangeListener(this);

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float value, boolean b) {
        if (isAnimated == false) {
            // show hidden views
            layoutForm.setVisibility(View.VISIBLE);

            // Work out animations
            ViewAnimator
                    // Initial Label
                    .animate(lblHowHappy)
                    .dp().translationY(0, -100)
                    .alpha(1, 0)
                    .duration(350)
                    .interpolator(new LinearOutSlowInInterpolator())
                    // Rating bar
                    .andAnimate(ratingBar)
                    .dp().translationY(0, -100)
                    .duration(450)
                    .interpolator(new LinearOutSlowInInterpolator())
                    // Layout Form
                    .andAnimate(layoutForm)
                    .dp().translationY(0, -100)
                    .singleInterpolator(new LinearOutSlowInInterpolator())
                    .duration(450)
                    .alpha(0, 1)
                    .interpolator(new FastOutSlowInInterpolator())
                    // Label feedback of form
                    .andAnimate(lblWeHearFeedback)
                    .dp().translationY(0, -20)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(300)
                    .alpha(0, 1)
                    // Commects edittext
                    .andAnimate(txtComments)
                    .dp().translationY(30, -30)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(550)
                    .alpha(0, 1)
                    // Submit button
                    .andAnimate(btnSubmit)
                    .dp().translationY(60, -35)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(800)
                    .alpha(0, 1)
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            isAnimated = true;
                        }
                    })
                    .start();
        }
    }


    public void onSubmitClickAnimation() {
        if (isAnimated) {
            // Feedback has been written
            txtThanks.setVisibility(View.VISIBLE);

            // Perfrom Animations
            ViewAnimator
                    .animate(ratingBar)
                    .dp().translationY(-100, -130)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(200)
                    .alpha(1, 0)
                    .andAnimate(lblWeHearFeedback)
                    .dp().translationY(-20, -90)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(250)
                    .alpha(1, 0)
                    .andAnimate(txtComments)
                    .dp().translationY(-30, -120)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(300)
                    .alpha(1, 0)
                    .andAnimate(btnSubmit)
                    .dp().translationY(-35, -200)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(340)
                    .alpha(1, 0)
                    .andAnimate(txtThanks)
                    .dp().translationY(0, -200)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(600)
                    .start();
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                // imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
            }

        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }

    //Submit feedback
    public void UpdateFeedBack() {

        if (ConnectivityReceiver.isConnected()) {
            new SubmitUpdateFeedBack().execute();
        } else {
            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UpdateFeedBack();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(Feedback.this, R.color.accent));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
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
                    .make(mroot, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    class SubmitUpdateFeedBack extends AsyncTask<Void, Void, String> {
        String message = "n";
        String code = "n";
        JSONObject response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnSubmit.setText("Sending....");
            hideKeyboard();

        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                response = Requestor.requestFeedback(requestQueue, GET_FEED_BACK_URL, authkey, feedbackmsg);
                Log.d(TAG, response.toString());


                if (response.has(CODE)) {
                    code = response.getString(CODE);

                }
                if (response.has(MESSAGE)) {
                    message = response.getString(MESSAGE);
                }


            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return code;
        }

        @Override
        protected void onPostExecute(String data) {

            if (data.equals("400")) {
                onSubmitClickAnimation();
                // Toast.makeText(Feedback.this, "Feedback Submitted Sucessfully", Toast.LENGTH_SHORT).show();

            } else {
                btnSubmit.setText("Send");
                Toast.makeText(Feedback.this, "Server busy! Please Try again Later", Toast.LENGTH_SHORT).show();
            }


        }


    }

}
