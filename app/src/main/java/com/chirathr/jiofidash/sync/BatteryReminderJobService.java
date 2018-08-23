package com.chirathr.jiofidash.sync;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.utils.NotificationUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;
import org.json.JSONObject;

public class BatteryReminderJobService extends JobService {

    private static final String TAG = BatteryReminderJobService.class.getSimpleName();
    private static final int LOW_BATTERY_PERCENTAGE = 20;

    @Override
    public boolean onStartJob(final JobParameters job) {

        final Context context = BatteryReminderJobService.this;
        String urlString = "http://jiofi.local.html/Device_info_ajax.cgi";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Show low battery notification
                try {
                    int batteryPercent = JioFiData.getBatteryLevel(response.getString(JioFiData.BATTERY_LEVEL));
                    String batteryStatus = response.getString(JioFiData.BATTERY_STATUS);

                    // Show battery full and low notifications
                    if (JioFiPreferences.getInstance().canShowNotification(context, batteryPercent, batteryStatus)) {
                        // 100, 20, 10, 5, 2
                        Log.v("JioFiPreferences", "True");
                        NotificationUtils.remindUserBatteryLow(context, batteryPercent);
                    } else {
                        Log.v("JioFiPreferences", "False");
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
                jobFinished(job, false);
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
