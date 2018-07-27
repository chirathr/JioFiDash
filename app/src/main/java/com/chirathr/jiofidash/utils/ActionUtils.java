package com.chirathr.jiofidash.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ActionUtils {

    private static final String TAG = ActionUtils.class.getSimpleName();
    private static final String TOKEN_INPUT_CSS_SELECTOR = "input[name='token']";
    private static final String VALUE_ATTRIBUTE_KEY = "value";

    public static String getLoginToken() {

        int loginUrlId = NetworkUtils.LOGIN_URL_ID;
        String loginUrlPath = NetworkUtils.getDeviceUrls(NetworkUtils.DEVICE_6_ID)[loginUrlId];
        String loginUrl = NetworkUtils.getHostAddress() + loginUrlPath;

        try {
            Document loginDocument = Jsoup.connect(loginUrl).get();
            String token = loginDocument.select(TOKEN_INPUT_CSS_SELECTOR).attr(VALUE_ATTRIBUTE_KEY);

            Log.v(TAG, "Login token: " + token);
            return token;

        } catch (IOException e) {
            Log.v(TAG, "Failed to get login Token: " + e.getMessage());
            return null;
        }
    }
}
