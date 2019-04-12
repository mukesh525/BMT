package vmc.in.mrecorder.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

/**
 * Created by gousebabjan on 27/5/16.
 */
public class PieModel implements Parcelable {

    private String calltype;
    private String count;

    protected PieModel(Parcel in) {
        calltype = in.readString();
        count = in.readString();
    }
    public PieModel(){

    }

    public static final Creator<PieModel> CREATOR = new Creator<PieModel>() {
        @Override
        public PieModel createFromParcel(Parcel in) {
            return new PieModel(in);
        }

        @Override
        public PieModel[] newArray(int size) {
            return new PieModel[size];
        }
    };

    public static String getCode() {
        return code;
    }

    public static void setCode(String code) {
        PieModel.code = code;
    }

    public static String code;


    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(calltype);
        dest.writeString(count);
    }
}
