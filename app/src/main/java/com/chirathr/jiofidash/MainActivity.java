package com.chirathr.jiofidash;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.utils.NetworkUtils;

import java.net.URL;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    Toast mwifiEnableToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (NetworkUtils.wifiEnabled(this)) {
            new UpdateDataTask().execute();

        } else {
            mwifiEnableToast = Toast.makeText(this, "This app requires a WiFi connection", Toast.LENGTH_LONG);
            mwifiEnableToast.show();
        }
    }


    private class UpdateDataTask extends AsyncTask<Void, Void, Void> {

        private JioFiData jioFiData;

        TextView batteryLevelTextView = (TextView) findViewById(R.id.tv_battery_level);
        TextView batteryStatusTextView = (TextView) findViewById(R.id.tv_battery_status);

        @Override
        protected Void doInBackground(Void... voids) {

            jioFiData = new JioFiData();

            try {
                jioFiData.loadDeviceInfo(MainActivity.this);

                publishProgress();
                sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            if (jioFiData != null) {
                String batteryLevelString = jioFiData.batteryLevel + " %";
                batteryLevelTextView.setText(batteryLevelString);
                batteryStatusTextView.setText(jioFiData.batteryStatus);
            }
        }
    }

}
