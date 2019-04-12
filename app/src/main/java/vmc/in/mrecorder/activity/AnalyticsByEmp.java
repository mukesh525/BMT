package vmc.in.mrecorder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

public class AnalyticsByEmp extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, vmc.in.mrecorder.callbacks.TAG {

    private Toolbar mToolbar;
    private PieChart pieChart;
    private BarChart chart;
    private LinearLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinner_nav;
    private TextView name;
    private RelativeLayout coordinatorLayout;
    private RelativeLayout offline;
    private ArrayList<BarModel> barModels;
    private JSONObject response;
    private JSONArray records;
    private String count;
    private String reportype = "0";
    private ArrayList<BarModel> barModel;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private Boolean rotate = false;
    private Boolean FirstLoad = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(AnalyticsByEmp.this) < 6.0)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_analytics_by_emp);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.setTitle("Analytics By Employee");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        coordinatorLayout = (RelativeLayout) findViewById(R.id.coordi_layout);
        offline = (RelativeLayout) findViewById(R.id.rl_dummy);
        name = (TextView) findViewById(R.id.tv_analy_name);
        name.setText("Analytics By Emp");
        if (savedInstanceState != null) {
            barModel = savedInstanceState.getParcelableArrayList("DATA");
            rotate = true;
            FirstLoad=false;
            setBarChart(barModel);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        if (barModel != null && barModel.size() > 0)
            outState.putParcelableArrayList("DATA", barModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CallApplication.getInstance().setConnectivityListener(this);
        if (barModel != null) {
            setBarChart(barModel);
        } else {
            getData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (chart != null) {
            mainLayout.removeView(chart);
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
//                String item = adapter.getItemAtPosition(position).toString();
//
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


    private void getData() {

        if (ConnectivityReceiver.isConnected()) {
            new GetBarChartData().execute();
            if (offline.getVisibility() == View.VISIBLE) {
                offline.setVisibility(View.GONE);
            }
            // setBarChart(barModels);

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
                    .setActionTextColor(ContextCompat.getColor(AnalyticsByEmp.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

    }


    private void setBarChart(ArrayList<BarModel> barModel) {
        if (chart != null) {
            mainLayout.removeView(chart);
        }
        chart = new BarChart(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        chart.setLayoutParams(layoutParams);
        mainLayout.addView(chart);
        BarData data = new BarData(getXAxisValues(barModel), getDataSet(barModel));
        chart.setData(data);
        chart.setDescription("Analytics By Employee");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private ArrayList<BarDataSet> getDataSet(ArrayList<BarModel> barModel) {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        ArrayList<BarEntry> valueSet3 = new ArrayList<>();

        for (int i = 0; i < barModel.size(); i++) {
            BarEntry vs1 = new BarEntry(Float.parseFloat(barModel.get(i).getInbound()), i);
            valueSet1.add(vs1);
            BarEntry vs2 = new BarEntry(Float.parseFloat(barModel.get(i).getOutbound()), i);
            valueSet2.add(vs2);
            BarEntry vs3 = new BarEntry(Float.parseFloat(barModel.get(i).getMissed()), i);
            valueSet3.add(vs3);
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Inbound");
        barDataSet1.setColor(Color.rgb(76, 153, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Outbound");
        barDataSet2.setColor(Color.rgb(0, 102, 204));
        BarDataSet barDataSet3 = new BarDataSet(valueSet3, "Missed");
        barDataSet3.setColor(Color.rgb(204, 0, 0));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        dataSets.add(barDataSet3);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues(ArrayList<BarModel> barModel) {
        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < barModel.size(); i++) {

            xAxis.add(barModel.get(i).getEmpname());
        }
        return xAxis;
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

    class GetBarChartData extends AsyncTask<Void, Void, ArrayList<BarModel>> {
        private String message, code;


        @Override
        protected ArrayList<BarModel> doInBackground(Void... params) {

            try {
                Log.d("TAG", reportype);
                Log.d("TAG", Utils.getFromPrefs(AnalyticsByEmp.this,SESSION_ID,UNKNOWN));
                barModel = new ArrayList<>();
                barModel = Parser.ParseEMPResponse(Requestor.requestByEMP(requestQueue, EMPREPORT_URL, reportype, Utils.getFromPrefs(AnalyticsByEmp.this,SESSION_ID,UNKNOWN), Utils.getFromPrefs(AnalyticsByEmp.this, AUTHKEY, "n")));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return barModel;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<BarModel> models) {

            if (models != null && Parser.code != null) {

                if (Parser.code.equals("400")) {
                    if (models.size() > 0) {
                        setBarChart(models);
                    } else {

                        setBarChart(models);
                        Toast.makeText(getApplicationContext(), "No Report Found",
                                Toast.LENGTH_LONG).show();

                    }


                } else if (Parser.code.equals("202") || Parser.code.equals("401")) {
                    Toast.makeText(getApplicationContext(), "You have been logout login to continue",
                            Toast.LENGTH_LONG).show();
                    // Utils.isLogoutBackground(AnalyticsByEmp.this);
                }


            }

        }
    }


}
