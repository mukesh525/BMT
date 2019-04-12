package vmc.in.mrecorder.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.fragment.AllCallsFragment;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.Utils;


public class ContactsActivity extends AppCompatActivity implements vmc.in.mrecorder.callbacks.TAG {

    private final String ALL_CALLS_FRAGMENT_KEY = "all_calls_fragment";
    private final String DIALLED_CALLS_FRAGMENT_KEY = "dialed_calls_fragment";
    private final String RECEIVED_CALLS_FRAGMENT_KEY = "received_calls_fragment";
    private final String MISSED_CALLS_FRAGMENT_KEY = "missed_calls_fragment";
    private Fragment mAllCallsFragment;
    private Fragment mDialledCallsFragment;
    private Fragment mReceivedCallsFragment;
    private Fragment missedCallsFragment;
    private String titles[] = {"ALL CALLS", "RECEIVED", "DIALLED", "MISSED"};
    private String TYPE;
    private MyPagerAdapter myPagerAdapter;
    private String TAG1 = "Sync_Offline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(ContactsActivity.this) < 6.0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_contacts_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < titles.length; i++) {
            tabLayout.getTabAt(i).setText(titles[i]);

        }

        if (mViewPager != null) {
//            initializeFragments(savedInstanceState);
//            setupViewPager(mViewPager);
//            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//            tabLayout.setupWithViewPager(mViewPager);
        }

        toolbar.setTitle("MCube Tracker");
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.fabBtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Utils.setRecording(ContactsActivity.this);
                startActivity(new Intent(ContactsActivity.this, Settings.class));
            }
        });
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

    }

    public void playAudioPath(String path) {

        // Toast.makeText(HomeActivity.this, url, Toast.LENGTH_LONG).show();//Uri myUri = Uri.parse("http://mcube.vmctechnologies.com/sounds/99000220411460096169.wav");
        Uri uri = null;
        File file = new File(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = Uri.parse("content://" + file);
        } else {
            uri = Uri.parse("file://" + file);
        }
        Log.wtf("FIle path now", uri + "");
        //intent.setDataAndType(Uri.fromFile(file), "audio/*");
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        performSync();
    }



    private void performSync() {
        ArrayList<Model> callList = CallApplication.getWritabledatabase().getAllOfflineCalls();
        if (callList != null & callList.size() > 0) {
            for (Model model : callList) {
                boolean isValid = !CallRecorderServiceAll.recording || !model.getPhoneNumber().equals(CallRecorderServiceAll.onCallRecordNumber);
                if (isValid)
                    new ContactsActivity.LongOperation().execute(model);

            }
        }
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = null;
            switch (position) {
                case 0:
                    myFragment = AllCallsFragment.newInstance(TYPE_ALL);
                    // myFragment = new AllCalls();
                    break;
                case 1:
                    //myFragment = new InboundCalls();
                    myFragment = AllCallsFragment.newInstance(INCOMING);
                    break;
                case 2:
                    //  myFragment = new OutboundCalls();
                    myFragment = AllCallsFragment.newInstance(OUTGOING);
                    break;
                case 3:
                    // myFragment = new MissedCalls();
                    myFragment = AllCallsFragment.newInstance(MISSED);
                    break;
            }
            return myFragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }

    private class LongOperation extends AsyncTask<Model, Void, Model> {

        @Override
        protected Model doInBackground(Model... params) {
            try {
                Utils.uploadMultipartData(params[0], params[0].getFile().exists() && params[0].getFile().canRead(), ContactsActivity.this);
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
