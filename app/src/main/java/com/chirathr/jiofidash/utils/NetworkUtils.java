package com.chirathr.jiofidash.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chirathr.jiofidash.data.JioFiPreferences;

import org.json.JSONObject;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String DEFAULT_HOST = "http://jiofi.local.html";

    // Instantiate the RequestQueue.
    private static RequestQueue requestQueue;

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
            "/LAN_info.cgi"
    };

    // Id to represent types of data from device urls
    public static final int LTE_INFO_ID = 0;
    public static final int LAN_INFO_ID = 1;
    public static final int WAN_INFO_ID = 2;
    public static final int DEVICE_INFO_ID = 3;
    public static final int PERFORMANCE_INFO_ID = 4;
    public static final int LOGIN_URL_ID = 5;
    public static final int URL_DEVICE_SETTINGS_ID = 6;
    public static final int URL_DEVICE_SETTINGS_POST_ID = 7;
    public static final int LAN_INFO_PAGE_ID = 8;

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


    public static final int CONNECTION_TIMEOUT = 3000;


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

    public static String getJsonData(Context context, int urlType, int deviceType) {
        String response = null;
        HttpURLConnection urlConnection = null;
        URL url = getURL(urlType);

        if (url == null) return null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();

            if (hasInput)
                response = scanner.next();
            scanner.close();
        } catch (IOException e) {
            Log.v(TAG, ": Connection error: " + e.getMessage());
            response = null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
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

    public static void initRequestQueue(Context context) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
    }

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

    public static String postRequest(URL url, Map<String, String> params, Map<String, String> authHeaders) {

        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        BufferedWriter writer = null;
        int responseCode;
        StringBuilder result = new StringBuilder();

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
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
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
        return result.toString();
    }

    public static String getRequest(URL url, Map<String, String> params, Map<String, String> authHeaders) {
        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();
        OutputStream outputStream = null;
        BufferedWriter writer = null;

        try {

            Log.v(TAG, url.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            if (params != null) {
//                outputStream = connection.getOutputStream();
//                writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//                writer.write(getPostParams(params));
            }

            if (authHeaders != null) {
                for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
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

        return result.toString();
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

            if (response != null && response.contains("Login Fail")) {
                authenticationError = true;
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

        URL url = getURL(URL_DEVICE_SETTINGS_ID);
        Map<String, String> authHeaders = getAuthHeaders();
        String response = getRequest(url, null, authHeaders);

        if (response == null) {
            return false;
        }

        Document deviceSettingDocument = Jsoup.parse(response);
        Elements tokenTags = deviceSettingDocument.select(TOKEN_INPUT_CSS_SELECTOR);

        if (tokenTags.size() > 1) {
            csrfToken = tokenTags.first().attr(VALUE_ATTRIBUTE_KEY);
            // TODO change convert to Variable
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


    private static void postPowerSavingTimeOut(Context context, final int powerSavingTimeOut) {

        // Post data and cookie
        // form_type: sleep_time
        // token: 932658574
        // Saving_Time: 20

        initRequestQueue(context);

        Response.Listener<String> powerSavingListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Power save timeout changed to: " + powerSavingTimeOut);
//                Log.v(TAG, response);
            }
        };

        final Response.ErrorListener powerSavingErrorListner = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Failed to Power save timeout settings: " + error.getMessage());
            }
        };

        String deviceSettingUrl = getUrlString(URL_DEVICE_SETTINGS_POST_ID);

        StringRequest powerSaveChangeRequest = new StringRequest(
                Request.Method.POST,
                deviceSettingUrl,
                powerSavingListener,
                powerSavingErrorListner
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(CSRF_TOKEN_STRING_ID, csrfToken);
                params.put(FORM_TYPE_STRING_ID, SAVING_TIME_FORM_TYPE);
                params.put(SAVING_TIME_STRING_ID, String.valueOf(powerSavingTimeOut));

                Log.v(TAG, csrfToken);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Cookie", getCookieString());
                return params;
            }
        };

        requestQueue.add(powerSaveChangeRequest);
    }
}
