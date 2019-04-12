package vmc.in.mrecorder.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.ContactsActivity;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.LocationActivity;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by gousebabjan on 12/4/16.
 */
public class NewSpeedDialAdpter extends RecyclerView.Adapter<NewSpeedDialAdpter.CallViewHolder> implements TAG {
    private Context context;
    private ArrayList<Model> CallDataArrayList;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");

    public NewSpeedDialAdpter(Context context, ArrayList<Model> CallDataArrayList) {
        this.context = context;
        this.CallDataArrayList = CallDataArrayList;
    }

    @Override
    public CallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.speeddial_item_layout, parent, false);
        return new CallViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(CallViewHolder holder, int position) {

        final Model ci = CallDataArrayList.get(position);
        if (ci.getCallType().equals(INCOMING))
            holder.calltype.setBackgroundResource(R.drawable.ic_call_incoming);
        else if (ci.getCallType().equals(OUTGOING))
            holder.calltype.setBackgroundResource(R.drawable.ic_call_outgoing);
        else if (ci.getCallType().equals(MISSED)) {
            //holder.iboverflow.setVisibility(View.GONE);
            holder.calltype.setBackgroundResource(R.drawable.ic_call_missed);
        }
        String sname = null;

        try {
            sname = getContactName(ci.getPhoneNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int duration;
        try {
            MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(ci.getFile()));
            duration = mp.getDuration();
        } catch (Exception e) {
            duration = 0;
        }
        //Added for status menu gone
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notifyMode = sharedPrefs.getBoolean("prefRecording", false);
        Log.d(TAG, "Calls No Recording" + notifyMode);


        if (new File(ci.getFilePath()).exists() && duration > 0) {
            if (notifyMode) {//added
                if (holder.iboverflow.getVisibility() == View.GONE) {
                    holder.iboverflow.setVisibility(View.VISIBLE);
                }
            } else if (holder.iboverflow.getVisibility() == View.VISIBLE) {
                holder.iboverflow.setVisibility(View.GONE);
            }
            holder.iboverflow.setOnClickListener(new OnOverflowSelectedListener(context, holder.getAdapterPosition(), CallDataArrayList));
        } else {
            if (holder.iboverflow.getVisibility() == View.VISIBLE) {
                holder.iboverflow.setVisibility(View.GONE);
            }
        }

        holder.nameTextView.setText(sname != null ? sname : ci.getPhoneNumber());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        //  String stime=sdf.format(new Date(Long.parseLong(c.getString(timeindex))));
        String stime = sdf.format(new Date(Long.parseLong(ci.getTime())));

        holder.timeTextView.setText(stime);

        sdf = new SimpleDateFormat("dd-MM-yyyy");
        String sdate = sdf.format(new Date(Long.parseLong(ci.getTime())));

        holder.dateTextView.setText(sdate);


    }


    public String getContactName(String snumber) throws Exception {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(snumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        // cursor.close();
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    @Override
    public int getItemCount() {
        return CallDataArrayList.size();
    }


    public static class OnOverflowSelectedListener implements View.OnClickListener {
        private Context mContext;
        private int position;
        private ArrayList<Model> callDatas;


        public OnOverflowSelectedListener(Context context, int pos, ArrayList<Model> callDatas) {
            mContext = context;
            this.position = pos;
            this.callDatas = callDatas;

        }

        @Override
        public void onClick(final View v) {
//            PopupMenu popupMenu = new PopupMenu(mContext, v) {
//                @Override
//                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.play:
//                            ((ContactsActivity) mContext).playAudioPath(callDatas.get(position).getFilePath());
//                            return true;
//                        case R.id.delete:
////                            CallApplication.getWritabledatabase().delete(callDatas.get(position).getId());
////                            File file = new File(callDatas.get(position).getFilePath());
////                            if (file.exists()) {
////                                file.delete();
////                                Toast.makeText(mContext, "File deleted", Toast.LENGTH_SHORT).show();
////                            } else {
////                                Toast.makeText(mContext, "File does not exist", Toast.LENGTH_SHORT).show();
////                            }
//                            return true;
//                        case R.id.share:
//                            String mypath = callDatas.get(position).getFilePath();
//                            Intent share = new Intent(Intent.ACTION_SEND);
//                            share.setType("audio/*");
//                            share.putExtra(Intent.EXTRA_TEXT, "I Recorded this audio using MTracker Call Recorder .\n https://play.google.com/store/apps/details?id=vmc.in.mrecorder");
//                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + mypath));
//                            mContext.startActivity(Intent.createChooser(share, "Share Sound File"));
//                            return true;
//                        default:
//                            return super.onMenuItemSelected(menu, item);
//                    }
//                }
//            };
//


            //creating a popup menu
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            //adding click listener
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.play:
                            ((ContactsActivity) mContext).playAudioPath(callDatas.get(position).getFilePath());
                            return true;
                        case R.id.delete:
//                            CallApplication.getWritabledatabase().delete(callDatas.get(position).getId());
//                            File file = new File(callDatas.get(position).getFilePath());
//                            if (file.exists()) {
//                                file.delete();
//                                Toast.makeText(mContext, "File deleted", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(mContext, "File does not exist", Toast.LENGTH_SHORT).show();
//                            }
                            return true;
                        case R.id.share:
                            String mypath = callDatas.get(position).getFilePath();
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("audio/*");
                            share.putExtra(Intent.EXTRA_TEXT, "I Recorded this audio using MTracker Call Recorder .\n https://play.google.com/store/apps/details?id=vmc.in.mrecorder");
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + mypath));
                            }else {
                                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + mypath));
                            }
                            //share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + mypath));
                            mContext.startActivity(Intent.createChooser(share, "Share Sound File"));
                            return true;

                    }
                    return false;
                }
            });

            // Force icons to show
            Object menuHelper = null;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                Log.w("t", "error forcing menu icons to show", e);
                popupMenu.show();
                // Try to force some horizontal offset
                try {
                    Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                    fListPopup.setAccessible(true);
                    Object listPopup = fListPopup.get(menuHelper);
                    argTypes = new Class[]{int.class};
                    Class listPopupClass = listPopup.getClass();
                } catch (Exception e1) {

                    Log.w("T", "Unable to force offset", e);
                }
                return;
            }

            popupMenu.inflate(R.menu.newpopupmenu);
            popupMenu.show();


        }
    }


    public static class CallViewHolder extends RecyclerView.ViewHolder {


        protected TextView nameTextView, timeTextView, dateTextView;

        protected ImageButton iboverflow;
        public ImageView calltype;

        public CallViewHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.name);
            timeTextView = (TextView) v.findViewById(R.id.time);
            dateTextView = (TextView) v.findViewById(R.id.date);
            calltype = (ImageView) v.findViewById(R.id.calltype);
            iboverflow = (ImageButton) v.findViewById(R.id.menu);


        }


    }

}
