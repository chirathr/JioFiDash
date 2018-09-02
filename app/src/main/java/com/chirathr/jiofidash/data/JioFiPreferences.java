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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JioFiPreferences {

    private static String TAG = JioFiPreferences.class.getSimpleName();

    private static JioFiPreferences mInstance;

    public static final String USERNAME_STRING_ID = "username";
    public static final String PASSWORD_STRING_ID = "password";

    public String username;
    public String password;

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    // Devices

    public static final String DEVICE_6 = "JMR815";
    public static final String DEVICE_OTHER = "other";

    public static final int LOGIN_TIMEOUT = 80;
    public static final int WPS_TIMEOUT = 120;

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
        // Return false if last login failed
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean(context.getString(R.string.login_success), false)) {
            return false;
        }
        if (username != null && password != null) {
            return true;
        } else {
            if (sharedPref.contains(context.getString(R.string.saved_user_name_key))
                    && sharedPref.contains(context.getString(R.string.saved_password_key))) {
                return true;
            }
        }
        return false;
    }

    public void setLoginState(Context context, boolean state) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.login_success), state);
        editor.apply();
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

        if (deviceId == NetworkUtils.DEVICE_JIOFI_6_ID) {
            editor.putInt(context.getString(R.string.saved_device_key), NetworkUtils.DEVICE_JIOFI_6_ID);
            editor.apply();
            currentDeviceId = NetworkUtils.DEVICE_JIOFI_6_ID;
        } else if (deviceId == NetworkUtils.DEVICE_NOT_SET_ID) {
            // For now show the onBoarding screen
            editor.putInt(context.getString(R.string.saved_device_key), NetworkUtils.DEVICE_NOT_SET_ID);
            editor.apply();
            currentDeviceId = NetworkUtils.DEVICE_NOT_SET_ID;
        }

    }

    public void loadDeviceId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);

        currentDeviceId = sharedPref.getInt(context.getString(R.string.saved_device_key), NetworkUtils.DEVICE_NOT_SET_ID);
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

    public void saveWPSTime(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String dateTime = dateTimeFormat.format(new Date());

        editor.putString(context.getString(R.string.wps_time), dateTime);
        editor.apply();
    }

    public boolean isWPSEnabled(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);

        String dateTimeString = sharedPref.getString(context.getString(R.string.wps_time), null);

        if (dateTimeString == null) {
            return false;
        }

        Date dateTime = null;

        try {
            dateTime = dateTimeFormat.parse(dateTimeString);
        } catch (ParseException e) {
            Log.v(TAG, "Date time parse error: " + e.getMessage());
        }

        if (dateTime == null) {
            return false;
        }

        if (getTimeDifferenceInSeconds(dateTime, new Date()) <= WPS_TIMEOUT) {
            return true;
        }
        return false;
    }

    public long getTimeDifferenceInSeconds(Date date1, Date date2) {
        long diff = date2.getTime() - date1.getTime();

        Log.v(TAG, Math.round(TimeUnit.MILLISECONDS.toSeconds(diff)) + "");

        return TimeUnit.MILLISECONDS.toSeconds(diff);
    }

    public void saveLoginCookie(Context context, String cookieString) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String dateTime = dateTimeFormat.format(new Date());

        editor.putString(context.getString(R.string.cookie_string), cookieString);
        editor.putString(context.getString(R.string.login_time), dateTime);
        editor.apply();
    }

    public String loadCookieString(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        String dateTimeString = sharedPref.getString(context.getString(R.string.login_time), null);

        if (dateTimeString == null) { return null; }

        Date dateTime = null;

        try {
            dateTime = dateTimeFormat.parse(dateTimeString);
        } catch (ParseException e) {
            Log.v(TAG, "Date time parse error: " + e.getMessage());
        }

        if (dateTime == null) { return null; }

        if (getTimeDifferenceInSeconds(dateTime, new Date()) <= LOGIN_TIMEOUT) {
            return sharedPref.getString(context.getString(R.string.cookie_string), null);
        }
        return null;
    }

    public void clearSavedCookie(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(R.string.login_time), null);
        editor.apply();
    }

    // Make sure only one notification at 100,  20, 10, 5 and 2%
    public void logBatteryNotificationPercentage(Context context, int batteryPercentage) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(context.getString(R.string.battery_notification_percent), batteryPercentage);
        editor.apply();
    }

    public int getLastBatteryNotificationPercentage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.data_preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getInt(context.getString(R.string.battery_notification_percent), -1);
    }

    private static final String BATTERY_DISCHARGING = "Discharging";
    private static final String BATTERY_CHARGING = "Charging";

    public boolean canShowNotification(Context context, int currentBatteryPercentage, String batteryStatus) {
        int lastBatteryPercentage = getLastBatteryNotificationPercentage(context);
        logBatteryNotificationPercentage(context, currentBatteryPercentage);

        if (currentBatteryPercentage < 100 && currentBatteryPercentage > 20) {
            return false;
        }

        if (lastBatteryPercentage == -1) { return true; }

        // If battery is charging don't show low battery notifications
        if (currentBatteryPercentage < 100 && batteryStatus.equals(BATTERY_CHARGING)) { return false; }

        if (lastBatteryPercentage < 100 && currentBatteryPercentage == 100) {
            // 100%
            return true;
        } else if (lastBatteryPercentage > 20 && currentBatteryPercentage <= 20 && currentBatteryPercentage > 10) {
            // 20%
            return true;
        } else if (lastBatteryPercentage > 10 && currentBatteryPercentage <= 10 && currentBatteryPercentage > 5) {
            // 10%
            return true;
        } else if (lastBatteryPercentage > 5 && currentBatteryPercentage <= 5 && currentBatteryPercentage > 2) {
            // 5%
            return true;
        } else if (lastBatteryPercentage > 2 && currentBatteryPercentage <= 2 && currentBatteryPercentage >= 0) {
            // 2% critical
            return true;
        } else {
            // Otherwise don't show
            return false;
        }

    }
}
