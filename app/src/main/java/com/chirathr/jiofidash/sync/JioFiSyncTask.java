package com.chirathr.jiofidash.sync;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.chirathr.jiofidash.utils.NetworkUtils;

public class JioFiSyncTask {

    private static final String TAG = JioFiSyncTask.class.getSimpleName();

    synchronized public static void syncDeviceInfo(Context context) {
        try {
            String jsonDataString = NetworkUtils.getJsonData(
                    context,
                    NetworkUtils.DEVICE_INFO_ID,
                    NetworkUtils.DEVICE_6_ID
            );
        }
        catch (Exception e) {
            Log.v(TAG, "Possibly network error :" + e.getMessage());
        }
    }
}
