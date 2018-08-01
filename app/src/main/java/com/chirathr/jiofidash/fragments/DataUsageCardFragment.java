package com.chirathr.jiofidash.fragments;

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

public class DataUsageCardFragment extends Fragment {

    private static final int DELAY = 5000;
    private static final int START_DELAY = 1000;

    private static final String TAG = DataUsageCardFragment.class.getSimpleName();

    private TextView downloadData;
    private TextView uploadData;
    private TextView upTime;
    private ProgressBar loadingProgressBar;
    private ConstraintLayout dataUsageLayout;

    private boolean updateUI = true;

    private Handler handler;

    private Runnable dataUsageUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            loadUsageData(getContext());
            if (updateUI) {
                handler.postDelayed(dataUsageUpdateRunnable, DELAY);
            } else {
                Log.v(TAG, "Stopped");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        handler = new Handler();

        View dataUsageView = inflater.inflate(R.layout.data_usage_card, container, false);

        loadingProgressBar = (ProgressBar) dataUsageView.findViewById(R.id.date_usage_loading_progress_bar);
        dataUsageLayout = (ConstraintLayout) dataUsageView.findViewById(R.id.data_usage_layout);

        showLoading();

        uploadData = (TextView) dataUsageView.findViewById(R.id.tv_total_upload_data);
        downloadData = (TextView) dataUsageView.findViewById(R.id.tv_total_download_data);

        handler.postDelayed(dataUsageUpdateRunnable, START_DELAY);

        return dataUsageView;
    }

    @Override
    public void onResume() {
        if (!updateUI) {
            updateUI = true;
            handler.post(dataUsageUpdateRunnable);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        updateUI = false;
        super.onPause();
    }

    private void loadUsageData(Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.WAN_INFO_ID, NetworkUtils.DEVICE_6_ID);

        JsonObjectRequest dataUsageJsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    downloadData.setText(response.getString(JioFiData.TOTAL_DOWNLOAD));
                    uploadData.setText(response.getString(JioFiData.TOTAL_UPLOAD));
                    Log.v(TAG, "Running");
                    showDataUsage();
                } catch (JSONException e) {
                    Log.v(TAG, "JSONException: " + e.getMessage());
                    showLoading();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Error: " + error.getMessage());
                showLoading();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(dataUsageJsonObjectRequest);
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        dataUsageLayout.setAlpha(Float.parseFloat("0.2"));
    }

    private void showDataUsage() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        dataUsageLayout.setAlpha(Float.parseFloat("1.0"));
    }
}
