package com.chirathr.jiofidash.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.WiFiSettings;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class WiFiCardFragment extends Fragment {

    public static final String TAG = WiFiCardFragment.class.getSimpleName();

    private static final int DELAY = 5000;
    private static final int START_DELAY = 1000;

    private TextView devicesCount;
    private TextView ssidNameTextView;
    private ProgressBar loadingProgressBar;
    private ConstraintLayout devicesLayout;

    private boolean updateUI = true;

    private Handler handler;

    private Runnable devicesUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            loadDevicesData(getContext());
            if (updateUI) {
                handler.postDelayed(devicesUpdateRunnable, DELAY);
            } else {
                Log.v(TAG, "Stopped");
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        handler = new Handler();

        View view = inflater.inflate(R.layout.wifi_card, container, false);

        loadingProgressBar = view.findViewById(R.id.wifi_progress_bar);
        loadingProgressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY);
        devicesLayout = view.findViewById(R.id.wifi_layout);
        showLoading();
        devicesCount = view.findViewById(R.id.tv_wifi_caption);
        ssidNameTextView = view.findViewById(R.id.tv_wifi_ssid);

        view.findViewById(R.id.wifi_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WiFiSettings.class);
                startActivity(intent);
            }
        });

        handler.postDelayed(devicesUpdateRunnable, START_DELAY);

        return view;
    }

    @Override
    public void onResume() {
        if (!updateUI) {
            updateUI = true;
            handler.post(devicesUpdateRunnable);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        updateUI = false;
        super.onPause();
    }


    public void loadDevicesData(Context context) {

        String urlString = NetworkUtils.getUrlString(NetworkUtils.LAN_INFO_PAGE_ID);

        StringRequest lanInfoRequest = new StringRequest(Request.Method.GET,
                urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document lanInfoDocument = Jsoup.parse(response);
                ssidNameTextView.setText(lanInfoDocument.getElementById("ssid").text());

                String noOfClients = lanInfoDocument.getElementById("noOfClient").text();
                String devicesFormatText;
                if (noOfClients.equals("1")) {
                    devicesFormatText = "%s device";
                } else {
                    devicesFormatText = "%s devices";
                }
                devicesCount.setText(String.format(devicesFormatText, noOfClients));

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
        devicesLayout.setAlpha(Float.parseFloat("0.2"));
    }

    private void showData() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        devicesLayout.setAlpha(Float.parseFloat("1.0"));
    }
}
