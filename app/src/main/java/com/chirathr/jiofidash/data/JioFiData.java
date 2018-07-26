package com.chirathr.jiofidash.data;

import android.content.Context;
import android.util.Log;

import com.chirathr.jiofidash.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class JioFiData {

    // Lte info
    private static final String LTE_TIME_STRING = "time_str";
    private static final String LTE_STATUS = "status";
    private static final String LTE_BAND = "opband";
    private static final String LTE_BANDWIDTH = "bandwidth";
    private static final String LTE_PHYSICAL_CELL_ID = "pcellID";

    public String lteTimeString;
    public String lteStatus;
    public int lteBand;
    public String lteBandwidth;
    public int lteCellId;

    // Performance
    private static final String UPLOAD_RATE = "txRate";
    private static final String UPLOAD_RATE_MAX = "txmax";
    private static final String DOWNLOAD_RATE = "rxRate";
    private static final String DOWNLOAD_RATE_MAX = "rxmax";

    public String uploadRateString;
    public String uploadRateMaxString;
    public String downloadRateString;
    public String downloadRateMaxString;

    // Lan information (connected users)



    // Wan information (total data used)

    // Device Info
    private static final String BATTERY_LEVEL = "batterylevel";
    private static final String BATTERY_STATUS = "batterystatus";

    public double batteryLevel;
    public String batteryStatus;

    private final String TAG = JioFiData.class.getSimpleName();

    public void setDeviceInfo(String deviceInfoJsonString) {
        JSONObject deviceInfoJson;
        try {
            deviceInfoJson = new JSONObject(deviceInfoJsonString);
            batteryLevel = Double.parseDouble(deviceInfoJson.getString(BATTERY_LEVEL).split(" ")[0]);
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
            lteBand = Integer.parseInt(lteInfoJson.getString(LTE_BAND));
            lteStatus = lteInfoJson.getString(LTE_STATUS);
            lteBandwidth = lteInfoJson.getString(LTE_BANDWIDTH);
            lteCellId = Integer.parseInt(lteInfoJson.getString(LTE_PHYSICAL_CELL_ID));
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

}
