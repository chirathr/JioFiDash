package com.chirathr.jiofidash.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.chirathr.jiofidash.data.JioFiPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
            "/Security_Mode_sv.cgi"
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

    private static final String TOKEN_INPUT_CSS_SELECTOR = "input[name='token']";
    private static final String POWER_SAVING_TIME_INPUT_CSS_SELECTOR = "input[name='Saving_Time'] option[selected]";
    private static final String VALUE_ATTRIBUTE_KEY = "value";

    private static String LOGIN_USERNAME_STRING_ID = "identify";
    private static String LOGIN_PASSWORD_STRING_ID = "password";
    private static String csrfToken = null;

    private static String cookieString = null;
    public static Date loggedInAt;
    public static boolean authenticationError = false;

    public static final int LOGIN_TIMEOUT = 10;

    // Power saving settings
    private static String SAVING_TIME_STRING_ID = "Saving_Time";
    private static String SAVING_TIME_FORM_TYPE = "sleep_time";

    private static final String COOKIE_FORMAT_STRING = "ksession=%s";


    public static final int CONNECTION_TIMEOUT = 50000;


    public static String getUrlString(int urlType) {
        int deviceType = JioFiPreferences.currentDeviceId;
        String[] deviceUrls = getDeviceUrls(deviceType);
        return getHostAddress() + deviceUrls[urlType];
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
            Log.v(TAG, "(postRequest)UnsupportedEncodingException: " + e.getMessage());
            return null;
        } catch (ProtocolException e) {
            Log.v(TAG, "(postRequest)ProtocolException: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.v(TAG, "(postRequest)IOException: " + e.getCause());
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }

    public static String getRequest(URL url, Map<String, String> params, Map<String, String> authHeaders) {
        HttpURLConnection connection = null;
        String response = null;
        OutputStream outputStream = null;
        BufferedWriter writer = null;

        try {

            Log.v(TAG, url.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            if (params != null) { }

            if (authHeaders != null) {
                for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = readAll(connection.getInputStream());
                Log.v(TAG, response);
            } else {
                Log.v(TAG, "Response code: " + responseCode);
            }

        } catch (ProtocolException e) {
            Log.v(TAG, "Get request error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.v(TAG, "Get request error: " + e.getMessage());
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

    public static boolean isLoggedIn() {

        Calendar cal = Calendar.getInstance();
        Date dateTimeNow = cal.getTime();

        if (cookieString != null && loggedInAt != null) {
            long diffInTime = (long) ((dateTimeNow.getTime() - loggedInAt.getTime()) / (1000 * 60 * 60 * 24));
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInTime);

            if (minutes < LOGIN_TIMEOUT) {
                return true;
            }
        }

        return false;
    }

    public static void clearLogin() {
        cookieString = null;
        loggedInAt = null;
    }

    public static boolean login(final Context context) {

        if (isLoggedIn()) {
            return true;
        }

        String urlString = getUrlString(LOGIN_URL_ID);
        URL url = getURL(LOGIN_URL_ID);

        String response;

        try {
            Document loginDocument = Jsoup.connect(urlString).get();
            csrfToken = loginDocument.select(TOKEN_INPUT_CSS_SELECTOR)
                    .attr(VALUE_ATTRIBUTE_KEY);
            Log.v(TAG, "Login token: " + csrfToken);

            if (csrfToken == null || csrfToken.equals("")) {
                return false;
            }

            Map<String, String> params = getLoginParams(context, csrfToken);
            response = postRequest(url, params, null);

            boolean loginSucess = response != null &&
                    !response.contains("Login Fail") &&
                    !response.contains("User already logged in !");

            if (response != null) {
                if (response.contains("Login Fail")) {
                    authenticationError = true;
                    Log.v(TAG, "Login Failed !");
                }
                if (response.contains("User already logged in !")) {
                    authenticationError = true;
                    Log.v(TAG, "User already logged in !");
                    login(context);
                    return true;
                }
            }

            if (loginSucess) {
                int startIndex = response.lastIndexOf("ksession") + 12;
                cookieString = response.substring(startIndex, startIndex + 32);

                Log.v(TAG, "cookie " + cookieString);
                authenticationError = false;

                Calendar cal = Calendar.getInstance();
                loggedInAt = cal.getTime();

            } else {
                Log.v(TAG, "Login Failed !");
                return false;
            }

            return true;
        } catch (IOException e) {
            Log.v(TAG, "IOException, Jsoup connect: " + e.getMessage());
            csrfToken = null;
            return false;
        }
    }

    public static Map<String, String> getAuthHeaders() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/x-www-form-urlencoded");
        params.put("Cookie", getCookieString());
        return params;
    }

    public static Map<String, String> getPowerSavingTimeOutParams(int powerSavingTimeOut) {
        Map<String, String> params = new HashMap<>();

        params.put(CSRF_TOKEN_STRING_ID, csrfToken);
        params.put(FORM_TYPE_STRING_ID, SAVING_TIME_FORM_TYPE);
        params.put(SAVING_TIME_STRING_ID, String.valueOf(powerSavingTimeOut));

        Log.v(TAG, csrfToken);

        return params;
    }

    public static boolean changePowerSavingTimeOut(Context context, boolean restart, int powerSavingTimeout) {

        if (!isLoggedIn()) {
            login(context);
        }

        URL url = getURL(URL_DEVICE_SETTINGS_GET_ID);
        Map<String, String> authHeaders = getAuthHeaders();
        String response = getRequest(url, null, authHeaders);

        if (response == null) {
            return false;
        }

        Document deviceSettingDocument = Jsoup.parse(response);
        Elements tokenTags = deviceSettingDocument.select(TOKEN_INPUT_CSS_SELECTOR);

        if (tokenTags.size() > 1) {
            csrfToken = tokenTags.first().attr(VALUE_ATTRIBUTE_KEY);
            Log.v(TAG, "changePowerSavingTimeOut csrf: " + csrfToken);

            if (restart) {
                String selectedVal = deviceSettingDocument.select(POWER_SAVING_TIME_INPUT_CSS_SELECTOR).val();
                powerSavingTimeout = Integer.parseInt(selectedVal);
            }

            Map<String, String> params = getPowerSavingTimeOutParams(powerSavingTimeout);
            url = getURL(URL_DEVICE_SETTINGS_POST_ID);
            response = postRequest(url, params, authHeaders);

            return response != null;

        } else {
            Log.v(TAG, "Cookie error or login not successful");
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
        if (!isLoggedIn()) {
            Log.v(TAG, "Is not logged in");
            login(context);
        }
        // 2. Make a get request to http://jiofi.local.html/Security_Mode.cgi
        URL url = getURL(WIFI_SETTINGS_GET_ID);
        String response = getRequest(url, null, getAuthHeaders());

        // 3. Get the current SSID and Password
        if (response == null) {
            return false;
        }

        Document wifiSettingPageDocument = Jsoup.parse(response);
        // 4. Set it to the static variables in NetworkUitls
        wiFiSSID = wifiSettingPageDocument.select(SSID_INPUT_CSS_SELECTOR).val();
        wiFiPassword = wifiSettingPageDocument.select(PASSWORD_WPA2_INPUT_CSS_SELECTOR).val();

        return true;
    }

    public static boolean changeSSIDAndPassword(Context context, String SSIDString, String password) {

        // 1. Login
        if (!isLoggedIn()) {
            login(context);
        }

        // 2. Make a get request
        URL url = getURL(WIFI_SETTINGS_GET_ID);
        String response = getRequest(url, null, getAuthHeaders());
        if (response == null) {
            return false;
        }

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
        postParams.put(POST_WIFI_SECURITY_MODE_ID, wifiSecurity);
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
        clearLogin();
        return true;
    }

}
