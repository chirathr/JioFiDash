package com.chirathr.jiofidash;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new UpdateDataTask().execute();
    }


    private class UpdateDataTask extends AsyncTask<Void, Void, Void> {

        private JioFiData jioFiData;

        @Override
        protected Void doInBackground(Void... voids) {

            // Get the data from the network
            String jsonDataString = NetworkUtils.getJsonData(
                    MainActivity.this, NetworkUtils.DEVICE_INFO_ID, NetworkUtils.DEVICE_6_ID);

//            String jsonDataString = "{ batterylevel:'0 %', batterystatus:'No Battery', curr_time:'Wed 25 Jul 2018 20:39:53'}";

            jioFiData = new JioFiData();

            // Parse the data
            jioFiData.setDeviceInfo(jsonDataString);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            TextView batteryLevelTextView = (TextView) findViewById(R.id.tv_battery_level);
            TextView batteryStatusTextView = (TextView) findViewById(R.id.tv_battery_status);

            if (jioFiData != null) {
                String batteryLevelString = jioFiData.batteryLevel + " %";
                batteryLevelTextView.setText(batteryLevelString);
                batteryStatusTextView.setText(jioFiData.batteryStatus);
            }

        }
    }

}
