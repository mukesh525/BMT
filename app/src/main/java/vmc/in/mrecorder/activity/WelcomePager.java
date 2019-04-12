package vmc.in.mrecorder.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.WelcomePagerAdapter;
import vmc.in.mrecorder.util.PagerCirclesManager;

public class WelcomePager extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_pager);
        viewPager = (ViewPager) findViewById(R.id.how_to_use_pager);
        WelcomePagerAdapter pagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PagerCirclesManager.dotStatusManage(position, getActivity());
                viewDoneButton(position);
                viewSkipButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(pagerAdapter);
    }

    void viewDoneButton(int position) {
        ImageView nextButton = (ImageView) findViewById(R.id.next_page_button);
        TextView doneButton = (TextView) findViewById(R.id.done_button);
        if ((nextButton != null) && (doneButton != null)) {
            if (position < 2) {
                nextButton.setVisibility(View.VISIBLE);
                doneButton.setVisibility(View.GONE);

            } else {
                nextButton.setVisibility(View.GONE);
                doneButton.setVisibility(View.VISIBLE);
            }
        }
    }

    void viewSkipButton(int position) {
        TextView skipButton = (TextView) findViewById(R.id.skip_button);
        if (skipButton != null) {
            if (position > 0) {
                skipButton.setVisibility(View.GONE);

            } else {
                skipButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void nextPage(View v) {
        int position = viewPager.getCurrentItem();
        viewPager.setCurrentItem(position + 1);
    }

    public void skipPages(View v) {
        // viewPager.setCurrentItem(1);
        startActivity(new Intent(WelcomePager.this, Login.class));
        finish();
    }

    public void donePages(View v) {

        Intent intentTo;
//        if (Utils.isLogin(UseActivity.this)) {
//
//            intentTo = new Intent(UseActivity.this, Home.class);
//            intentTo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//        } else {
        intentTo = new Intent(WelcomePager.this, Login.class);
        intentTo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        //  }
        startActivity(intentTo);
        finish();
    }

    public Activity getActivity() {
        return this;
    }


}
