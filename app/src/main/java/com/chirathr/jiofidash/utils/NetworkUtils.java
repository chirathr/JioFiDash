package com.chirathr.jiofidash.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    // JioFI 6
    public static final int DEVICE_6_ID = 6;
    private static final String[] DEVICE_6_URLS = new String[] {
            "http://jiofi.local.html/lte_ajax.cgi",
            "http://jiofi.local.html/lan_ajax.cgi",
            "http://jiofi.local.html/wan_info_ajax.cgi",
            "http://jiofi.local.html/Device_info_ajax.cgi",
            "http://jiofi.local.html/performance_ajax.cgi"
    };

    // Id to represent types of data from device urls
    private static final int LTE_INFO_ID = 0;
    private static final int LAN_INFO_ID = 1;
    private static final int WAN_INFO_ID = 2;
    private static final int DEVICE_INFO_ID = 3;
    private static final int PERFORMANCE_INFO_ID = 4;


    // Get device url based on type
    public static String[] getDeviceUrls(int deviceType) {
        if (deviceType == 6)    // JioFi 6
            return DEVICE_6_URLS;

        return null;
    }

    public static URL getURL(int urlType, int deviceType) {
        URL url = null;
        String urlString;

        String[] deviceUrls = getDeviceUrls(deviceType);
        if (deviceUrls == null) return null;
        urlString = deviceUrls[urlType];

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
        URL url = getURL(urlType, deviceType);

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
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }
}
