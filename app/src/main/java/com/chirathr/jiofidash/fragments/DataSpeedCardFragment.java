package com.chirathr.jiofidash.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;

public class DataSpeedCardFragment extends Fragment {

    private TextView uploadSpeedTextView;
    private TextView uploadSpeedMaxTextView;
    private TextView downloadSpeedTextView;
    private TextView downloadSpeedMaxTextView;

    private static final String TAG = DataSpeedCardFragment.class.getSimpleName();

    private Handler handler;

    private Runnable dataSpeedUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            loadDataSpeed(getContext());

            handler.postDelayed(dataSpeedUpdateRunnable, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        handler = new Handler();

        View dataSpeedCardView = inflater.inflate(R.layout.data_speed_card, container, false);

        uploadSpeedTextView = (TextView)  container.findViewById(R.id.tv_upload_speed);
        uploadSpeedMaxTextView = (TextView) container.findViewById(R.id.tv_upload_speed_max);
        downloadSpeedTextView = (TextView) container.findViewById(R.id.tv_download_speed);
        downloadSpeedMaxTextView = (TextView) container.findViewById(R.id.tv_download_speed_max);

        handler.post(dataSpeedUpdateRunnable);

        return dataSpeedCardView;
    }


    public void loadDataSpeed(Context context) {
        String urlString = NetworkUtils.getUrlString(
                NetworkUtils.PERFORMANCE_INFO_ID,
                NetworkUtils.DEVICE_6_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    uploadSpeedTextView.setText(response.getString(JioFiData.UPLOAD_RATE));
                    downloadSpeedTextView.setText(response.getString(JioFiData.DOWNLOAD_RATE));
                    uploadSpeedMaxTextView.setText(response.getString(JioFiData.UPLOAD_RATE_MAX));
                    downloadSpeedMaxTextView.setText(response.getString(JioFiData.DOWNLOAD_RATE_MAX));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, error.getMessage());
                Log.v(TAG, error.toString());
            }
        });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
