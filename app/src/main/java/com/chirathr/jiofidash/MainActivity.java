package com.chirathr.jiofidash;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.fragments.BottomSheetFragment;
import com.chirathr.jiofidash.fragments.LoginDialog;
import com.chirathr.jiofidash.progressBar.ColorArcProgressBar;
import com.chirathr.jiofidash.sync.Utilities;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.NotificationUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements BottomSheetFragment.onOptionSelectedListener, LoginDialog.LoginCompleteListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DELAY = 1000;

    private View mainConstrainView;

    // Actions that are done after successful login
    public static final String LOGIN_COMPLETE_ACTION_RESTART = "restart";
    public static final String LOGIN_COMPLETE_ACTION_OPEN_WIFI_SETTINGS = "wifi-settings";
    public static final String LOGIN_COMPLETE_ACTION_WPS_BUTTON = "wps-button";

    private ColorArcProgressBar batteryProgressBar;
    private Handler handler;
    private Runnable batteryUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (updateUI) {
                loadBatteryInfo(getApplicationContext());
                handler.postDelayed(batteryUpdateRunnable, DELAY);
            }
        }
    };

    private BottomSheetFragment bottomSheetFragment;

    // Controls all the network and Ui update processes on MainActivity
    public static boolean updateUI = true;
    private Snackbar noJioFiSnackBar;

    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainConstrainView = findViewById(R.id.main_layout);

        handler = new Handler();
        batteryProgressBar = (ColorArcProgressBar) findViewById(R.id.batteryProgressBar);
        handler.post(batteryUpdateRunnable);
        loadingProgressBar = findViewById(R.id.battery_loading_progress_bar);

        bottomSheetFragment = new BottomSheetFragment();

        findViewById(R.id.wifi_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (JioFiPreferences.getInstance().isLoginDataAvailable(MainActivity.this)) {
                    startActivity(new Intent(MainActivity.this, WiFiSettings.class));
                } else {
                    showLoginDialog(LOGIN_COMPLETE_ACTION_OPEN_WIFI_SETTINGS);
                }
            }
        });

        noJioFiSnackBar = Snackbar.make(
                mainConstrainView, "JioFi not found, check your WiFi.", Snackbar.LENGTH_INDEFINITE);

        // Start the battery notification task
        Utilities.scheduleBatteryJob(this);
        showLoading();

        NotificationUtils.remindUserBatteryLow(this, 20);
    }

    @Override
    protected void onResume() {
        updateUI = true;
        handler.post(batteryUpdateRunnable);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUI = false;
    }

    private void showJioFiNotFoundSnackBar() {
        if (!noJioFiSnackBar.isShown())
            noJioFiSnackBar.show();
    }

    private void hideJioFiNotFoundSnackBar() {
        if (noJioFiSnackBar.isShown())
            noJioFiSnackBar.dismiss();
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

        // Open bottom sheet dialog fragment
        if (selectedItemId == R.id.action_settings) {
            showBottomSheetDialogFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // function that runs a volley json request to get battery state and battery level
    private void loadBatteryInfo(Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.DEVICE_INFO_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v(TAG, response.toString());

                try {
                    int batteryPercent = JioFiData.getBatteryLevel(response.getString(JioFiData.BATTERY_LEVEL));
                    batteryProgressBar.setCurrentValues(batteryPercent);
                    batteryProgressBar.setUnit(response.getString(JioFiData.BATTERY_STATUS));
                    batteryProgressBar.setTitle(JioFiData.calculateRemainingTimeString(getApplicationContext(), batteryPercent));
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException: " + e.getMessage());
                }

                hideJioFiNotFoundSnackBar();
                showDataUsage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showJioFiNotFoundSnackBar();
                showLoading();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void showBottomSheetDialogFragment() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    // Event listener that handles actions clicked on BottomSheetFragment
    @Override
    public void onOptionSelected(int optionId) {
        switch (optionId) {
            case BottomSheetFragment.OPTION_RESTART_ID: {
                bottomSheetFragment.dismiss();
                if (JioFiPreferences.getInstance().isLoginDataAvailable(this)) {
                    restartJioFi();
                } else {
                    showLoginDialog(LOGIN_COMPLETE_ACTION_RESTART);
                }
                break;
            }
            case BottomSheetFragment.OPTION_WIFI_SETTINGS_ID: {
                bottomSheetFragment.dismiss();
                if (JioFiPreferences.getInstance().isLoginDataAvailable(this)) {
                    startActivity(new Intent(this, WiFiSettings.class));
                } else {
                    showLoginDialog(LOGIN_COMPLETE_ACTION_OPEN_WIFI_SETTINGS);
                }
                break;
            }
            case BottomSheetFragment.OPTION_PUSH_WPS_BUTTON: {
                bottomSheetFragment.dismiss();
                if (JioFiPreferences.getInstance().isLoginDataAvailable(this)) {
                    pushWPSButton();
                } else {
                    showLoginDialog(LOGIN_COMPLETE_ACTION_WPS_BUTTON);
                }
                break;
            }
            case BottomSheetFragment.OPTION_CHANGE_SAVED_USERNAME_PASSWORD: {
                bottomSheetFragment.dismiss();
                showLoginDialog(null);
                break;
            }
            case BottomSheetFragment.OPTION_CHANGE_SELECTED_DEVICE: {
                bottomSheetFragment.dismiss();
                // Clear selected device and load onBoarding activity
                JioFiPreferences.getInstance().clearSelectedDevice(this);
                startActivity(new Intent(this, OnBoarding.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            }
            case BottomSheetFragment.OPTION_ADMIN_WEB_UI: {
                bottomSheetFragment.dismiss();
                Uri web_page = Uri.parse(NetworkUtils.DEFAULT_HOST);
                Intent intent = new Intent(Intent.ACTION_VIEW, web_page);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    //Page not found
                    Log.v(TAG, "page not found, open web page.");
                }
                break;
            }
            case BottomSheetFragment.OPTION_ABOUT_ID: {
                startActivity(new Intent(this, About.class));
                break;
            }
        }
    }

    // Show login dialog and complete the action on successful login
    public void showLoginDialog(String action) {
        DialogFragment loginDialog = new LoginDialog();
        ((LoginDialog) loginDialog).setActionAfterLogin(this, action);
        loginDialog.show(getSupportFragmentManager(), "LoginDialog");
    }

    // Event listener that is called after successful login with corresponding action to be done
    @Override
    public void loginCompleteListener(String action) {
        if (action == null) {
            return;
        }
        switch (action) {
            case LOGIN_COMPLETE_ACTION_RESTART: {
                restartJioFi();
                break;
            }
            case LOGIN_COMPLETE_ACTION_OPEN_WIFI_SETTINGS: {
                Intent intent = new Intent(this, WiFiSettings.class);
                startActivity(intent);
                break;
            }
            case LOGIN_COMPLETE_ACTION_WPS_BUTTON: {
                pushWPSButton();
                break;
            }
            default: Log.v(TAG, "Loggeg in");
        }
    }

    // Async task that restarts JioFi by setting the power save time out.
    private class RestartJioFiAsyncTask extends AsyncTask<Void, Void, Void> {

        private boolean restarted;

        @Override
        protected void onPreExecute() {
            restarted = false;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            restarted = NetworkUtils.changePowerSavingTimeOut(getApplicationContext(), true, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (restarted) {
                Log.v(TAG, "Restart successful");
                Snackbar.make(mainConstrainView, R.string.restart_successful, Snackbar.LENGTH_LONG)
                        .show();
                wiFiRestart();
            } else {
                Log.e(TAG, "Restart failed");
                Snackbar.make(mainConstrainView, "Restart failed!", Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (JioFiPreferences.getInstance().isLoginDataAvailable(MainActivity.this)) {
                                    restartJioFi();
                                } else {
                                    showLoginDialog(LOGIN_COMPLETE_ACTION_RESTART);
                                }
                            }
                        }).show();
            }
        }
    }

    public void restartJioFi() {
        new RestartJioFiAsyncTask().execute();
    }

    private void pushWPSButton() {
        if (JioFiPreferences.getInstance().isWPSEnabled(this)) {
            Snackbar.make(mainConstrainView, "WPS button already enabled.", Snackbar.LENGTH_LONG).show();
        } else {
            new WPSButtonAsyncTask().execute();
        }
    }

    // Async task that enables WPS
    private class WPSButtonAsyncTask extends AsyncTask<Void, Void, Void> {

        private boolean isSuccessful = false;

        @Override
        protected Void doInBackground(Void... voids) {
            isSuccessful = NetworkUtils.pushWPSButton(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSuccessful) {
                Snackbar.make(mainConstrainView, "WPS button pressed, connect your device", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(mainConstrainView, "WPS button error, please try after sometime.", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pushWPSButton();
                            }
                        }).show();
            }
        }
    }

    public void wiFiRestart() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wiFiRestart();
                }
            }, 1000);
        }
        else {
            wifiManager.setWifiEnabled(true);
        }
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        batteryProgressBar.setAlpha(Float.parseFloat("0.2"));
    }

    private void showDataUsage() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        batteryProgressBar.setAlpha(Float.parseFloat("1.0"));
    }
}
