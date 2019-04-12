package vmc.in.mrecorder.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gousebabjan on 19/8/16.
 */
public class OTPData implements Parcelable{
    private String code,msg,Otp,mobileNumber;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    protected OTPData(Parcel in) {

        code = in.readString();
        msg = in.readString();
        Otp = in.readString();
    }

    public OTPData() {
    }

    public static final Creator<OTPData> CREATOR = new Creator<OTPData>() {
        @Override
        public OTPData createFromParcel(Parcel in) {
            return new OTPData(in);
        }

        @Override
        public OTPData[] newArray(int size) {
            return new OTPData[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOtp() {
        return Otp;
    }

    public void setOtp(String otp) {
        Otp = otp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(msg);
        dest.writeString(Otp);
    }
}
