package vmc.in.mrecorder.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vmc.in.mrecorder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomePagerFragment extends Fragment {


    private int images[] = {R.drawable.ic_microphone, R.drawable.analytics, R.drawable.star};
    private String tags[] = {"MTracker is used to record and manage calls."
            , "It helps to analyze call statistics.",
            "One can rate and review the call records."};

    private String titles[] = {"Call Recording","Analytics","Rating and Reviews"};
    private ImageView imgTag;
    private TextView tvTag,tvTitle;
    private RelativeLayout rootLayout;

    public static WelcomePagerFragment newInstance(int position) {
        Bundle args = new Bundle();
        WelcomePagerFragment fragment = new WelcomePagerFragment();
        args.putInt("pager_fragment", position);
        fragment.setArguments(args);
        return fragment;
    }


    public WelcomePagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome_pager, container, false);
        imgTag = (ImageView) v.findViewById(R.id.img_tag);
        tvTag = (TextView) v.findViewById(R.id.tv_tag);
        tvTitle = (TextView) v.findViewById(R.id.tv_title);
        rootLayout = (RelativeLayout) v.findViewById(R.id.rl_main);
        int layoutResource = getProperLayout(getArguments().getInt("pager_fragment", 0));
        getProperLayout(layoutResource);
        return v;
    }

    int getProperLayout(int position) {
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "Allura.otf");
        tvTag.setTypeface(custom_font);
        switch (position) {
            case 0:
                imgTag.setImageResource(images[0]);
                tvTag.setText(tags[0]);
                tvTitle.setText(titles[0]);
                rootLayout.setBackgroundResource(R.color.pager1);
                break;
            // return R.layout.fragment_pager_one;
            case 1:
                imgTag.setImageResource(images[1]);
                tvTag.setText(tags[1]);
                tvTitle.setText(titles[1]);
                rootLayout.setBackgroundResource(R.color.pager2);
                break;
            //return R.layout.fragment_pager_two;
            case 2:
                imgTag.setImageResource(images[2]);
                tvTag.setText(tags[2]);
                tvTitle.setText(titles[2]);
                rootLayout.setBackgroundResource(R.color.pager3);
                break;
            // return R.layout.fragment_pager_three;

        }
        return position;
    }


}
