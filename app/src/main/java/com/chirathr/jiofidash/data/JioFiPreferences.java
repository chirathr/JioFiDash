package com.chirathr.jiofidash.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.chirathr.jiofidash.R;

import java.util.HashMap;
import java.util.Map;

public class JioFiPreferences {

    public static final String USERNAME_STRING_ID = "username";
    public static final String PASSWORD_STRING_ID = "password";

    private static JioFiPreferences mInstance;

    public String username;
    public String password;

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

    // TODO load from shared preference
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

    public boolean loadUsernameAndPassword(Context context) {

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
}
