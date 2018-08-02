package com.chirathr.jiofidash.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class LteCardFragment extends Fragment {

    private static final int DELAY = 1000;
    private static final int START_DELAY = 1000;

    private static final String TAG = LteCardFragment.class.getSimpleName();

    private static final int LTE_HIGH_SPEED = 40;
    private static final int LTE_MEDIUM_SPEED = 3;
    private static final int LTE_LOW_SPEED = 5;

    private static final String LTE_HIGH_SPEED_STRING = "High speed";
    private static final String LTE_MEDIUM_SPEED_STRING = "Medium speed";
    private static final String LTE_LOW_SPEED_STRING = "Low speed";
    private static final String LTE_NO_NETWORK_STRING = "No network";

    private static final int LTE_RSRP_GOOD = -90;
    private static final int LTE_RSRP_MEDIUM = -106;
    private static final int LTE_RSRP_FAIR = -114;
    private static final int LTE_RSRP_POOR = -120;

    private static final int LTE_NETWORK_ICON_4_BARS = R.drawable.ic_round_signal_cellular_4_bar_24px;
    private static final int LTE_NETWORK_ICON_3_BARS = R.drawable.ic_round_signal_cellular_3_bar_24px;
    private static final int LTE_NETWORK_ICON_2_BARS = R.drawable.ic_round_signal_cellular_2_bar_24px;
    private static final int LTE_NETWORK_ICON_1_BARS = R.drawable.ic_round_signal_cellular_1_bar_24px;
    private static final int LTE_NETWORK_ICON_0_BARS = R.drawable.ic_round_signal_cellular_0_bar_24px;
    private static final int LTE_NETWORK_ICON_NO_SIGNAL = R.drawable.ic_round_signal_cellular_off_24px;

    private ImageView lteNetworkIcon;
    private TextView lteSpeedTextView;
    private TextView lteBandTextView;
    private TextView lteBandwidthTextView;
    private TextView lteCellIdTextView;

    private ProgressBar loadingProgressBar;
    private ConstraintLayout layout;

    private boolean updateUI = true;
    private Handler handler;

    private Runnable lteInfoRunnable = new Runnable() {
        @Override
        public void run() {
            loadLteData(getContext());
            if (updateUI)
                handler.postDelayed(lteInfoRunnable, DELAY);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        handler = new Handler();

        View view = inflater.inflate(R.layout.lte_info_card, container, false);

        loadingProgressBar = (ProgressBar) view.findViewById(R.id.lte_info_loading_progress_bar);
        loadingProgressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY);
        layout = (ConstraintLayout) view.findViewById(R.id.lte_info_layout);

        showLoading();

        lteNetworkIcon = (ImageView) view.findViewById(R.id.lte_network_icon);
        lteSpeedTextView = (TextView) view.findViewById(R.id.tv_lte_speed_text);
        lteBandTextView = (TextView) view.findViewById(R.id.tv_lte_band);
        lteBandwidthTextView = (TextView) view.findViewById(R.id.tv_lte_bandwidth);
        lteCellIdTextView = (TextView) view.findViewById(R.id.tv_lte_cell_id);

        handler.postDelayed(lteInfoRunnable, START_DELAY);

        return view;
    }

    @Override
    public void onResume() {
        if (!updateUI) {
            updateUI = true;
            handler.post(lteInfoRunnable);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        updateUI = false;
        super.onPause();
    }

    private void loadLteData(Context context) {
        String urlString = NetworkUtils.getUrlString(NetworkUtils.LTE_INFO_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    lteBandTextView.setText(response.getString(JioFiData.LTE_BAND));
                    lteBandwidthTextView.setText(response.getString(JioFiData.LTE_BANDWIDTH));
                    lteCellIdTextView.setText(response.getString(JioFiData.LTE_PHYSICAL_CELL_ID));

                    setNetworkIconAndSpeedText(
                            response.getString(JioFiData.LTE_BAND),
                            response.getString(JioFiData.LTE_RSRP_ID));

                    showLteData();
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

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        layout.setAlpha(Float.parseFloat("0.2"));
    }

    private void showLteData() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        layout.setAlpha(Float.parseFloat("1.0"));
    }

    private void setNetworkIconAndSpeedText(String lteBandString, String rsrpString) {
        String text;
        int icon, lteBand, rsrp, textColor;

        try {
            lteBand = Integer.parseInt(lteBandString);
            rsrp = Integer.parseInt(rsrpString.split(" ")[0]);

            switch (lteBand) {
                case LTE_HIGH_SPEED: {
                    text = LTE_HIGH_SPEED_STRING;
                    textColor = R.color.colorPrimaryGreenLight;
                    break;
                }
                case LTE_MEDIUM_SPEED: {
                    text = LTE_MEDIUM_SPEED_STRING;
                    textColor = R.color.colorAccentBlueLight;
                    break;
                }
                case LTE_LOW_SPEED: {
                    text = LTE_LOW_SPEED_STRING;
                    textColor = R.color.colorPrimaryYellowLight;
                    break;
                }
                default:
                    text = LTE_NO_NETWORK_STRING;
                    textColor = R.color.colorPrimaryRedLight;
            }

            if (rsrp < LTE_RSRP_POOR) {
                icon = LTE_NETWORK_ICON_0_BARS;
            }
            else if (rsrp < LTE_RSRP_FAIR) {
                icon = LTE_NETWORK_ICON_1_BARS;
            }
            else if (rsrp < LTE_RSRP_MEDIUM) {
                icon = LTE_NETWORK_ICON_2_BARS;
            }
            else if (rsrp < LTE_RSRP_GOOD) {
                icon = LTE_NETWORK_ICON_3_BARS;
            } else {
                icon = LTE_NETWORK_ICON_4_BARS;
            }

        }
        catch (Exception e) {
            text = LTE_NO_NETWORK_STRING;
            icon = LTE_NETWORK_ICON_NO_SIGNAL;
            textColor = R.color.colorPrimaryRedLight;

            Log.v(TAG, "No signal: " + e.getMessage());
        }

        lteSpeedTextView.setText(text);
        try {
            lteSpeedTextView.setTextColor(getResources().getColor(textColor));
            lteNetworkIcon.setImageDrawable(getResources().getDrawable(icon));
        } catch (IllegalStateException e) {
            Log.v(TAG, "IllegalStateException: " + e.getMessage());
        }
    }
}
