package com.chirathr.jiofidash.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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

    private static final int batteryTimeAt100 = 370;
    private static final String batteryTimeFormatString = "%d hours %d mins";

    private static final String TAG = JioFiData.class.getSimpleName();

    public static int getBatteryLevel(String text) {
        try {
            return Integer.parseInt(text.split(" ")[0]);
        } catch (Exception e) {
            Log.v(TAG, "Error converting to battery % : " + e.getMessage());
            return 0;
        }
    }

    public static String calculateRemainingTimeString(Context context, int batteryPercentage) {

        int batteryRemaining;
        // TODO Calculate battery time left based on rate of change

        if (batteryPercentage == 0) {
            return String.format(batteryTimeFormatString, 0, 0);
        }

        batteryRemaining = (int) Math.round(batteryTimeAt100 * (batteryPercentage / 100.0));

        int hours = batteryRemaining / 60;
        int minutes = batteryRemaining % 60;

        return String.format(batteryTimeFormatString, hours, minutes);
    }

}
