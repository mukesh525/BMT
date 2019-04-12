package vmc.in.mrecorder.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import vmc.in.mrecorder.callbacks.TAG;

/**
 * Created by gousebabjan on 17/3/16.
 */
public class JSONParser implements TAG{
    static JSONObject jObj = null;

    public static JSONObject login(String url1, String email, String password,String deviceid,String gcmkey) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(EMAIL, email);
        params.put(PASSWORD, password);
        params.put(DEVICE_ID, deviceid);
        params.put(GCM_KEY, gcmkey);

       Log.d("GCMPRO",email+" "+password+" "+deviceid+" "+gcmkey);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("LOG", result.toString());
        jObj = new JSONObject(result.toString());
        Log.d("RESPONSE",result.toString());
        return jObj;

    }

    public static JSONObject getOTP(String url1, String email,String password) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(EMAIL, email);
        params.put(PASSWORD, password);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        jObj = new JSONObject(result.toString());
        Log.d("RESPONSEOTP",result.toString());
        return jObj;

    }
    public static JSONObject SubmitFeedBack(String url1, String authkey, String Feedback) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(AUTHKEY, authkey);
        params.put(FEEDBACK, Feedback);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        //Log.d("LOG", result.toString());
        jObj = new JSONObject(result.toString());
        return jObj;

    }

    public static JSONObject ChangePass(String url1, String phone, String otp, String password) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(OTP, otp);
        // Log.d("OTP", otp);
        params.put(NUMBER, phone);
        //  Log.d("OTP",phone);
        params.put(PASSWORD, password);
        //  Log.d("OTP", password);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        jObj = new JSONObject(result.toString());
        return jObj;

    }


    public static JSONObject getCallsData(String url1, String authKey, String limit, String offset, String deviceid,String type) throws Exception {

        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(AUTHKEY, authKey);
        params.put(TYPE, type);
        params.put(OFFSET, offset);
        params.put(LIMIT, limit);
        params.put(DEVICE_ID, deviceid);
        Log.d(TAG, "url  " + url);
        Log.d(TAG, "Post Parameters................!!");
        Log.d(TAG, AUTHKEY + authKey);
        Log.d(TAG, TYPE+ type);
        Log.d(TAG, OFFSET + offset);
        Log.d(TAG, LIMIT + limit);
        Log.d(TAG, DEVICE_ID + deviceid);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("Test",result.toString());
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }


    public static JSONObject getEmpdata(String url1, String reporttype,String deviceid,String authkey) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(REPORTTYPE, reporttype);
       params.put(DEVICE_ID, deviceid);
        params.put(AUTHKEY, authkey);

        Log.d("LOGIN",reporttype+" "+deviceid+" "+deviceid+" "+authkey);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("LOG", result.toString());
        jObj = new JSONObject(result.toString());
        Log.d("RESPONSE",result.toString());
        return jObj;

    }

}
