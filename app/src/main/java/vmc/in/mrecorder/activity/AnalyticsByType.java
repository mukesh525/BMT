package vmc.in.mrecorder.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.CustomSpinnerAdapter;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.BarModel;
import vmc.in.mrecorder.entity.PieModel;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Parser;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.SingleTon;
import vmc.in.mrecorder.util.Utils;

public class AnalyticsByType extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, vmc.in.mrecorder.callbacks.TAG {

    private Toolbar mToolbar;
    private PieChart pieChart;
    private BarChart chart;
    private LinearLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout offline;
    private TextView tv_noResponse;
    private RelativeLayout coordinatorLayout;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    private Spinner spinner_nav;
    private String reportype = "0";
    private JSONObject response;
    private JSONArray records;
    private String count;
    private ArrayList<PieModel> pieModel;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private  Boolean rotate = false;
    private boolean FirstLoad=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        if(Utils.tabletSize(AnalyticsByType.this)< 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_analytics_by_emp);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        offline = (RelativeLayout) findViewById(R.id.rl_dummy);
        tv_noResponse = (TextView) findViewById(R.id.tv_noresponse);
        coordinatorLayout = (RelativeLayout) findViewById(R.id.coordi_layout);
        if (savedInstanceState != null) {
            pieModel = savedInstanceState.getParcelableArrayList("DATA");
            rotate=true;
            FirstLoad=false;
            setPieChart(pieModel);
         } else {
            FirstLoad = true;
        }

        addItemsToSpinner();
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CallApplication.getInstance().setConnectivityListener(this);
        if(pieModel!=null){
            setPieChart(pieModel);
        }else{
            getData();
        }

        Log.d("RESUME","RESUME CALLED");

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        if(pieModel!=null && pieModel.size()> 0)
        outState.putParcelableArrayList("DATA", pieModel);


    }
    @Override
    protected void onPause() {
        super.onPause();
        if (pieChart != null) {
            mainLayout.removeView(pieChart);
        }
    }

    private void getData() {
        if (ConnectivityReceiver.isConnected()) {
            new GetPieChartData().execute();
            if (offline.getVisibility() == View.VISIBLE) {
                offline.setVisibility(View.GONE);
            }

        } else {
            if (offline.getVisibility() == View.GONE) {
                offline.setVisibility(View.VISIBLE);
            }
            Snackbar snack = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            getData();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(AnalyticsByType.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

    }


    public void addItemsToSpinner() {

        ArrayList<String> list = new ArrayList<String>();
        list.add("Daily");
        list.add("Weekly");
        list.add("Monthly");


        // Custom ArrayAdapter with spinner item layout to set popup background

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);


        // Default ArrayAdapter with default spinner item layout, getting some
        // view rendering problem in lollypop device, need to test in other
        // devices

		/*
         * ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_spinner_item, list);
		 * spinAdapter.setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 */

        spinner_nav.setAdapter(spinAdapter);

        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                reportype = position + "";
                if (!rotate && !FirstLoad) {
                    getData();
                } else {
                    rotate = false;
                    FirstLoad = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

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




    private void setPieChart(ArrayList<PieModel> pieModel) {
        if (pieChart != null) {
            mainLayout.removeView(pieChart);
        }

        pieChart = new PieChart(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pieChart.setLayoutParams(layoutParams);
        // add pie chart to main layout
        mainLayout.addView(pieChart);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(15);
        pieChart.setTransparentCircleRadius(20);
        pieChart.setUsePercentValues(true);
        // customize legends
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(9);
        l.setYEntrySpace(7);

        final ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < pieModel.size(); i++) {
            labels.add(pieModel.get(i).getCalltype());
            entries.add(new Entry(Float.valueOf(pieModel.get(i).getCount()), i));
        }

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        dataset.setSliceSpace(3);
        dataset.setSelectionShift(7);

        PieData data = new PieData(labels, dataset);
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setDescription("Analytics By Calls");

        pieChart.setData(data);

        pieChart.animateY(5000);

     //   pieChart.saveToGallery("/sd/mychart.jpg", 85); // 85 is the quality of the image

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Toast.makeText(getApplicationContext(), " " + (int) Math.round(e.getVal()) + " " + labels.get(e.getXIndex()) + " calls", Toast.LENGTH_SHORT).show();

                if (e == null)
                    return;
                Log.d("VAL SELECTED",
                        "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                                + ", DataSet index: " + dataSetIndex);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }


    class GetPieChartData extends AsyncTask<Void, Void, ArrayList<PieModel>> {
        private String message, code;


        @Override
        protected ArrayList<PieModel> doInBackground(Void... params) {


            try {
                pieModel = new ArrayList<PieModel>();
                pieModel = Parser.ParseTypeResponse(Requestor.requestByType(requestQueue,TYPEREPORT_URL, reportype,  Utils.getFromPrefs(AnalyticsByType.this,SESSION_ID,UNKNOWN), Utils.getFromPrefs(AnalyticsByType.this, AUTHKEY, "n")));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return pieModel;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<PieModel> models) {
            if (models != null && Parser.code!= null) {
                if (Parser.code.equals("400")) {
                    if (models.size() > 0) {
                        setPieChart(models);
                    } else {
                        setPieChart(models);
                        Toast.makeText(getApplicationContext(), "No Report Found",
                                Toast.LENGTH_LONG).show();

                    }


                } else if (Parser.code.equals("202") ||Parser.code.equals("401")) {
                    Toast.makeText(getApplicationContext(), "You have been logout login to continue",
                            Toast.LENGTH_LONG).show();
                } else if (Parser.code.equals("404")) {
                    if (pieChart != null) {
                        mainLayout.removeView(pieChart);
                    }
                    Toast.makeText(getApplicationContext(), "No Records Found",
                            Toast.LENGTH_LONG).show();
                }


            }
        }
    }
}