package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.chirathr.jiofidash.adapters.DeviceListAdapter;
import com.chirathr.jiofidash.data.DeviceViewModel;
import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.fragments.ChangeSSIDPasswordDialogFragment;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WiFiSettings extends AppCompatActivity
        implements ChangeSSIDPasswordDialogFragment.OnChangeSSIDCompleteListener,
        DeviceListAdapter.OnClickListener{

    private static final String TAG = WiFiSettings.class.getSimpleName();
    private static int DELAY_SSID = 5000;
    private static int DELAY = 3000;

    private static boolean updateUi = true;

    private TextView wiFiSSIDTextView;
    private TextView wiFiDeviceCount;
    private ProgressBar loadingProgressBar;
    private ConstraintLayout wifiSettingsLayout;
    private ConstraintLayout wifiLayoutView;
    private ConstraintLayout devicesLayoutView;

    private List<DeviceViewModel> deviceViewModels;

    private RecyclerView mRecyclerView;
    private DeviceListAdapter mDeviceListAdapter;

    private Handler handler;
    private Runnable loadDeviceListRunnable = new Runnable() {
        @Override
        public void run() {
            loadDeviceList(WiFiSettings.this);
            if (updateUi) {
                handler.postDelayed(loadDeviceListRunnable, DELAY);
            }
        }
    };

    private Runnable loadSSIDRunnable = new Runnable() {
        @Override
        public void run() {
            loadDevicesData(WiFiSettings.this);
            if (updateUi) {
                handler.postDelayed(loadSSIDRunnable, DELAY_SSID);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        wiFiSSIDTextView = findViewById(R.id.tv_wifi_ssid);
        wiFiDeviceCount = findViewById(R.id.tv_device_count);
        loadingProgressBar = findViewById(R.id.progress_bar_wifi_settings);
        wifiLayoutView = findViewById(R.id.wifi_layout);
        devicesLayoutView = findViewById(R.id.devices_layout);
        wifiSettingsLayout = findViewById(R.id.wifi_settings_layout);

        mRecyclerView = findViewById(R.id.users_list_recyler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        handler = new Handler();
        showLoading();
        wifiLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeSSIDPassDialog();
            }
        });

        updateUi = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!updateUi) {
            updateUi = true;
            new loginAsyncTask().execute();
        }
    }

    public class loginAsyncTask extends AsyncTask<Void, Void, Void> {

        private boolean isSuccessful = true;
        @Override
        protected Void doInBackground(Void... voids) {
            if (!NetworkUtils.isLoggedIn()) {
                if (!NetworkUtils.login(WiFiSettings.this))
                     isSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSuccessful) {
                handler.post(loadDeviceListRunnable);
                handler.post(loadSSIDRunnable);
            } else {
                updateUi = false;
            }

            Snackbar.make(wifiSettingsLayout, "Login failed, try again later", Snackbar.LENGTH_LONG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUi = false;
    }

    private static final String CSS_SELECTOR_BLOCKED_ITEM_LIST = "table[id='active_deny_list'] td[class='text_list']";

    public void loadDeviceList(final Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.LAN_INFO_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                deviceViewModels = new ArrayList<>();
                try {
                    wiFiDeviceCount.setText(response.getString(JioFiData.USER_COUNT));
                    String deviceListString = response.getString(JioFiData.USER_LIST);
                    String[] deviceList = deviceListString.split(";");

                    for (String deviceInfoString: deviceList) {
                        if (!deviceInfoString.contains("Static"))
                            deviceViewModels.add(new DeviceViewModel(deviceInfoString));
                    }

                    loadAndUpdateBlockedDevices(context);

                } catch (JSONException e) {
                    Log.v(TAG, "userlistinfo not found in json response or json error: " + e.getMessage());
                    showLoading();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void loadAndUpdateBlockedDevices(Context context) {

        String urlString = NetworkUtils.getUrlString(NetworkUtils.WIFI_MAC_GET_ID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Load the blocked List
                        Document wiFiMacDocument = Jsoup.parse(response);
                        Elements elementList = wiFiMacDocument.select(CSS_SELECTOR_BLOCKED_ITEM_LIST);

                        for (int i = 0; i < elementList.size(); i += 2) {
                            String macAddress = elementList.get(i).text().trim();
                            String name = elementList.get(i + 1).text().trim();
                            DeviceViewModel deviceViewModel = new DeviceViewModel(name, macAddress);
                            deviceViewModels.add(deviceViewModel);
                        }

                        // Update the UI
                        if (mDeviceListAdapter == null) {
                            mDeviceListAdapter = new DeviceListAdapter(WiFiSettings.this);
                            mDeviceListAdapter.setDeviceViewModels(deviceViewModels);
                            mRecyclerView.setAdapter(mDeviceListAdapter);
                        } else {
                            mDeviceListAdapter.setDeviceViewModels(deviceViewModels);
                        }
                        showData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return NetworkUtils.getAuthHeaders();
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void loadDevicesData(Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.LAN_INFO_PAGE_ID);

        StringRequest lanInfoRequest = new StringRequest(Request.Method.GET,
                urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document lanInfoDocument = Jsoup.parse(response);
                wiFiSSIDTextView.setText(lanInfoDocument.getElementById("ssid").text());
                showData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Volley string request error: " + error.getMessage());
                showLoading();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(lanInfoRequest);
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        wifiLayoutView.setAlpha(Float.parseFloat("0.2"));
        devicesLayoutView.setAlpha(Float.parseFloat("0.2"));
    }

    private void showData() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        wifiLayoutView.setAlpha(Float.parseFloat("1.0"));
        devicesLayoutView.setAlpha(Float.parseFloat("1.0"));
    }

    public void showChangeSSIDPassDialog() {
        DialogFragment changeSSIDPassFragment = new ChangeSSIDPasswordDialogFragment();
        changeSSIDPassFragment.show(
                getSupportFragmentManager(), ChangeSSIDPasswordDialogFragment.FRGAMENT_TAG);
    }

    @Override
    public void onChangeSSIDCompleteListener() {
        Snackbar.make(wifiSettingsLayout, "SSID and Password changed.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoadSSIDFailedListener() {
        Snackbar.make(wifiSettingsLayout, "Loading password failed!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showChangeSSIDPassDialog();
                    }
                }).show();
    }

    @Override
    public void onClickBlockListener(int itemId) {


        // TODO setUp an AsyncTask to block and save this item to the database
        DeviceViewModel temp = deviceViewModels.remove(itemId);
        temp.setIsBlocked(true);
        Snackbar.make(wifiSettingsLayout, temp.getDeviceName() , Snackbar.LENGTH_LONG).show();
        deviceViewModels.add(temp);
        mDeviceListAdapter.setDeviceViewModels(deviceViewModels);
    }

    @Override
    public void onClickUnBlockListener(int itemId) {
        Snackbar.make(wifiSettingsLayout, itemId + " Unblocked", Snackbar.LENGTH_LONG).show();
    }

    // TODO AsyncTask to block a device
    // TODO add blocked device to SQLite local storage
    // TODO Show blocked device under the list of devices
    // TODO AsyncTask to unblock a device and remove from sqlite storage

    // TODO change the appbar name to WiFI Settings and add a back button
}
