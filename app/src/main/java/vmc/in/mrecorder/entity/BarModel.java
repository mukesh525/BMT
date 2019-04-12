package vmc.in.mrecorder.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

/**
 * Created by gousebabjan on 27/5/16.
 */
public class BarModel implements Parcelable {
    protected BarModel(Parcel in) {
        empname = in.readString();
        inbound = in.readString();
        outbound = in.readString();
        missed = in.readString();
        count = in.readString();
    }

    public BarModel() {
    }

    public static final Creator<BarModel> CREATOR = new Creator<BarModel>() {
        @Override
        public BarModel createFromParcel(Parcel in) {
            return new BarModel(in);
        }

        @Override
        public BarModel[] newArray(int size) {
            return new BarModel[size];
        }
    };

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getInbound() {
        return inbound;
    }

    public void setInbound(String inbound) {
        this.inbound = inbound;
    }

    public String getOutbound() {
        return outbound;
    }

    public void setOutbound(String outbound) {
        this.outbound = outbound;
    }

    public String getMissed() {
        return missed;
    }

    public void setMissed(String missed) {
        this.missed = missed;
    }

    private String empname;
    private String inbound;
    private String outbound;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JSONArray getRecords() {
        return records;
    }

    public void setRecords(JSONArray records) {
        this.records = records;
    }

    private String missed;
    private String count;
    public static String code;
    private JSONArray records;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(empname);
        dest.writeString(inbound);
        dest.writeString(outbound);
        dest.writeString(missed);
        dest.writeString(count);
    }
}
