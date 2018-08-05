package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chirathr.jiofidash.adapters.DeviceListAdapter;
import com.chirathr.jiofidash.data.DeviceViewModel;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WiFiSettings extends AppCompatActivity {

    private static final String TAG = WiFiSettings.class.getSimpleName();
    private static int DELAY = 1000;

    private TextView wiFiSSIDTextView;
    private TextView wiFiDeviceCount;
    private Button changeSSIDPasswordButton;
    private DeviceViewModel deviceViewModel;

    private RecyclerView mRecyclerView;
    private DeviceListAdapter mDeviceListAdapter;

    private Handler handler;
    private Runnable loadWiFiDataRunnable = new Runnable() {
        @Override
        public void run() {
            loadDevicesData(WiFiSettings.this);
            handler.postDelayed(loadWiFiDataRunnable, DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_settings);

        wiFiSSIDTextView = findViewById(R.id.tv_wifi_ssid);
        changeSSIDPasswordButton = findViewById(R.id.change_ssid_password_button);
        wiFiDeviceCount = findViewById(R.id.tv_device_count);

        mRecyclerView = findViewById(R.id.users_list_recyler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        handler = new Handler();

        handler.post(loadWiFiDataRunnable);
    }

    public void loadDevicesData(Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.LAN_INFO_PAGE_ID);

        StringRequest lanInfoRequest = new StringRequest(Request.Method.GET,
                urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document lanInfoDocument = Jsoup.parse(response);
                wiFiSSIDTextView.setText(lanInfoDocument.getElementById("ssid").text());

                String noOfClients = lanInfoDocument.getElementById("noOfClient").text();
                wiFiDeviceCount.setText(String.valueOf(noOfClients));

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
//        loadingProgressBar.setVisibility(View.VISIBLE);
//        devicesLayout.setAlpha(Float.parseFloat("0.2"));
    }

    private void showData() {
//        loadingProgressBar.setVisibility(View.INVISIBLE);
//        devicesLayout.setAlpha(Float.parseFloat("1.0"));
    }
}
