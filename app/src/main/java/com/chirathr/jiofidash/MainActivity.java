package com.chirathr.jiofidash;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.utils.NetworkUtils;

import java.net.URL;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    Toast mwifiEnableToast;

    UpdateDataTask mUpdateDataTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkUtils.wifiEnabled(this)) {
            mUpdateDataTask = new UpdateDataTask();
            mUpdateDataTask.execute();

        } else {
            mwifiEnableToast = Toast.makeText(this, "This app requires a WiFi connection", Toast.LENGTH_LONG);
            mwifiEnableToast.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mUpdateDataTask != null)
            mUpdateDataTask.cancel(true);
    }

    private class UpdateDataTask extends AsyncTask<Void, Void, Void> {

        private JioFiData jioFiData;

        TextView batteryLevelTextView = (TextView) findViewById(R.id.tv_battery_level);
        TextView batteryStatusTextView = (TextView) findViewById(R.id.tv_battery_status);

        TextView lteStatusTextView = (TextView) findViewById(R.id.tv_lte_status);
        TextView lteBandTextView = (TextView) findViewById(R.id.tv_lte_band);
        TextView lteBandwidthTextView = (TextView) findViewById(R.id.tv_lte_bandwidth);
        TextView lteCellIdTextView = (TextView) findViewById(R.id.tv_lte_cell_id);


        @Override
        protected Void doInBackground(Void... voids) {

            jioFiData = new JioFiData();

            try {

                while (!isCancelled()) {
                    jioFiData.loadDeviceInfo(MainActivity.this);
                    jioFiData.loadLteInfo(MainActivity.this);
                    publishProgress();
                    sleep(1000);
                }

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

                lteStatusTextView.setText(jioFiData.lteStatus);
                lteBandTextView.setText(String.valueOf(jioFiData.lteBand));
                lteBandwidthTextView.setText(jioFiData.lteBandwidth);
                lteCellIdTextView.setText(String.valueOf(jioFiData.lteCellId));

                Log.v("Update", String.valueOf(jioFiData.lteBand));
            }


        }
    }

}
