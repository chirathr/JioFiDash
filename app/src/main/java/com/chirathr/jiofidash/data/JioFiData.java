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

    public String lteTimeString;
    public String lteStatus;
    public int lteBand;
    public String lteBandwidth;
    public String lteCellId;

    // Performance
    public static final String UPLOAD_RATE = "txRate";
    public static final String UPLOAD_RATE_MAX = "txmax";
    public static final String DOWNLOAD_RATE = "rxRate";
    public static final String DOWNLOAD_RATE_MAX = "rxmax";

    public String uploadRateString;
    public String uploadRateMaxString;
    public String downloadRateString;
    public String downloadRateMaxString;

    // Lan information (connected users)
    public static final String USER_COUNT = "act_cnt";
    public static final String USER_LIST = "userlistinfo";

    public int userCount;
    public List<String> userNameList;
    public List<Boolean> userConnectedList;
    public List<String> userMacList;

    // Wan information (total data used)
    public static final String TOTAL_UPLOAD = "duration_ul";
    public static final String TOTAL_DOWNLOAD = "duration_dl";

    public String totalUploadString;
    public String totalDownloadString;

    // Device Info
    public static final String BATTERY_LEVEL = "batterylevel";
    public static final String BATTERY_STATUS = "batterystatus";

    public int batteryLevel;
    public String batteryStatus;

    private final String TAG = JioFiData.class.getSimpleName();

    public static int toInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double toDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

}
