package vmc.in.mrecorder.fragment;


import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.entity.CallData;


/**
 * Created by mukesh on 2/12/15.
 */
public class ReferDialogFragment extends DialogFragment {
    private FragmentManager fragmentManager;
    private CallData callData;
    private ViewPager mViewPager;
    private MyPagerAdapter myPagerAdapter;
    private TabLayout mTabLayout;
    private int width, height;
    private String titles[] = {"RATE", "REVIEWS"};

    public ReferDialogFragment() {

    }

    public void setCallid(CallData callData) {
        this.callData = callData;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(
                R.layout.referal, container);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpagerr);
        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setOffscreenPageLimit(0);

        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(Constants.isRate){
        mViewPager.setCurrentItem(1);
            Constants.isRate=false;
        }
        width = size.x;
        height = size.y;
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(width - ((width / 100) * 15), height - ((height / 100) * 40));
        window.setGravity(Gravity.CENTER);
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
                    myFragment = RateFragment.newInstance(callData.getCallid());
                    break;
                case 1:
                    myFragment = ReviewFragment.newInstance(callData.getCallid());
                    break;
            }

            return myFragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];

        }

    }
}
