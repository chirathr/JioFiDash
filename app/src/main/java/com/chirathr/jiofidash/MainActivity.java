package com.chirathr.jiofidash;

import android.content.Context;
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

        if (!NetworkUtils.wifiEnabled(this)) {
            mwifiEnableToast = Toast.makeText(this, "This app requires a WiFi connection.", Toast.LENGTH_LONG);
            mwifiEnableToast.show();
        }

        mUpdateDataTask = new UpdateDataTask();
        mUpdateDataTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mUpdateDataTask != null)
            mUpdateDataTask.cancel(false);
    }

    private class UpdateDataTask extends AsyncTask<Void, Void, Void> {

        private boolean jiofiAvailable = true;

        private JioFiData jioFiData;

        TextView batteryLevelTextView = (TextView) findViewById(R.id.tv_battery_level);
        TextView batteryStatusTextView = (TextView) findViewById(R.id.tv_battery_status);

        TextView lteStatusTextView = (TextView) findViewById(R.id.tv_lte_status);
        TextView lteBandTextView = (TextView) findViewById(R.id.tv_lte_band);
        TextView lteBandwidthTextView = (TextView) findViewById(R.id.tv_lte_bandwidth);
        TextView lteCellIdTextView = (TextView) findViewById(R.id.tv_lte_cell_id);

        TextView uploadSpeedTextView = (TextView) findViewById(R.id.tv_upload_speed);
        TextView uploadSpeedMaxTextView = (TextView) findViewById(R.id.tv_upload_speed_max);
        TextView downloadSpeedTextView = (TextView) findViewById(R.id.tv_download_speed);
        TextView downloadSpeedMaxTextView = (TextView) findViewById(R.id.tv_download_speed_max);

        @Override
        protected Void doInBackground(Void... voids) {
            Context context = MainActivity.this;
            jioFiData = new JioFiData();

            try {

                while (!isCancelled()) {

                    if (NetworkUtils.wifiEnabled(context)) {
                        if (NetworkUtils.jiofiAvailableCheck()) {
                            jiofiAvailable = true;
                            jioFiData.loadDeviceInfo(context);
                            jioFiData.loadLteInfo(context);
                            jioFiData.loadPerformanceInfo(context);
                        } else {
                            jiofiAvailable = false;
                            sleep(1000);
                        }
                    }

                    publishProgress();
                    sleep(1000);

                    if (isCancelled())
                        break;
                }
                return null;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            if (!jiofiAvailable) {

                if (mwifiEnableToast != null) mwifiEnableToast.cancel();
                mwifiEnableToast = Toast.makeText(MainActivity.this, "JioFi not found.", Toast.LENGTH_LONG);
                mwifiEnableToast.show();

            } else if (jioFiData != null) {
                String batteryLevelString = jioFiData.batteryLevel + " %";
                batteryLevelTextView.setText(batteryLevelString);
                batteryStatusTextView.setText(jioFiData.batteryStatus);

                lteStatusTextView.setText(jioFiData.lteStatus);
                lteBandTextView.setText(String.valueOf(jioFiData.lteBand));
                lteBandwidthTextView.setText(jioFiData.lteBandwidth);
                lteCellIdTextView.setText(String.valueOf(jioFiData.lteCellId));

                uploadSpeedTextView.setText(jioFiData.uploadRateString);
                uploadSpeedMaxTextView.setText(jioFiData.uploadRateMaxString);
                downloadSpeedTextView.setText(jioFiData.downloadRateString);
                downloadSpeedMaxTextView.setText(jioFiData.downloadRateMaxString);

                Log.v("Update", String.valueOf(jioFiData.lteBand));
            }
        }
    }

}
