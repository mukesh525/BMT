package vmc.in.mrecorder.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.BarModel;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.entity.LoginData;

import vmc.in.mrecorder.entity.OTPData;
import vmc.in.mrecorder.entity.PieModel;
import vmc.in.mrecorder.entity.RateData;

/**
 * Created by gousebabjan on 30/3/16.
 */
public class Parser implements TAG {
    public static String code;

    public synchronized static LoginData ParseLoginResponse(JSONObject response) throws JSONException {
        LoginData loginData = new LoginData();
        if (response != null) {
            if (response.has(CODE)) {
                loginData.setCode(response.getString(CODE));
            }
            if (response.has(MESSAGE)) {
                loginData.setMessage(response.getString(MESSAGE));
            }
            if (response.has(AUTHKEY)) {
                loginData.setAuthcode(response.getString(AUTHKEY));
            }
            if (response.has(NAME)) {
                loginData.setUsername(response.getString(NAME));
            }
            if (response.has(USERTYPE)) {
                loginData.setUsertype(response.getString(USERTYPE));
            }
            if (response.has(RECORDING)) {
                loginData.setRecording(response.getString(RECORDING));
            }
            if (response.has(MCUBECALLS)) {
                loginData.setMcuberecording(response.getString(MCUBECALLS));
            }
            if (response.has(WORKHOUR)) {
                loginData.setWorkhour(response.getString(WORKHOUR));

            }
            return loginData;
        }
        return null;
    }

    public synchronized static OTPData ParseOTPResponse(JSONObject response) throws JSONException {
        OTPData OtpData = new OTPData();
        if (response != null) {
            if (response.has(CODE)) {
                OtpData.setCode(response.getString(CODE));
            }
            if (response.has(MESSAGE)) {
                OtpData.setMsg(response.getString(MESSAGE));
            }
            if (response.has(OTP)) {
                OtpData.setOtp(response.getString(OTP));
            }
            if (response.has(MOBILE_NUMBER)) {
                OtpData.setMobileNumber(response.getString(MOBILE_NUMBER));
            }
            return OtpData;
        }
        return null;
    }

    public synchronized static ArrayList<BarModel> ParseEMPResponse(JSONObject response) throws JSONException {

        ArrayList<BarModel> barModels = new ArrayList<BarModel>();
        BarModel barModel;
        JSONArray records = null;
        if (response != null) {

            if (response.has(CODE)) {
                code = response.getString(CODE);
            }
            if (response.has(RECORDS)) {
                records = response.getJSONArray(RECORDS);
            }

            if (records != null) {
                for (int i = 0; i < records.length(); i++) {
                    JSONObject jsonobj = response.getJSONArray(RECORDS).getJSONObject(i);
                    barModel = new BarModel();
                    if (jsonobj.has(EMPNAME)) {
                        barModel.setEmpname(jsonobj.getString(EMPNAME));
                    }
                    if (jsonobj.has(Inbound)) {
                        barModel.setInbound(jsonobj.getString(Inbound));
                    }
                    if (jsonobj.has(Outbound)) {
                        barModel.setOutbound(jsonobj.getString(Outbound));
                    }
                    if (jsonobj.has(missed)) {
                        barModel.setMissed(jsonobj.getString(missed));
                    }

                    barModels.add(barModel);
                }
            }
            return barModels;
        }
        return null;
    }

    public synchronized static ArrayList<PieModel> ParseTypeResponse(JSONObject response) throws JSONException {

        ArrayList<PieModel> pieModels = new ArrayList<>();
        JSONArray records = null;
        PieModel pieModel;
        if (response != null) {
            if (response.has(CODE)) {
                code = response.getString(CODE);
            }
            if (response.has(RECORDS)) {
                records = response.getJSONArray(RECORDS);
            }
            if (records != null) {
                for (int i = 0; i < records.length(); i++) {
                    JSONObject jsonobj = response.getJSONArray(RECORDS).getJSONObject(i);
                    pieModel = new PieModel();
                    if (jsonobj.has(CALLTYPEE)) {

                        pieModel.setCalltype(jsonobj.getString(CALLTYPEE).equals("0") ? MISSED :
                                jsonobj.getString(CALLTYPEE).equals("1") ? INCOMING : OUTGOING);
                        Log.d("CALLTYPE", jsonobj.getString(CALLTYPEE) + "");
                    }

                    if (jsonobj.has(COUNT)) {
                        pieModel.setCount(jsonobj.getString(COUNT));
                    }


                    pieModels.add(pieModel);
                }
            }

            return pieModels;
        }
        return null;
    }

