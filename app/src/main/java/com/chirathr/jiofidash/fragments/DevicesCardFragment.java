package com.chirathr.jiofidash.fragments;

import android.accounts.NetworkErrorException;
import android.content.Context;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.chirathr.jiofidash.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class DevicesCardFragment extends Fragment {

    public static final String TAG = DevicesCardFragment.class.getSimpleName();

    private static final int DELAY = 5000;
    private static final int START_DELAY = 1000;

    private TextView devicesCount;
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

        View view = inflater.inflate(R.layout.devices_card, container, false);

        loadingProgressBar = view.findViewById(R.id.devices_loading_progress_bar);
        devicesLayout = view.findViewById(R.id.devices_layout);
        showLoading();
        devicesCount = view.findViewById(R.id.tv_devices_count);

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

        String urlString = NetworkUtils.getUrlString(NetworkUtils.LAN_INFO_ID);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    devicesCount.setText(response.getString(JioFiData.USER_COUNT));
                    showDataUsage();
                } catch (JSONException e) {
                    Log.v(TAG, "JSONException: " + e.getMessage());
                    showLoading();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Volley error: " + error.getMessage());
                showLoading();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        devicesLayout.setAlpha(Float.parseFloat("0.2"));
    }

    private void showDataUsage() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        devicesLayout.setAlpha(Float.parseFloat("1.0"));
    }
}
