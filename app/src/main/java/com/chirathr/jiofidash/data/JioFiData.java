package com.chirathr.jiofidash.data;

import android.content.Context;
import android.util.Log;

import com.chirathr.jiofidash.MainActivity;
import com.chirathr.jiofidash.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class JioFiData {

    // Lte info
    private static final String LTE_STATUS = "status";
    private static final String LTE_BAND = "opband";
    private static final String LTE_BANDWIDTH = "bandwidth";
    private static final String LTE_PHYSICAL_CELL_ID = "pcellID";

    public String lteStatus;
    public int lteBand;
    public String lteBandwidth;
    public int lteCellId;

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
        setDeviceInfo(jsonDeviceDataString);
    }

    public void setLteInfo(String lteInfoJsonString) {
        JSONObject lteInfoJson;

        try {
            lteInfoJson = new JSONObject(lteInfoJsonString);

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
        setLteInfo(jsonLteDataString);
    }

}