    public static ArrayList<CallData> ParseData(JSONObject response) throws JSONException {
        ArrayList<CallData> CallList = new ArrayList<CallData>();
        JSONArray recordsArray = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
        if (response != null) {
            if (response.has(RECORDS)) {
                Log.d("RESPONSE", response.toString());
                recordsArray = response.getJSONArray(RECORDS);
                for (int i = 0; i < recordsArray.length(); i++) {
                    CallData callData = new CallData();
                    JSONObject record = (JSONObject) recordsArray.get(i);
                    if (record.has(CALLID)) {
                        callData.setCallid(record.getString(CALLID));
                    }
                    if (record.has(BID)) {
                        callData.setBid(record.getString(BID));
                    }
                    if (record.has(EID)) {
                        callData.setEid(record.getString(EID));
                    }
                    if (record.has(CALLFROM)) {
                        callData.setCallfrom(record.getString(CALLFROM));
                    }
                    if (record.has(CALLTO)) {
                        callData.setCallto(record.getString(CALLTO));
                    }
                    if (record.has(EMPNAME)) {
                        callData.setEmpname(record.getString(EMPNAME));
                    }
                    if (record.has(NAME)) {
                        callData.setName(record.getString(NAME));
                    }
                    if (record.has(CALLTYPEE)) {
                        callData.setCalltype(record.getString(CALLTYPEE));
                    }

                    if (record.has(STARTTIME)) {
                        callData.setStarttime(record.getString(STARTTIME));
                    }
                    if (record.has(ENDTIME)) {
                        callData.setEndtime(record.getString(ENDTIME));
                    }
                    if (record.has(FILENAME)) {
                        callData.setFilename(record.getString(FILENAME));
                        Log.d(TAG, callData.getFilename());
                    }

                    if (record.has(LOCATION)) {
                        callData.setLocation(record.getString(LOCATION));
                    }
                    if (record.has(LISTEN)) {
                        callData.setSeen(record.getString(LISTEN));
                    }

                    if (record.has(PULSE)) {
                        if (record.getString(PULSE) != null)
                            callData.setDuration(Integer.parseInt(record.getString(PULSE)));
                    }

                    if (record.has(RATING_COUNT)) {
                        callData.setReview(record.getString(RATING_COUNT));
                    } else {
                        callData.setReview("0");
                    }
                    Date startTime = null;
                    Date endTime = null;
                    try {
                        startTime = sdf.parse(record.getString(STARTTIME));
                        endTime = sdf.parse(record.getString(ENDTIME));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    callData.setStartTime(startTime);
                    callData.setEndTime(endTime);

                    CallList.add(callData);

                }
            }
            return CallList;
        }
        return null;
    }


    public static ArrayList<RateData> ParseReview(JSONObject response) throws JSONException {
        ArrayList<RateData> CallList = new ArrayList<RateData>();
        JSONArray recordsArray = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
        if (response != null) {
            if (response.has(RATING_LIST)) {
                Log.d("RESPONSE", response.toString());
                recordsArray = response.getJSONArray(RATING_LIST);
                for (int i = 0; i < recordsArray.length(); i++) {
                    RateData rateData = new RateData();
                    JSONObject record = (JSONObject) recordsArray.get(i);
                    if (record.has(COMMENT)) {
                        rateData.setDesc(record.getString(COMMENT));
                    }
                    if (record.has(EMPLOYEE)) {
                        rateData.setName(record.getString(EMPLOYEE));
                    }
                    if (record.has(RATING)) {
                        rateData.setRate(record.getString(RATING));
                    }
                    if (record.has(RATING_TITLE)) {
                        rateData.setTitle(record.getString(RATING_TITLE));
                    }

                    if (record.has(DATE)) {
                        Date startTime = null;
                        try {
                            startTime = sdf.parse(record.getString(DATE));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        rateData.setDate(startTime);

                    }
                    CallList.add(rateData);

                }
            }
            return CallList;
        }
        return null;
    }
}
