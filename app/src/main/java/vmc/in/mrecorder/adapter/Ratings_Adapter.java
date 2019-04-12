package vmc.in.mrecorder.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.entity.RateData;


/**
 * Created by mukesh on 3/24/2016.
 */
public class Ratings_Adapter extends RecyclerView.Adapter<Ratings_Adapter.CallViewHolder>  {

    private Context context;
    private ArrayList<RateData> RateDataArrayList;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
    public View mroot;
    public Fragment fragment;
    PrettyTime p = new PrettyTime();



    public Ratings_Adapter(Context context, ArrayList<RateData> RateDataArrayList) {
        this.context = context;
        this.RateDataArrayList = RateDataArrayList;

    }




    @Override
    public CallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_item, parent, false);
        return new CallViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CallViewHolder holder, int position) {

        final RateData ci = RateDataArrayList.get(position);

        holder.name.setText(ci.getName());
        holder.rate.setRating(Float.parseFloat(ci.getRate()));
        holder.date.setText(p.format(ci.getDate()));
        holder.title.setText(ci.getTitle());
        holder.description.setText(ci.getDesc());


    }


    @Override
    public int getItemCount() {
        return RateDataArrayList.size();
    }




    public class CallViewHolder extends RecyclerView.ViewHolder  {

        protected TextView name, date, title, description;
        protected RatingBar rate;


        public CallViewHolder(View v) {
            super(v);

            name = (TextView) v.findViewById(R.id.name);
            date = (TextView) v.findViewById(R.id.date);
            rate = (RatingBar) v.findViewById(R.id.rate);
            title = (TextView) v.findViewById(R.id.title);
            description = (TextView) v.findViewById(R.id.desc);


        }


    }



}



