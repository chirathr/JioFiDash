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

public class DataSpeedCardFragment extends Fragment {

    private static final String TAG = DataSpeedCardFragment.class.getSimpleName();

    private static final int DELAY = 1000;
    private static final int START_DELAY = 1500;

    private TextView uploadSpeedTextView;
    private TextView uploadSpeedMaxTextView;
    private TextView downloadSpeedTextView;
    private TextView downloadSpeedMaxTextView;
    private ProgressBar loadingProgressBar;
    private ConstraintLayout dataSpeedConstrainLayout;

    private Handler handler;

    private boolean updateUI = true;

    private Runnable dataSpeedUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (updateUI) {
                loadDataSpeed(getContext());
                handler.postDelayed(dataSpeedUpdateRunnable, DELAY);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        handler = new Handler();

        View dataSpeedCardView = inflater.inflate(R.layout.data_speed_card, container, false);

        loadingProgressBar = (ProgressBar) dataSpeedCardView.findViewById(R.id.date_speed_loading_progress_bar);
        loadingProgressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY);
        dataSpeedConstrainLayout = (ConstraintLayout) dataSpeedCardView.findViewById(R.id.data_speed_layout);
        showLoading();

        uploadSpeedTextView = (TextView)  dataSpeedCardView.findViewById(R.id.tv_upload_speed);
        uploadSpeedMaxTextView = (TextView) dataSpeedCardView.findViewById(R.id.tv_upload_speed_max);
        downloadSpeedTextView = (TextView) dataSpeedCardView.findViewById(R.id.tv_download_speed);
        downloadSpeedMaxTextView = (TextView) dataSpeedCardView.findViewById(R.id.tv_download_speed_max);

        handler.postDelayed(dataSpeedUpdateRunnable, START_DELAY);

        return dataSpeedCardView;
    }

    @Override
    public void onResume() {
        if (!updateUI) {
            updateUI = true;
            handler.post(dataSpeedUpdateRunnable);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        updateUI = false;
        super.onPause();
    }

    private void loadDataSpeed(Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.PERFORMANCE_INFO_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    uploadSpeedTextView.setText(response.getString(JioFiData.UPLOAD_RATE));
                    downloadSpeedTextView.setText(response.getString(JioFiData.DOWNLOAD_RATE));
                    uploadSpeedMaxTextView.setText(response.getString(JioFiData.UPLOAD_RATE_MAX));
                    downloadSpeedMaxTextView.setText(response.getString(JioFiData.DOWNLOAD_RATE_MAX));

                    showDataSpeed();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Error");

                showLoading();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        dataSpeedConstrainLayout.setAlpha(Float.parseFloat("0.2"));
    }

    private void showDataSpeed() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        dataSpeedConstrainLayout.setAlpha(Float.parseFloat("1.0"));
    }
}
