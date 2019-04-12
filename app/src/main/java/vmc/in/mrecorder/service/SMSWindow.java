package vmc.in.mrecorder.service;

/**
 * Created by vmc on 14/2/17.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.util.Utils;

public class SMSWindow extends Service {

    private Context mContext;
    private WindowManager mWindowManager;
    private View mView;
    private String phoneNumber, smsDefaultText;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
       // if (intent != null) {
            phoneNumber = (String) intent.getExtras().get("Number");
            smsDefaultText = (String) intent.getExtras().get("SmsDefaultContent");
            Log.d("SMSNUMBER", phoneNumber + "Number");
       // }
        allAboutLayout(intent);
        moveView();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        if (mView != null) {
            mWindowManager.removeView(mView);
        }
        super.onDestroy();
    }

    WindowManager.LayoutParams mWindowsParams;

    private void moveView() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.7f);
        int height = (int) (metrics.heightPixels * 0.45f);

        mWindowsParams = new WindowManager.LayoutParams(
                width,//WindowManager.LayoutParams.WRAP_CONTENT,
                height + 100,//WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.TYPE_TOAST,
                //WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, // Not displaying keyboard on bg activity's EditText
                //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //Not work with EditText on keyboard
                PixelFormat.TRANSLUCENT);


        mWindowsParams.gravity = Gravity.TOP | Gravity.CENTER;
        //params.x = 0;
        mWindowsParams.y = 100;
        mWindowManager.addView(mView, mWindowsParams);

        mView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            long startTime = System.currentTimeMillis();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (System.currentTimeMillis() - startTime <= 300) {
                    return false;
                }
                if (isViewInBounds(mView, (int) (event.getRawX()), (int) (event.getRawY()))) {
                    editTextReceiveFocus();
                } else {
                    editTextDontReceiveFocus();
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = mWindowsParams.x;
                        initialY = mWindowsParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mWindowsParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mWindowsParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mView, mWindowsParams);
                        break;
                }
                return false;
            }
        });
    }

    private boolean isViewInBounds(View view, int x, int y) {
        Rect outRect = new Rect();
        int[] location = new int[2];
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private void editTextReceiveFocus() {
        if (!wasInFocus) {
            mWindowsParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            mWindowManager.updateViewLayout(mView, mWindowsParams);
            wasInFocus = true;
        }
    }

    private void editTextDontReceiveFocus() {
        if (wasInFocus) {
            mWindowsParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            mWindowManager.updateViewLayout(mView, mWindowsParams);
            wasInFocus = false;
        }
    }

    private boolean wasInFocus = true;

    private void allAboutLayout(Intent intent) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.ovelay_window, null);

        final EditText edtText = (EditText) mView.findViewById(R.id.et_smstext);
        final TextView tvTitle = (TextView) mView.findViewById(R.id.tv_title);
        final TextView tvcharCount = (TextView) mView.findViewById(R.id.tv_charCount);
        final TextView tvNumberTo = (TextView) mView.findViewById(R.id.tv_toNumber);
        final ImageView imgClose = (ImageView) mView.findViewById(R.id.img_cancel);
        Button btnSend = (Button) mView.findViewById(R.id.btn_send);
        Typeface custom_font = Typeface.createFromAsset(getResources().getAssets(), "Regular.otf");
        tvTitle.setTypeface(custom_font);
        if (phoneNumber != null) {
            tvNumberTo.setText("To: " + phoneNumber);
        }
        else {
            tvNumberTo.setText("To: " + "Unknown");
        }
        if (smsDefaultText != null) {
            edtText.setText(smsDefaultText + " ");
            edtText.setSelection(smsDefaultText.length() + 1);

        }
        edtText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

        edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //tvValue.setText(edtText.getText());
                smsDefaultText = edtText.getText().toString();
                tvcharCount.setText(smsDefaultText.length() + "/140");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtText.getText().toString().length() > 2) {
                    Utils.sendSms(getApplicationContext(), phoneNumber, edtText.getText().toString());
                    stopSelf();
                } else {
                    edtText.setError("Enter Message");
                    // Toast.makeText(getApplicationContext(), "Enter Message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
