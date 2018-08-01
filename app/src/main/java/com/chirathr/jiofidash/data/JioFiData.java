package com.chirathr.jiofidash.data;

import android.content.Context;
import android.util.Log;

import com.chirathr.jiofidash.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JioFiData {

    // Lte info
    public static final String LTE_TIME_STRING = "time_str";
    public static final String LTE_STATUS = "status";
    public static final String LTE_BAND = "opband";
    public static final String LTE_BANDWIDTH = "bandwidth";
    public static final String LTE_PHYSICAL_CELL_ID = "pcellID";
    public static final String LTE_RSRP_ID = "rsrp";

    // Performance
    public static final String UPLOAD_RATE = "txRate";
    public static final String UPLOAD_RATE_MAX = "txmax";
    public static final String DOWNLOAD_RATE = "rxRate";
    public static final String DOWNLOAD_RATE_MAX = "rxmax";

    // Lan information (connected users)
    public static final String USER_COUNT = "act_cnt";
    public static final String USER_LIST = "userlistinfo";

    // Wan information (total data used)
    public static final String TOTAL_UPLOAD = "duration_ul";
    public static final String TOTAL_DOWNLOAD = "duration_dl";

    // Device Info
    public static final String BATTERY_LEVEL = "batterylevel";
    public static final String BATTERY_STATUS = "batterystatus";

    private final String TAG = JioFiData.class.getSimpleName();

}
