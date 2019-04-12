package vmc.in.mrecorder.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.LocationActivity;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.fragment.DownloadFile;
import vmc.in.mrecorder.util.BuilderManager;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by mukesh on 3/24/2016.
 */
public class Calls_Adapter extends RecyclerView.Adapter<Calls_Adapter.CallViewHolder> implements TAG {

    private Context context;
    private ArrayList<CallData> CallDataArrayList;
    private CallClickedListner callClickedListner;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
    private int previousPosition = 0;
    public View mroot;
    public Fragment fragment;

    private List<String> dataList;
    private List<String> itemsPendingRemoval;

    private static final int PENDING_REMOVAL_TIMEOUT = 5000; // 5sec
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    public Calls_Adapter(Context context, ArrayList<CallData> CallDataArrayList, View mroot, Fragment fragment) {
        this.context = context;
        this.CallDataArrayList = CallDataArrayList;
        this.mroot = mroot;
        this.fragment = fragment;
        itemsPendingRemoval = new ArrayList<>();

    }


    public void setClickedListner(CallClickedListner callClickedListner1) {
        this.callClickedListner = callClickedListner1;
    }


    @Override
    public CallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call, parent, false);
        return new CallViewHolder(itemView, CallDataArrayList, callClickedListner);
    }

    @Override
    public void onBindViewHolder(final CallViewHolder holder, final int position) {
        try {
            final CallData ci = CallDataArrayList.get(position);
            String callFrom = "";
            if (ci.getCallto().length() > 10) {
                callFrom = ci.getCallto().substring(ci.getCallto().length() - 10, ci.getCallto().length());
            } else callFrom = ci.getCallto();
            //BoomMneuButton
            holder.bmb3.clearBuilders();
            if (!Utils.isEmpty(CallDataArrayList.get(position).getFilename()) && CallDataArrayList.get(position).getLocation().length() > 7) {
                //   popupMenu.inflate(R.menu.popupmenu_cmsl);
                holder.bmb3.clearBuilders();
                holder.bmb3.setButtonEnum(ButtonEnum.TextInsideCircle);
                holder.bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_3);
                holder.bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_5_3);
                for (int i = 0; i < holder.bmb3.getButtonPlaceEnum().buttonNumber(); i++)
                    holder.bmb3.addBuilder(BuilderManager.getTextInsideCircleButtonBuilder(i));

            } else if (!Utils.isEmpty(CallDataArrayList.get(position).getFilename()) && CallDataArrayList.get(position).getLocation().length() <= 7) {
                //popupMenu.inflate(R.menu.popupmenu_cms);
                holder.bmb3.clearBuilders();
                holder.bmb3.setButtonEnum(ButtonEnum.TextInsideCircle);
                holder.bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_2);
                holder.bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_2);
                for (int i = 0; i < holder.bmb3.getButtonPlaceEnum().buttonNumber(); i++)
                    holder.bmb3.addBuilder(BuilderManager.getTextInsideCircleButtonBuilder(i));


            } else if (Utils.isEmpty(CallDataArrayList.get(position).getFilename()) && CallDataArrayList.get(position).getLocation().length() > 7) {
                //popupMenu.inflate(R.menu.popupmenu_cml);
                holder.bmb3.clearBuilders();
                holder.bmb3.setButtonEnum(ButtonEnum.TextInsideCircle);
                holder.bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_2);
                holder.bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_2);
                for (int i = 0; i < holder.bmb3.getButtonPlaceEnum().buttonNumber(); i++)
                    holder.bmb3.addBuilder(BuilderManager.getTextInsideCircleButtonBuilder(i));
            } else {
                holder.bmb3.clearBuilders();
                holder.bmb3.setButtonEnum(ButtonEnum.TextInsideCircle);
                holder.bmb3.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_2);
                holder.bmb3.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_2);
                for (int i = 0; i < holder.bmb3.getButtonPlaceEnum().buttonNumber(); i++)
                    holder.bmb3.addBuilder(BuilderManager.getTextInsideCircleButtonBuilder(i));

                // popupMenu.inflate(R.menu.popupmenu);
            }

            final String finalCallFrom = callFrom;
            holder.bmb3.setOnBoomListener(new OnBoomListener() {
                @Override
                public void onClicked(int index, BoomButton boomButton) {
                    // Toast.makeText(context,"Clicked"+index,Toast.LENGTH_SHORT).show();
                    switch (index) {
                        case 0:
                            if (!Utils.isEmpty(finalCallFrom)) {
                                Utils.makeAcall(finalCallFrom, (Home) context);
                            } else {
                                Toast.makeText(context, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            if (!Utils.isEmpty(finalCallFrom)) {
                                Utils.sendSms(finalCallFrom, (Home) context);
                            } else {
                                Toast.makeText(context, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 2:
                            if (ConnectivityReceiver.isConnected()) {
                                Gson gson = new Gson();
                                String TrackInfo = gson.toJson(CallDataArrayList.get(position));
                                Intent intent = new Intent(context, LocationActivity.class);
                                intent.putExtra("DATA", TrackInfo);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 3:
                            ((Home) context).onShareFile(CallDataArrayList.get(position).getFilename());
                            break;
                        case 4:
                            ((Home) context).onRatingsClick(CallDataArrayList.get(position));
                            break;
                    }
                }

                @Override
                public void onBackgroundClick() {

                }

                @Override
                public void onBoomWillHide() {

                }

                @Override
                public void onBoomDidHide() {

                }

                @Override
                public void onBoomWillShow() {

                }

                @Override
                public void onBoomDidShow() {

                }
            });


            holder.callerNameTextView.setText(Utils.isEmpty(ci.getName()) ? UNKNOWN : ci.getName());

            //  holder.callFromTextView.setText(Utils.isEmpty(ci.getCallto()) ? UNKNOWN : ci.getCallto());
            holder.callFromTextView.setText(Utils.isEmpty(callFrom) ? UNKNOWN : callFrom);
            try {
                holder.dateTextView.setText(sdfDate.format(ci.getStartTime()));
                holder.timeTextView.setText(sdfTime.format(ci.getStartTime()));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage().toString());

            }
            holder.groupNameTextView.setText(Utils.isEmpty(ci.getEmpname()) ? UNKNOWN : ci.getEmpname());
//            holder.overflow.setOnClickListener(new OnOverflowSelectedListener(context, holder.getAdapterPosition(), CallDataArrayList));
//            holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.overflow.performClick();
//                }
//            });
            holder.img_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConnectivityReceiver.isConnected()) {
                        if (!Utils.isEmpty(ci.getFilename())) {
                            ((Home) context).playAudio(ci);
                            Log.d(TAG, ci.getFilename());
                        }
                    } else {
                        Toast.makeText(context, "Check Internet Connection..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.isRate = true;
                    ((Home) context).onRatingsClick(ci);
                }
            });
            switch (ci.getCalltype()) {
                case "0":
                    holder.call_img.setImageResource(R.drawable.ic_call_missed);
                    // holder.statusTextView.setText("MISSED");
                    break;
                case "1":
                    holder.call_img.setImageResource(R.drawable.ic_call_incoming);
//                if (!Utils.isEmpty(ci.getFilename()))
//                    holder.statusTextView.setText("ANSWER");
//                else holder.statusTextView.setText("CANCEL");
                    break;
                default:
                    holder.call_img.setImageResource(R.drawable.ic_call_outgoing);
//                if (!Utils.isEmpty(ci.getFilename()))
//                    holder.statusTextView.setText("ANSWER");
//                else holder.statusTextView.setText("CANCEL");
                    break;
            }
         /*   if (ci.getCalltype().equals("0")) {
                holder.call_img.setImageResource(R.drawable.ic_call_missed);
                // holder.statusTextView.setText("MISSED");
            } else if (ci.getCalltype().equals("1")) {
                holder.call_img.setImageResource(R.drawable.ic_call_incoming);
//                if (!Utils.isEmpty(ci.getFilename()))
//                    holder.statusTextView.setText("ANSWER");
//                else holder.statusTextView.setText("CANCEL");
            } else {
                holder.call_img.setImageResource(R.drawable.ic_call_outgoing);
//                if (!Utils.isEmpty(ci.getFilename()))
//                    holder.statusTextView.setText("ANSWER");
//                else holder.statusTextView.setText("CANCEL");
            }*/
            //for Statue textview
            if (ci.getCalltype().equals("0")) {
                // holder.statusTextView.setVisibility(View.VISIBLE);
                // holder.statusTextView.setText("MISSED");
                holder.imgCallStatus.setVisibility(View.VISIBLE);
                holder.imgCallStatus.setImageResource(R.drawable.callmissed);
                DrawableCompat.setTint(holder.imgCallerstatue.getDrawable(), ContextCompat.getColor(context, R.color.deeppurple_primary_dark));
            } else if (!ci.getCalltype().equals("0") && ci.getDuration() == 0) {
                // holder.statusTextView.setVisibility(View.VISIBLE);
                // holder.statusTextView.setText("CANCEL");
                holder.imgCallStatus.setVisibility(View.VISIBLE);
                holder.imgCallStatus.setImageResource(R.drawable.callcancel);
                DrawableCompat.setTint(holder.imgCallerstatue.getDrawable(), ContextCompat.getColor(context, R.color.red));
            } else {
                // holder.statusTextView.setVisibility(View.VISIBLE);
                // holder.statusTextView.setText("ANSWER");
                holder.imgCallStatus.setVisibility(View.VISIBLE);
                holder.imgCallStatus.setImageResource(R.drawable.callreceived);
                DrawableCompat.setTint(holder.imgCallerstatue.getDrawable(), ContextCompat.getColor(context, R.color.green));
            }
//            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//            boolean notifyMode = sharedPrefs.getBoolean("prefRecording", false);
            //Log.d(TAG, "Calls No Recording" + notifyMode);


            if (!ci.getReview().equals("0")) {
                holder.review.setVisibility(View.VISIBLE);
                holder.rate.setVisibility(View.VISIBLE);
                holder.review.setText(ci.getReview());

            } else {
                holder.review.setVisibility(View.GONE);
                holder.rate.setVisibility(View.GONE);
            }
            holder.img_play.setVisibility(View.VISIBLE);

            // holder.callFrom.setText(ci.getCalltype().equals("0") ? "Call From" : ci.getCalltype().equals("1") ? "Call From" : "Call To");


            if (ci.getCalltype().equals("0") || ci.getFilename().equals("")) {
                holder.img_play.setVisibility(View.INVISIBLE);
                holder.review.setVisibility(View.GONE);
                holder.rate.setVisibility(View.GONE);
            } else {
                if (ci.getSeen() != null && ci.getSeen().equals("1")) {
                    //if api 17  drwle with red image
                    if (Build.VERSION.SDK_INT < 18) {
                        holder.img_play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_seen));
                        notifyItemChanged(position);
                    } else
                        holder.img_play.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.SRC_ATOP);
                    notifyItemChanged(position);

                } else {
                    holder.img_play.getBackground().setColorFilter(fetchAccentColor(), PorterDuff.Mode.SRC_ATOP);
                    holder.review.setVisibility(View.GONE);
                    holder.rate.setVisibility(View.GONE);
                }
