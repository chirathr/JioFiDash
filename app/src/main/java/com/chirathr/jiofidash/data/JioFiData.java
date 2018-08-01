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

    public void setDeviceInfo(String deviceInfoJsonString) {
        JSONObject deviceInfoJson;
        try {
            deviceInfoJson = new JSONObject(deviceInfoJsonString);
            batteryLevel = toInt(deviceInfoJson.getString(BATTERY_LEVEL).split(" ")[0]);
            batteryStatus = deviceInfoJson.getString(BATTERY_STATUS);

        } catch (JSONException e) {
            Log.v(TAG, "Device data Json parsing error: " + e.getMessage());
        }
    }

    public void loadDeviceInfo(Context context) {
        String jsonDeviceDataString = NetworkUtils.getJsonData(
                context, NetworkUtils.DEVICE_INFO_ID, NetworkUtils.DEVICE_6_ID);
        if (jsonDeviceDataString != null)
            setDeviceInfo(jsonDeviceDataString);
    }

    public void setLteInfo(String lteInfoJsonString) {
        JSONObject lteInfoJson;

        try {
            lteInfoJson = new JSONObject(lteInfoJsonString);
            lteTimeString = lteInfoJson.getString(LTE_TIME_STRING);
            lteBand = toInt(lteInfoJson.getString(LTE_BAND));
            lteStatus = lteInfoJson.getString(LTE_STATUS);
            lteBandwidth = lteInfoJson.getString(LTE_BANDWIDTH);
            lteCellId = lteInfoJson.getString(LTE_PHYSICAL_CELL_ID);
        } catch (JSONException e) {
            Log.v(TAG, "Lte data Json parsing error: " + e.getMessage());
        }
    }

    public void loadLteInfo(Context context) {
        String jsonLteDataString = NetworkUtils.getJsonData(
                context, NetworkUtils.LTE_INFO_ID, NetworkUtils.DEVICE_6_ID);
        if (jsonLteDataString != null)
            setLteInfo(jsonLteDataString);
    }

    public void setPerformanceInfo(String performanceInfoString) {
        JSONObject performanceInfoJson;

        try {
            performanceInfoJson = new JSONObject(performanceInfoString);

            uploadRateString = performanceInfoJson.getString(UPLOAD_RATE);
            uploadRateMaxString = performanceInfoJson.getString(UPLOAD_RATE_MAX);
            downloadRateString = performanceInfoJson.getString(DOWNLOAD_RATE);
            downloadRateMaxString = performanceInfoJson.getString(DOWNLOAD_RATE_MAX);

        } catch (JSONException e) {
            Log.v(TAG, "Performance data Json parsing error: " + e.getMessage());
        }
    }

    public void loadPerformanceInfo(Context context) {
        String jsonPerformanceDataString = NetworkUtils.getJsonData(
                context, NetworkUtils.PERFORMANCE_INFO_ID, NetworkUtils.DEVICE_6_ID);
        if (jsonPerformanceDataString != null)
            setPerformanceInfo(jsonPerformanceDataString);
    }

    public void setWanInfo(String wanInfoJsonString) {
        JSONObject wanInfoJson;

        try {
            wanInfoJson = new JSONObject(wanInfoJsonString);

            totalUploadString = wanInfoJson.getString(TOTAL_UPLOAD);
            totalDownloadString = wanInfoJson.getString(TOTAL_DOWNLOAD);

        } catch (JSONException e) {
            Log.v(TAG, "Wan data Json parsing error: " + e.getMessage());
        }
    }

    public void loadWanInfo(Context context) {
        String jsonWanDataString = NetworkUtils.getJsonData(
                context, NetworkUtils.WAN_INFO_ID, NetworkUtils.DEVICE_6_ID);
        if (jsonWanDataString != null)
            setWanInfo(jsonWanDataString);
    }

    public void setLanInfo(String lanInfoJsonString) {
        JSONObject lanInfoJson;
        try {
            lanInfoJson = new JSONObject(lanInfoJsonString);

            userCount = toInt(lanInfoJson.getString(USER_COUNT));

            String userInfoListString = lanInfoJson.getString(USER_LIST);
            String[] userInfoList = userInfoListString.split(";");

            userNameList = new ArrayList<>();
            userConnectedList = new ArrayList<>();
            userMacList = new ArrayList<>();

            for (int i = 0; i < userInfoList.length; ++i) {
                String[] userInfo = userInfoList[i].split(",");
                if (!userInfo[0].equals("Static")) {
                    userNameList.add(userInfo[0]);
                    userMacList.add(userInfo[1]);
                    userConnectedList.add(userInfo[4].equals("Connected"));
                }
            }

        } catch (JSONException e) {
            Log.v(TAG, "Wan data Json parsing error: " + e.getMessage());
        }
    }

    public void loadLanInfo(Context context) {
        String lanInfoJsonString = NetworkUtils.getJsonData(
                context, NetworkUtils.LAN_INFO_ID, NetworkUtils.DEVICE_6_ID);
        if (lanInfoJsonString != null)
            setLanInfo(lanInfoJsonString);
    }

}
