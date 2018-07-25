package com.chirathr.jiofidash.data;

import android.content.Context;
import android.util.Log;

import com.chirathr.jiofidash.MainActivity;
import com.chirathr.jiofidash.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class JioFiData {

    private static final String BATTERY_LEVEL = "batterylevel";
    private static final String BATTERY_STATUS = "batterystatus";

    public double batteryLevel;
    public String batteryStatus;

    private final String TAG = JioFiData.class.getSimpleName();

    public void setDeviceInfo(String deviceInfoJsonString) {

        // deviceInfoJsonString = "{ batterylevel:'0 %', batterystatus:'No Battery', curr_time:'Wed 25 Jul 2018 18:53:27'}";
        JSONObject deviceInfoJson;
        try {
            deviceInfoJson = new JSONObject(deviceInfoJsonString);
            batteryLevel = Double.parseDouble(deviceInfoJson.getString(BATTERY_LEVEL).split(" ")[0]);
            batteryStatus = deviceInfoJson.getString(BATTERY_STATUS);

        } catch (JSONException e) {
            Log.v(TAG, "Device data Json parsing error: " + e.getMessage());
            return;
        }
    }

    public void loadDeviceInfo(Context context) {
        String jsonDeviceDataString = NetworkUtils.getJsonData(
                context, NetworkUtils.DEVICE_INFO_ID, NetworkUtils.DEVICE_6_ID);
        setDeviceInfo(jsonDeviceDataString);
    }
}
