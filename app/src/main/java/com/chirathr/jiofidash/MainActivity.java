package com.chirathr.jiofidash;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.fragments.BottomSheetFragment;
import com.chirathr.jiofidash.fragments.LoginDialog;
import com.chirathr.jiofidash.progressBar.ColorArcProgressBar;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements BottomSheetFragment.onOptionSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DELAY = 1000;

    private View mainCordinateView;

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

    private BottomSheetFragment bottomSheetFragment;
    private RestartJioFiAsyncTask restartJioFiAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainCordinateView = findViewById(R.id.main_layout);

        handler = new Handler();
        batteryProgressBar = (ColorArcProgressBar) findViewById(R.id.batteryProgressBar);
        handler.post(batteryUpdateRunnable);

        bottomSheetFragment = new BottomSheetFragment();
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

        if (selectedItemId == R.id.action_settings) {
            showBottomSheetDialogFragment();
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
                    batteryProgressBar.setTitle(JioFiData.calculateRemainingTimeString(getApplicationContext(), batteryPercent));
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

    public void showBottomSheetDialogFragment() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public void onOptionSelected(int optionId) {
        switch (optionId) {
            case BottomSheetFragment.OPTION_RESTART_ID: {
                bottomSheetFragment.dismiss();
                loginIfNeeded();
                restartJioFi();
                break;
            }
            case BottomSheetFragment.OPTION_WIFI_SETTINGS_ID: {
                bottomSheetFragment.dismiss();
                Intent intent = new Intent(this, WiFiSettings.class);
                startActivity(intent);
                break;
            }
            case BottomSheetFragment.OPTION_ADMIN_WEB_UI: {
                bottomSheetFragment.dismiss();
                Uri webpage = Uri.parse(NetworkUtils.DEFAULT_HOST);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else{
                    //Page not found
                    Log.v(TAG, "page not found, open web page.");
                }
            }
        }
    }

    public void loginIfNeeded() {
        if (!JioFiPreferences.getInstance().isLoginDataAvailable(this)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            LoginDialog loginDialog = new LoginDialog();
            loginDialog.show(fragmentManager, "LoginDialog");
        }
    }

    public void restartJioFi() {
        restartJioFiAsyncTask = new RestartJioFiAsyncTask();
        restartJioFiAsyncTask.execute();
    }

    private class RestartJioFiAsyncTask extends AsyncTask<Void, Void, Void> {

        private boolean restarted;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            restarted = false;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            restarted = NetworkUtils.changePowerSavingTimeOut(getApplicationContext(), false, 10);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (restarted) {
                Log.v(TAG, "Restart successful");

                NetworkUtils.clearLogin();

                Snackbar.make(mainCordinateView, R.string.restart_successful, Snackbar.LENGTH_LONG)
                        .show();
            }
            else {
                Log.v(TAG, "Restart failed");

                Snackbar.make(mainCordinateView, "Restart failed!", Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        loginIfNeeded();
                                        restartJioFi();
                                    }
                                })
                        .show();
            }
        }
    }
}
