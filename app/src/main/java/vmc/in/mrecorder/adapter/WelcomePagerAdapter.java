package vmc.in.mrecorder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vmc.in.mrecorder.fragment.WelcomePagerFragment;

/**
 * Created by vmc on 17/2/17.
 */

public class WelcomePagerAdapter extends FragmentPagerAdapter {

    public WelcomePagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return WelcomePagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
