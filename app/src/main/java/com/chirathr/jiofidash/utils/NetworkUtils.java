package com.chirathr.jiofidash.utils;

import android.content.Context;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DEFAULT_HOST = "http://jiofi.local.html";

    // Instantiate the RequestQueue.
    private static RequestQueue requestQueue;

    // JioFI 6
    public static final int DEVICE_6_ID = 6;
    private static final String[] DEVICE_6_URLS = new String[]{
            "/lte_ajax.cgi",
            "/lan_ajax.cgi",
            "/wan_info_ajax.cgi",
            "/Device_info_ajax.cgi",
            "/performance_ajax.cgi",
            "/login.cgi",
            "/Device_setting.cgi",
            "/Device_setting_sv.cgi"
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

    // Get device url based on type
    public static String[] getDeviceUrls(int deviceType) {
        if (deviceType == 6)    // JioFi 6
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
    private static final String VALUE_ATTRIBUTE_KEY = "value";

    private static String LOGIN_USERNAME_STRING_ID = "identify";
    private static String LOGIN_PASSWORD_STRING_ID = "password";
    public static String csrfToken = null;

    private static String cookieString = null;

    // Power saving settings
    private static String SAVING_TIME_STRING_ID = "Saving_Time";
    private static String SAVING_TIME_FORM_TYPE = "sleep_time";


    public static String getUrlString(int urlType) {
        int deviceType = DEVICE_6_ID;
        String[] deviceUrls = getDeviceUrls(deviceType);
        return getHostAddress() + deviceUrls[urlType];
    }

    private static URL getURL(int urlType, int deviceType) {
        URL url = null;
        String urlString;

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

    // TODO(1) Use volley to make the requests

    public static String getJsonData(Context context, int urlType, int deviceType) {
        String response = null;
        HttpURLConnection urlConnection = null;
        URL url = getURL(urlType, deviceType);

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

    public static boolean wifiEnabled(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifi != null && wifi.isWifiEnabled();
    }

    public static boolean jiofiAvailableCheck() {
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(getHostAddress());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(100);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200)
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
            return "ksession=" + cookieString;
        else {
            Log.v(TAG, "Cookie not set");
            return null;
        }
    }

    public static void login(final Context context) {

        initRequestQueue(context);

        String loginUrl = getUrlString(LOGIN_URL_ID);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Get the login token
                        Document loginDocument = Jsoup.parse(response);
                        csrfToken = loginDocument.select(TOKEN_INPUT_CSS_SELECTOR)
                                .attr(VALUE_ATTRIBUTE_KEY);

                        Log.v(TAG, "Login token: " + csrfToken);

                        loginAndSetCookie(context);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Failed to get login Token: " + error.getMessage());
                csrfToken = null;
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private static void loginAndSetCookie(final Context context) {

        initRequestQueue(context);

        // Login token is not set
        if (csrfToken == null)
            return;

        String loginUrl = getUrlString(LOGIN_URL_ID);

        Response.Listener<String> loginListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.

                if (!response.contains("User already logged in !")) {
                    int startIndex = response.lastIndexOf("ksession") + 12;
                    cookieString = response.substring(startIndex, startIndex + 32);

                    Log.v(TAG, "cookie " + cookieString);
                } else {
                    Log.v(TAG, "User already logged in !");
                }
            }
        };

        Response.ErrorListener LoginErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Failed to Login: " + error.getMessage());
            }
        };

        // Request a string response from the provided URL.
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, loginUrl, loginListener, LoginErrorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> userLoginData = JioFiPreferences.getUserLoginData(context);
                        Map<String, String> params = new HashMap<>();

                        params.put(LOGIN_USERNAME_STRING_ID,
                                userLoginData.get(JioFiPreferences.USERNAME_STRING_ID));
                        params.put(LOGIN_PASSWORD_STRING_ID,
                                userLoginData.get(JioFiPreferences.PASSWORD_STRING_ID));
                        params.put(CSRF_TOKEN_STRING_ID, csrfToken);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };

        requestQueue.add(stringRequest);
    }

    public static void changePowerSavingTimeOut(final Context context, final int powerSavingTimeOut) {


        initRequestQueue(context);

        if (cookieString == null) {
            Log.v(TAG, "Cookie not set, login required!");
            return;
        }

        Response.Listener<String> deviceSettingLister = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.

                Document deviceSettingDocument = Jsoup.parse(response);

//                Log.v(TAG, response);

                Elements tokenTags = deviceSettingDocument.select(TOKEN_INPUT_CSS_SELECTOR);

                if (tokenTags.size() > 1) {
                    csrfToken = tokenTags.first().attr(VALUE_ATTRIBUTE_KEY);
                    // TODO change convert to Variable
                    Log.v(TAG, "changePowerSavingTimeOut csrf: " + csrfToken);
                    postPowerSavingTimeOut(context, powerSavingTimeOut);
                } else {
                    Log.v(TAG, "Cookie error or login not successful");
                    login(context);
                }
            }
        };

        Response.ErrorListener deviceSettingErrorLister = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Failed to get power save change csrf token: " + error.getMessage());
            }
        };


        String deviceSettingUrl = getUrlString(URL_DEVICE_SETTINGS_ID);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, deviceSettingUrl, deviceSettingLister, deviceSettingErrorLister) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Cookie", getCookieString());

                return params;
            }
        };

        requestQueue.add(stringRequest);
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
