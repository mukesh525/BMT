package vmc.in.mrecorder.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.Ratings_Adapter;
import vmc.in.mrecorder.entity.RateData;
import vmc.in.mrecorder.parser.Parser;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.SingleTon;
import vmc.in.mrecorder.util.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, vmc.in.mrecorder.callbacks.TAG {

    private Ratings_Adapter adapter;
    public RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mprogressLayout, retrylayout;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<RateData> rateDataList;
    public ArrayList<RateData> rateData;
    private String callid;
    private SingleTon volleySingleton;
    private RequestQueue requestQueue;
    private TextView click;
    private ProgressBar progressBar;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(String callData) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(CALLID, callData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            callid = getArguments().getString(CALLID);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_review, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        //  mroot = (RelativeLayout) view.findViewById(R.id.fragment_followup);

        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        progressBar= (ProgressBar) view.findViewById(R.id.progressBar);
        click = (TextView) view.findViewById(R.id.click);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        volleySingleton = SingleTon.getInstance();
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.RED, PorterDuff.Mode.MULTIPLY);
        requestQueue = volleySingleton.getRequestQueue();
        retrylayout.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
        DownloadCalls();
       }
       });

        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

      DownloadCalls();

        return view;
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        }
        DownloadCalls();
    }


    protected void DownloadCalls() {
        if (ConnectivityReceiver.isConnected()) {
            new DownloadCallData().execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (retrylayout.getVisibility() == View.GONE) {
                retrylayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }
    class DownloadCallData extends AsyncTask<Void, Void, ArrayList<RateData>> {
        private String code = "n/a", msg = "n/a";

        @Override
        protected void onPreExecute() {
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }

            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }

            super.onPreExecute();
        }


        @Override
        protected ArrayList<RateData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub
            JSONObject response = null;
            try {

                response = Requestor.requestGetRating(requestQueue, GET_RATE_URL, Utils.getFromPrefs(getActivity(), AUTHKEY, "N/A"), callid);
                Log.d(TAG, response.toString());
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage().toString());
            }
            if (response != null) {

                try {

                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }

                    rateDataList = Parser.ParseReview(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return rateDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<RateData> data) {

            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            if (mprogressLayout.getVisibility() == View.VISIBLE) {
                mprogressLayout.setVisibility(View.GONE);
            }

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

            }
            if (data != null && getActivity() != null && data.size() > 0) {
                  adapter = new Ratings_Adapter(getActivity(), data);
                  recyclerView.setAdapter(adapter);

            } else if (code.equals("202") || code.equals("401")) {
                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }

            } else {

                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }

            }
        }

    }
}