//                if (!ci.getReview().equals("0")) {
//                    holder.review.setVisibility(View.VISIBLE);
//                    holder.rate.setVisibility(View.VISIBLE);
//                } else {
//                    holder.review.setVisibility(View.GONE);
//                    holder.rate.setVisibility(View.GONE);
//                }
//
//                holder.review.setText(ci.getReview());
//                holder.img_play.setVisibility(View.VISIBLE);
            }


            //  holder.statusTextView.setText(ci.getCalltype().equals("0") ? MISSED : ci.getCalltype().equals("1") ? INCOMING : OUTGOING);
            Log.d(TAG, "" + ci.getCalltype().equals("0"));
            //    holder.contactphoto.setImageBitmap(getFacebookPhoto(ci.getCallto()));


        } catch (Exception e) {
            // Log.d("TAG", e.getMessage());
        }
        ;
        previousPosition = position;

    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    @Override
    public int getItemCount() {
        return CallDataArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class OnOverflowSelectedListener implements View.OnClickListener {
        private Context mContext;
        private int position;
        private ArrayList<CallData> callDatas;
        private DownloadFile fileDownloadFragment;
        private DownloadFile mTaskFragment;

        public OnOverflowSelectedListener(Context context, int pos, ArrayList<CallData> callDatas) {
            mContext = context;
            this.position = pos;
            this.callDatas = callDatas;

        }

        @Override
        public void onClick(final View v) {
            //creating a popup menu
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            //adding click listener
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.call:
                            if (!Utils.isEmpty(callDatas.get(position).getCallto())) {
                                Utils.makeAcall(callDatas.get(position).getCallto(), (Home) mContext);
                            } else {
                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        case R.id.sms:
                            if (!Utils.isEmpty(callDatas.get(position).getCallto())) {
                                Utils.sendSms(callDatas.get(position).getCallto(), (Home) mContext);
                            } else {
                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        case R.id.location:
                            if (ConnectivityReceiver.isConnected()) {
                                Gson gson = new Gson();
                                String TrackInfo = gson.toJson(callDatas.get(position));
                                Intent intent = new Intent(mContext, LocationActivity.class);
                                intent.putExtra("DATA", TrackInfo);
                                mContext.startActivity(intent);
                            } else {
                                Toast.makeText(mContext, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                            }
                            return true;

                        case R.id.share:
                            ((Home) mContext).onShareFile(callDatas.get(position).getFilename());
                            return true;
                        case R.id.rate:
                            ((Home) mContext).onRatingsClick(callDatas.get(position));
                            return true;


                    }
                    return false;
                }
            });
            //displaying the popup
            //popup.show();


//            PopupMenu popupMenu = new PopupMenu(mContext, v) {
//                @Override
//                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.call:
//                            if (!Utils.isEmpty(callDatas.get(position).getCallto())) {
//                                Utils.makeAcall(callDatas.get(position).getCallto(), (Home) mContext);
//                            } else {
//                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
//                            }
//                            return true;
//                        case R.id.sms:
//                            if (!Utils.isEmpty(callDatas.get(position).getCallto())) {
//                                Utils.sendSms(callDatas.get(position).getCallto(), (Home) mContext);
//                            } else {
//                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
//                            }
//                            return true;
//                        case R.id.location:
//                            if (ConnectivityReceiver.isConnected()) {
//                                Gson gson = new Gson();
//                                String TrackInfo = gson.toJson(callDatas.get(position));
//                                Intent intent = new Intent(mContext, LocationActivity.class);
//                                intent.putExtra("DATA", TrackInfo);
//                                mContext.startActivity(intent);
//                            } else {
//                                Toast.makeText(mContext, "No Internet Connection.", Toast.LENGTH_SHORT).show();
//                            }
//                            return true;
//
//                        case R.id.share:
//                            ((Home) mContext).onShareFile(callDatas.get(position).getFilename());
//                            return true;
//                        case R.id.rate:
//                            ((Home) mContext).onRatingsClick(callDatas.get(position));
//                            return true;
//
//                        default:
//                            return super.onMenuItemSelected(menu, item);
//                    }
//                }
//            };

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

            try {
                if (!Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() > 7) {
                    popupMenu.inflate(R.menu.popupmenu_cmsl);
                } else if (!Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() <= 7) {
                    popupMenu.inflate(R.menu.popupmenu_cms);
                } else if (Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() > 7) {
                    popupMenu.inflate(R.menu.popupmenu_cml);
                } else {
                    popupMenu.inflate(R.menu.popupmenu);
                }
//                if (Utils.getFromPrefs(mContext, USERTYPE, DEFAULT).equals("0")) {
//                    if(popupMenu.getMenu().findItem(R.id.share)!=null)
//                    popupMenu.getMenu().findItem(R.id.share).setVisible(false);
//                } else {
//                    if(popupMenu.getMenu().findItem(R.id.share)!=null)
//                    popupMenu.getMenu().findItem(R.id.share).setVisible(true);
//                    //popupMenu.getMenu().findItem(R.id.share).setTitle("Share Edit");
//                }
                popupMenu.show();
            } catch (Exception e) {
                if (e.getMessage().toString() != null) {
                    Log.d("ERROR", e.getMessage().toString());
                }
            }
        }
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // private final ImageView overflow, rate, home;
        private ImageView rate;
        protected TextView callFromTextView, callerNameTextView, listItem, undo,
                groupNameTextView, dateTextView, timeTextView, statusTextView, callFrom, review;
        protected ImageButton ibcall, ibmessage;
        private ArrayList<CallData> CallDataArrayList;
        private CallClickedListner callClickedListner;
        public ImageView contactphoto, img_play, call_img, imgCallStatus, imgCallerstatue;
        private LinearLayout overflowMenu, ratelayout, regularLayout, swipeLayout;
        private BoomMenuButton bmb3;

        public CallViewHolder(View v, ArrayList<CallData> callDataArrayList, CallClickedListner callClickedListner) {
            super(v);
            overflowMenu = (LinearLayout) v.findViewById(R.id.overflowmenu);
            ratelayout = (LinearLayout) v.findViewById(R.id.rateLayout);
            callFromTextView = (TextView) v.findViewById(R.id.fCallFromTextView);
            //callFrom = (TextView) v.findViewById(R.id.fCallFromLabel);
            review = (TextView) v.findViewById(R.id.review);
            img_play = (ImageView) v.findViewById(R.id.ivplay);
            rate = (ImageView) v.findViewById(R.id.rate);
            call_img = (ImageView) v.findViewById(R.id.call_img);
            imgCallStatus = (ImageView) v.findViewById(R.id.call_status);
            imgCallerstatue = (ImageView) v.findViewById(R.id.fGroupNameLabel);
            //  home = (ImageView) v.findViewById(R.id.home);
            callerNameTextView = (TextView) v.findViewById(R.id.fCallerNameTextView);
            groupNameTextView = (TextView) v.findViewById(R.id.fGroupNameTextView);
            dateTextView = (TextView) v.findViewById(R.id.fDateTextView);
            timeTextView = (TextView) v.findViewById(R.id.fTimeTextView);
            // statusTextView = (TextView) v.findViewById(R.id.fStatusTextView);
            // overflow = (ImageView) v.findViewById(R.id.ic_more);
            // contactphoto = (ImageView) v.findViewById(R.id.df);
            this.bmb3 = (BoomMenuButton) v.findViewById(R.id.bmb3);

            //callFromTextView=(TextView) v.findViewById(R.id.ch);
            this.callClickedListner = callClickedListner;
            this.CallDataArrayList = callDataArrayList;
            v.setClickable(true);
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (callClickedListner != null) {
                callClickedListner.OnItemClick(CallDataArrayList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }


    public interface CallClickedListner {
        void OnItemClick(CallData callData, int position);
    }

    public void setTextTheme(TextView view) {
        int id = Integer.parseInt(Utils.getFromPrefs(context, THEME, "5"));
        ;
        switch (id) {
            case 0:
                view.setTextColor(Color.parseColor("#2196F3"));
                break;
            case 1:
                view.setTextColor(Color.parseColor("#F44336"));
                break;
            case 2:
                view.setTextColor(Color.parseColor("#8BC34A"));
                break;
            default:
                view.setTextColor(Color.parseColor("#FF5722"));
                break;
        }
    }


}
