package com.chirathr.jiofidash.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.chirathr.jiofidash.data.DeviceViewModel;
import com.chirathr.jiofidash.data.JioFiPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String DEFAULT_HOST = "http://jiofi.local.html";

    // JioFI 6
    public static final int DEVICE_6_ID = 6;
    public static final int DEVICE_OTHER_ID = 0;
    public static final int DEVICE_NOT_SET_ID = -1;
    private static final String[] DEVICE_6_URLS = new String[]{
            "/lte_ajax.cgi",
            "/lan_ajax.cgi",
            "/wan_info_ajax.cgi",
            "/Device_info_ajax.cgi",
            "/performance_ajax.cgi",
            "/login.cgi",
            "/Device_setting.cgi",
            "/Device_setting_sv.cgi",
            "/LAN_info.cgi",
            "/Security_Mode.cgi",
            "/Security_Mode_sv.cgi",
            "/MAC_Filter.cgi",
            "/MAC_Filter_ajax.cgi",
            "/WPS.cgi",
            "/wps_sv.cgi",
            "/top.cgi",
            "/logout_btn.cgi"
    };

    // Id to represent types of data from device urls
    public static final int LTE_INFO_ID = 0;
    public static final int LAN_INFO_ID = 1;
    public static final int WAN_INFO_ID = 2;
    public static final int DEVICE_INFO_ID = 3;
    public static final int PERFORMANCE_INFO_ID = 4;
    public static final int LOGIN_URL_ID = 5;
    public static final int URL_DEVICE_SETTINGS_GET_ID = 6;
    public static final int URL_DEVICE_SETTINGS_POST_ID = 7;
    public static final int LAN_INFO_PAGE_ID = 8;
    public static final int WIFI_SETTINGS_GET_ID = 9;
    public static final int WIFI_SETTINGS_POST_ID = 10;
    public static final int WIFI_MAC_GET_ID = 11;
    public static final int WIFI_MAC_POST_ID = 12;
    public static final int WPS_GET_ID = 13;
    public static final int WPS_POST_ID = 14;
    public static final int LOGOUT_PAGE_ID = 15;
    public static final int LOGOUT_GET_ID = 16;


    // Get device url based on type
    public static String[] getDeviceUrls(int deviceType) {
        if (deviceType == DEVICE_6_ID)    // JioFi 6
            return DEVICE_6_URLS;

        return null;
    }

    public static String getHostAddress() {
        return DEFAULT_HOST;
    }

    private static String CSRF_TOKEN_STRING_ID = "token";
    private static String FORM_TYPE_STRING_ID = "form_type";

    // Login
    public static String cookieString = null;
    public static boolean authenticationError = false;

    private static final String TOKEN_INPUT_CSS_SELECTOR = "input[name='token']";
    private static final String POWER_SAVING_TIME_INPUT_CSS_SELECTOR = "input[name='Saving_Time'] option[selected]";
    private static final String VALUE_ATTRIBUTE_KEY = "value";

    private static String LOGIN_USERNAME_STRING_ID = "identify";
    private static String LOGIN_PASSWORD_STRING_ID = "password";

    // Power saving settings
    private static String SAVING_TIME_STRING_ID = "Saving_Time";
    private static String SAVING_TIME_FORM_TYPE = "sleep_time";

    private static final String COOKIE_FORMAT_STRING = "ksession=%s";


    public static final int CONNECTION_TIMEOUT = 50000;


    public static String getUrlString(int urlType) {
        int deviceType = JioFiPreferences.currentDeviceId;
        String[] deviceUrls = getDeviceUrls(deviceType);
        String url = null;
        if (deviceUrls != null) {
            url = deviceUrls[urlType];
        }
        return getHostAddress() + url;
    }

    private static URL getURL(int urlType) {
        URL url = null;
        String urlString;

        int deviceType = JioFiPreferences.currentDeviceId;

        String[] deviceUrls = getDeviceUrls(deviceType);
        if (deviceUrls == null) return null;
        urlString = getHostAddress() + deviceUrls[urlType];

        try {
            url = new URL(urlString);

        } catch (MalformedURLException e) {
            Log.v(TAG, "Malformed string url used to create URL: " + e.getMessage());
            return null;
        }

        return url;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();

        if (!isWifiConn) return false;

        networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean jiofiAvailableCheck() {
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(getHostAddress());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(100);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK)
                return true;

        } catch (MalformedURLException e) {
            Log.v(TAG, "Malformed string url used to create URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.v(TAG, ": Connection error: " + e.getMessage());
            return false;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return false;
    }

    // Authenticated URLS

    public static String getCookieString() {
        if (cookieString != null)
            return String.format(COOKIE_FORMAT_STRING, cookieString);
        else {
            Log.v(TAG, "Cookie not set");
            return null;
        }
    }

    public static String getPostParams(Map<String, String> params) throws UnsupportedEncodingException {
        Uri.Builder builder = new Uri.Builder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().getEncodedQuery();
    }

    public static String readAll(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[10000];
        StringBuilder builder = new StringBuilder();
        int len = reader.read(buffer);
        while (len > 0) {
            builder.append(buffer, 0, len);
            len = reader.read(buffer);
        }
        return builder.toString();
    }

    public static String postRequest(URL url, Map<String, String> params, Map<String, String> authHeaders) {

        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        BufferedWriter writer = null;
        int responseCode;
        String response = null;

        try {
            Log.v(TAG, "POST: " + url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (authHeaders != null) {
                for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            outputStream = connection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            if (params != null) {
                writer.write(getPostParams(params));
            }

            writer.flush();
            writer.close();

            connection.connect();
            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = readAll(connection.getInputStream());
            }

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "(postRequest)UnsupportedEncodingException: " + e.getMessage());
            return null;
        } catch (ProtocolException e) {
            Log.e(TAG, "(postRequest)ProtocolException: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "(postRequest)IOException: " + e.getCause());
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }

    public static String getRequest(URL url, Map<String, String> authHeaders) {
        HttpURLConnection connection = null;
        String response = null;

        try {
            Log.v(TAG, "GET: " + url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            if (authHeaders != null) {
                for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = readAll(connection.getInputStream());
            } else {
                Log.e(TAG, "Error, Response code: " + responseCode);
            }

        } catch (ProtocolException e) {
            Log.e(TAG, "Get request error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Get request error: " + e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    public static Map<String, String> getLoginParams(Context context, String csrfToken) {
        Map<String, String> userLoginData = JioFiPreferences.getInstance().getUserLoginData(context);
        Map<String, String> params = new HashMap<>();

        params.put(LOGIN_USERNAME_STRING_ID,
                userLoginData.get(JioFiPreferences.USERNAME_STRING_ID));
        params.put(LOGIN_PASSWORD_STRING_ID,
                userLoginData.get(JioFiPreferences.PASSWORD_STRING_ID));
        params.put(CSRF_TOKEN_STRING_ID, csrfToken);

        return params;
    }

    public static boolean login(final Context context) {

        // If logged in less then 2 minutes before
        cookieString = JioFiPreferences.getInstance().loadCookieString(context);
        if (cookieString != null) {
            return true;
        }

        String urlString = getUrlString(LOGIN_URL_ID);
        URL url = getURL(LOGIN_URL_ID);

        String response;

        try {
            Document loginDocument = Jsoup.connect(urlString).get();
            String csrfToken = loginDocument.select(TOKEN_INPUT_CSS_SELECTOR)
                    .attr(VALUE_ATTRIBUTE_KEY);

            if (csrfToken == null || csrfToken.equals("")) {
                return false;
            }

            Map<String, String> params = getLoginParams(context, csrfToken);
            response = postRequest(url, params, null);

            boolean loginSuccess = response != null &&
                    !response.contains("Login Fail") &&
                    !response.contains("User already logged in !");

            if (response != null) {
                if (response.contains("Login Fail")) {
                    authenticationError = true;
                    Log.e(TAG, "Login Failed !");
                }
                if (response.contains("User already logged in !")) {
                    authenticationError = true;
                    Log.e(TAG, "User already logged in !");
                    return false;
                }
            }

            if (loginSuccess) {
                int startIndex = response.lastIndexOf("ksession") + 12;
                cookieString = response.substring(startIndex, startIndex + 32);

                Log.v(TAG, "cookie " + cookieString);
                authenticationError = false;

                // save the current cookie
                JioFiPreferences.getInstance().saveLoginCookie(context, cookieString);
            } else {
                Log.e(TAG, "Login Failed !");
                return false;
            }

            return true;
        } catch (IOException e) {
            Log.e(TAG, "IOException, Jsoup connect: " + e.getMessage());
            return false;
        }
    }

    public static Map<String, String> getAuthHeaders() {
        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/x-www-form-urlencoded");
        params.put("Cookie", getCookieString());
        return params;
    }

    public static Map<String, String> getPowerSavingTimeOutParams(int powerSavingTimeOut, String csrfToken) {
        Map<String, String> params = new HashMap<>();

        params.put(CSRF_TOKEN_STRING_ID, csrfToken);
        params.put(FORM_TYPE_STRING_ID, SAVING_TIME_FORM_TYPE);
        params.put(SAVING_TIME_STRING_ID, String.valueOf(powerSavingTimeOut));

        Log.v(TAG, csrfToken);

        return params;
    }

    public static boolean changePowerSavingTimeOut(Context context, boolean restart, int powerSavingTimeout) {

        if (!login(context)) {
            return false;
        }

        URL url = getURL(URL_DEVICE_SETTINGS_GET_ID);
        Map<String, String> authHeaders = getAuthHeaders();
        String response = getRequest(url, authHeaders);

        if (response == null) {
            return false;
        }

        try {
            Document deviceSettingDocument = Jsoup.parse(response);
            Elements tokenTags = deviceSettingDocument.select(TOKEN_INPUT_CSS_SELECTOR);

            if (tokenTags.size() > 1) {
                String csrfToken = tokenTags.first().attr(VALUE_ATTRIBUTE_KEY);

                if (restart) {
                    String selectedVal = deviceSettingDocument.select(POWER_SAVING_TIME_INPUT_CSS_SELECTOR).val();
                    powerSavingTimeout = Integer.parseInt(selectedVal);
                }

                Map<String, String> params = getPowerSavingTimeOutParams(powerSavingTimeout, csrfToken);
                url = getURL(URL_DEVICE_SETTINGS_POST_ID);
                response = postRequest(url, params, authHeaders);

                if (response != null) {
                    return true;
                }

            } else {
                Log.e(TAG, "Cookie error or login not successful");
                JioFiPreferences.getInstance().clearSavedCookie(context);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "changePowerSavingTimeOut error: " + e.getMessage());
            JioFiPreferences.getInstance().clearSavedCookie(context);
            return false;
        }
        return false;
    }

    // ----------- SSID and Password ------------------------

    public static String wiFiSSID = null;
    public static String wiFiPassword = null;

    private static final String SSID_INPUT_CSS_SELECTOR = "input[name='SSID_text']";
    private static final String WIFI_CHANNEL_INPUT_CSS_SELECTOR = "input[name='channel'] option[selected]";
    private static final String WIFI_MODE_INPUT_CSS_SELECTOR = "input[name='technology'] option[selected]";
    private static final String WIFI_WMM_INPUT_CSS_SELECTOR = "input[name='WMM_config'] option[selected]";
    private static final String WIFI_BROADCASTING_INPUT_CSS_SELECTOR = "input[name='HSSID_config'] option[selected]";
    private static final String WIFI_SECURITY_MODE_INPUT_CSS_SELECTOR = "input[name='Se_Encryption'] option[selected]";
    private static final String PASSWORD_WPA2_INPUT_CSS_SELECTOR = "input[name='wpa2_key']";

    private static final String WIFI_SETTING_FORM_TYPE = "wifi_set";
    private static final String POST_SSID_ID = "SSID_text";
    private static final String POST_WIFI_CHANNEL_ID = "channel";
    private static final String POST_WIFI_MODE_ID = "technology";
    private static final String POST_WIFI_WMM_ID = "WMM_config";
    private static final String POST_WIFI_BROADCASTING_ID = "HSSID_config";
    private static final String POST_WIFI_SECURITY_MODE_ID = "Se_Encryption";
    private static final String POST_WIFI_WPA_ID = "wpa_key";
    private static final String POST_WIFI_WPA2_ID = "wpa2_key";
    private static final String POST_WIFI_WPA_MIXED_ID = "wpa_mixed_key";

    public static boolean SSIDPasswordIsLoaded() {
        if (wiFiSSID != null && wiFiPassword != null)
            return true;
        return false;
    }

    public static boolean loadCurrentSSIDAndPassword(Context context) {
        // 1. Login
        if (!login(context)) {
            return false;
        }

        // 2. Make a get request to http://jiofi.local.html/Security_Mode.cgi
        URL url = getURL(WIFI_SETTINGS_GET_ID);
        String response = getRequest(url, getAuthHeaders());

        // 3. Get the current SSID and Password
        if (response == null) {
            return false;
        }

        try {
            Document wifiSettingPageDocument = Jsoup.parse(response);
            // 4. Set it to the static variables in NetworkUtils
            String ssid = wifiSettingPageDocument.select(SSID_INPUT_CSS_SELECTOR).val();
            String password = wifiSettingPageDocument.select(PASSWORD_WPA2_INPUT_CSS_SELECTOR).val();

            if (!ssid.isEmpty() && !password.isEmpty()) {
                wiFiSSID = ssid;
                wiFiPassword = password;
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadCurrentSSIDAndPassword error: " + e.getMessage());
            JioFiPreferences.getInstance().clearSavedCookie(context);
            return false;
        }

        return false;
    }

    public static boolean changeSSIDAndPassword(Context context, String SSIDString, String password) {

        // 1. Login
        if (!login(context)) {
            return false;
        }

        // 2. Make a get request
        URL url = getURL(WIFI_SETTINGS_GET_ID);
        String response = getRequest(url, getAuthHeaders());
        if (response == null) {
            return false;
        }

        try {
            Document wifiSettingPageDocument = Jsoup.parse(response);

            // Get all the other parameter values
            String csrfToken = wifiSettingPageDocument.select(TOKEN_INPUT_CSS_SELECTOR).val();
            String wifiChannel = wifiSettingPageDocument.select(WIFI_CHANNEL_INPUT_CSS_SELECTOR).val();
            String wifiMode = wifiSettingPageDocument.select(WIFI_MODE_INPUT_CSS_SELECTOR).val();
            String wifiWMM = wifiSettingPageDocument.select(WIFI_WMM_INPUT_CSS_SELECTOR).val();
            String wifiBroadcasting = wifiSettingPageDocument.select(WIFI_BROADCASTING_INPUT_CSS_SELECTOR).val();
            String wifiSecurity = wifiSettingPageDocument.select(WIFI_SECURITY_MODE_INPUT_CSS_SELECTOR).val();

            // 3. Create the post params
            Map<String, String> postParams = new HashMap<>();

            postParams.put(FORM_TYPE_STRING_ID, WIFI_SETTING_FORM_TYPE);
            postParams.put(CSRF_TOKEN_STRING_ID, csrfToken);
            postParams.put(POST_SSID_ID, SSIDString);
            postParams.put(POST_WIFI_CHANNEL_ID, wifiChannel);
            postParams.put(POST_WIFI_MODE_ID, wifiMode);
            postParams.put(POST_WIFI_WMM_ID, wifiWMM);
            postParams.put(POST_WIFI_BROADCASTING_ID, wifiBroadcasting);
            postParams.put(POST_WIFI_SECURITY_MODE_ID, "wpa2");
            postParams.put(POST_WIFI_WPA_ID, password);
            postParams.put(POST_WIFI_WPA2_ID, password);
            postParams.put(POST_WIFI_WPA_MIXED_ID, password);

            // 4. Make the post request
            url = getURL(WIFI_SETTINGS_POST_ID);
            response = postRequest(url, postParams, getAuthHeaders());

            if (response == null) {
                return false;
            }

            // 5. Clear login data since JioFi restarts
            JioFiPreferences.getInstance().clearSavedCookie(context);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "changeSSIDAndPassword error: " + e.getMessage());
            JioFiPreferences.getInstance().clearSavedCookie(context);
            return false;
        }
    }

    public static boolean pushWPSButton(Context context) {
        // 1. Login
        if (!login(context)) {
            return false;
        }

        // 2. Make a get request
        URL url = getURL(WPS_GET_ID);
        String response = getRequest(url, getAuthHeaders());
        if (response == null) {
            return false;
        }

        // Get the CSRF token
        String csrfToken;
        try {
            Document pushButtonDocument = Jsoup.parse(response);
            if (pushButtonDocument == null) {
                return false;
            }
            csrfToken = pushButtonDocument.select(TOKEN_INPUT_CSS_SELECTOR).last().val();
        } catch (Exception e) {
            Log.e(TAG, "Push button parse error: " + e.getMessage());
            JioFiPreferences.getInstance().clearSavedCookie(context);
            return false;
        }

        Map<String, String> postParams = new HashMap<>();
        postParams.put(CSRF_TOKEN_STRING_ID, csrfToken);
        postParams.put(FORM_TYPE_STRING_ID, "push_pbs");

        // 4. Make the post request
        url = getURL(WPS_POST_ID);
        response = postRequest(url, postParams, getAuthHeaders());

        if (response == null) {
            return false;
        }

        JioFiPreferences.getInstance().saveWPSTime(context);
        return true;
    }

    private static final String POST_WIFI_MAC_MODE = "mode";
    private static final String POST_WIFI_MAC_DENY_COUNT = "deny_cnt";
    private static final String POST_WIFI_MAC_DENY_MAC_FORMAT_STRING = "deny_%d_mac";
    private static final String POST_WIFI_MAC_DENY_DISC_FORMAT_STRING = "deny_%d_dis";
    private static final String POST_WIFI_MAC_DENY_ENABLE_FORMAT_STRING = "deny_%d_enable";
    private static final String POST_WIFI_MAC_DENY_KEY_FORMAT_STRING = "deny_%d_key";

    public static boolean setBlockedDevices(Context context, List<DeviceViewModel> viewModels) {
        // 1. Login
        if (!login(context)) {
            return false;
        }

        // 2. Make a get request
        URL url = getURL(WIFI_MAC_GET_ID);

        String response = getRequest(url, getAuthHeaders());
        if (response == null) {
            return false;
        }

        // Get the CSRF token
        String csrfToken;
        try {
            Document pushButtonDocument = Jsoup.parse(response);
            csrfToken = pushButtonDocument.select(TOKEN_INPUT_CSS_SELECTOR).last().val();
        } catch (Exception e) {
            Log.e(TAG, "setBlockedDevices csrf token error: " + e.getMessage());
            JioFiPreferences.getInstance().clearSavedCookie(context);
            return false;
        }
        Map<String, String> postParams = new HashMap<>();

        postParams.put(CSRF_TOKEN_STRING_ID, csrfToken);
        postParams.put(POST_WIFI_MAC_MODE, "2");

        int count = 0;

        for (DeviceViewModel viewModel: viewModels) {
            if (viewModel.getIsBlocked()) {
                postParams.put(String.format(Locale.US, POST_WIFI_MAC_DENY_MAC_FORMAT_STRING, count), viewModel.getMacAddress());
                postParams.put(String.format(Locale.US, POST_WIFI_MAC_DENY_DISC_FORMAT_STRING, count), viewModel.getDeviceName());
                postParams.put(String.format(Locale.US, POST_WIFI_MAC_DENY_ENABLE_FORMAT_STRING, count), "checked");
                postParams.put(String.format(Locale.US, POST_WIFI_MAC_DENY_KEY_FORMAT_STRING, count), "");
                count++;
            }
        }

        postParams.put(POST_WIFI_MAC_DENY_COUNT, String.valueOf(count));
        String urlString = String.format("%s?%s", getUrlString(WIFI_MAC_POST_ID), new Date().getTime());
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL for wifi mac post");
        }
        response = postRequest(url, postParams, getAuthHeaders());
        return response != null;
    }
}
