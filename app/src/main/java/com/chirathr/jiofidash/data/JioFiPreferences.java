package com.chirathr.jiofidash.data;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class JioFiPreferences {

    public static final String USERNAME_STRING_ID = "username";
    public static final String PASSWORD_STRING_ID = "password";

    public static Map<String, String> getUserLoginData(Context context) {
        Map<String,String> params = new HashMap<String, String>();

        params.put(USERNAME_STRING_ID, "chirath");
        params.put(PASSWORD_STRING_ID, "Rchirath02");

        return params;
    }
}
