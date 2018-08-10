package com.chirathr.jiofidash.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.utils.NetworkUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class JioFiPreferences {

    private static String TAG = JioFiPreferences.class.getSimpleName();

    private static JioFiPreferences mInstance;

    public static final String USERNAME_STRING_ID = "username";
    public static final String PASSWORD_STRING_ID = "password";

    public String username;
    public String password;

    // Devices

    public static final String DEVICE_6 = "JMR815";
    public static final String DEVICE_OTHER = "other";

    public static int currentDeviceId;

    public static String ipAddressString = null;

    public Map<String, String> getUserLoginData(Context context) {
        Map<String,String> params = new HashMap<String, String>();

        if (username == null || password == null) {
            boolean didLoad = loadUsernameAndPassword(context);
            if (!didLoad)
                return null;
        }
        params.put(USERNAME_STRING_ID, username);
        params.put(PASSWORD_STRING_ID, password);

        return params;
    }

    public static synchronized JioFiPreferences getInstance() {
        if (mInstance == null)
            mInstance = new JioFiPreferences();
        return mInstance;
    }

    public void setUsernameAndPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void saveUsernameAndPassword(Context context) {

        if (username == null || password == null) {
            return;
        }
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(R.string.saved_user_name_key), username);
        editor.putString(context.getString(R.string.saved_password_key), password);

        editor.apply();
    }

    public boolean isLoginDataAvailable(Context context) {
        if (username != null && password != null) {
            return true;
        } else {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
            if (sharedPref.contains(context.getString(R.string.saved_user_name_key))
                    && sharedPref.contains(context.getString(R.string.saved_password_key))) {
                return true;
            }
        }
        return false;
    }

    public boolean loadUsernameAndPassword(Context context) {

        if (username != null && password != null) {
            return true;
        }

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);

        if (sharedPref.contains(context.getString(R.string.saved_user_name_key))
                && sharedPref.contains(context.getString(R.string.saved_password_key))) {

            username = sharedPref.getString(context.getString(R.string.saved_user_name_key), null);
            password = sharedPref.getString(context.getString(R.string.saved_password_key), null);

            return true;
        }

        return false;
    }

    public void setDevice(Context context, int deviceId) {

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (deviceId == NetworkUtils.DEVICE_6_ID) {
            editor.putInt(context.getString(R.string.saved_device_key), NetworkUtils.DEVICE_6_ID);
            editor.apply();
            currentDeviceId = NetworkUtils.DEVICE_6_ID;
        }

    }

    public void loadDeviceId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);

        currentDeviceId = sharedPref.getInt(context.getString(R.string.saved_device_key), -1);
    }

    public boolean loadWiFiIpAddress(Context context) {
        Log.v("macAddress", "Loading mac");
        if (NetworkUtils.isOnline(context)) {
            WifiManager manager = (WifiManager) context
                    .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int ipAddress = manager.getConnectionInfo().getIpAddress();

            // Convert little-endian to big-endian if needed
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                ipAddress = Integer.reverseBytes(ipAddress);
            }

            byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

            try {
                ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
            } catch (UnknownHostException ex) {
                Log.e(TAG, "Unable to get host address.");
                ipAddressString = null;
            }
            Log.v(TAG, ipAddressString);
        }
        if (ipAddressString == null)
            return false;
        else
            return true;
    }
}
