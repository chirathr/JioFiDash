package com.chirathr.jiofidash.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.NotificationUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;
import org.json.JSONObject;

public class BatteryReminderJobService extends JobService {

    private static final String TAG = BatteryReminderJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters job) {

        final Context context = BatteryReminderJobService.this;
        String urlString = NetworkUtils.getUrlString(NetworkUtils.DEVICE_INFO_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG, response.toString());

                try {
                    int batteryPercent = JioFiData.getBatteryLevel(response.getString(JioFiData.BATTERY_LEVEL));
                    // Show battery low warning
                    if (batteryPercent <= 20) {
                        // < 20, 15, 10, 5, 2
                        // TODO to reminder to once
                        NotificationUtils.remindUserBatteryLow(context, batteryPercent);
                    }
                } catch (JSONException e) {
                    Log.v(TAG, "JSONException: " + e.getMessage());
                }

                jobFinished(job, false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Volley error: " + error.getMessage());
                jobFinished(job, true);
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
