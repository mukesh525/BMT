package vmc.in.mrecorder.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import vmc.in.mrecorder.callbacks.TAG;

/**
 * Created by vmc on 15/3/17.
 */

public class ErrorReport implements vmc.in.mrecorder.callbacks.TAG {

    public void sendError(final String error, final Context mContext) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest strRequest = new StringRequest(Request.Method.POST, ERROR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Voley", response.toString());
                        try {
                            JSONObject obj = new JSONObject(response);

                        } catch (JSONException e) {
                            Log.d("Voley", "Invalid Response  " + response.toString());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Voley", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("Voley", Utils.getFromPrefs(mContext, AUTHKEY, UNKNOWN));
                params.put(AUTHKEY, Utils.getFromPrefs(mContext, AUTHKEY, UNKNOWN));
                params.put("Error", error);
                params.put(DEVICE, DeviceName.getDeviceName());

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(strRequest);

    }
}