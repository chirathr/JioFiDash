package com.chirathr.jiofidash;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.progressBar.ColorArcProgressBar;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DELAY = 1000;

    private ColorArcProgressBar batteryProgressBar;
    private boolean updateUI = true;
    private Handler handler;
    private Runnable batteryUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            loadBatteryInfo(getApplicationContext());
            if (updateUI) {
                handler.postDelayed(batteryUpdateRunnable, DELAY);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        batteryProgressBar = (ColorArcProgressBar) findViewById(R.id.batteryProgressBar);

        handler.post(batteryUpdateRunnable);
    }

    @Override
    protected void onResume() {
        if (!updateUI) {
            updateUI = true;
            handler.post(batteryUpdateRunnable);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUI = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedItemId = item.getItemId();

        if (selectedItemId == R.id.action_restart) {
            NetworkUtils.changePowerSavingTimeOut(this, 10);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadBatteryInfo(Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.DEVICE_INFO_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    int batteryPercent = JioFiData.getBatteryLevel(response.getString(JioFiData.BATTERY_LEVEL));
                    batteryProgressBar.setCurrentValues(batteryPercent);
                    batteryProgressBar.setUnit(response.getString(JioFiData.BATTERY_STATUS));
                } catch (JSONException e) {
                    Log.v(TAG, "JSONException: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
