package com.chirathr.jiofidash;

import android.content.Context;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.chirathr.jiofidash.data.JioFiData;
import com.chirathr.jiofidash.progressBar.ColorArcProgressBar;
import com.chirathr.jiofidash.utils.NetworkUtils;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    Toast mWifiEnableToast;

    UpdateDataTask mUpdateDataTask;

    ColorArcProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkUtils.login(this);

        progressBar = (ColorArcProgressBar) findViewById(R.id.bar1);
        progressBar.setCurrentValues(80);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!NetworkUtils.wifiEnabled(this)) {
            mWifiEnableToast = Toast.makeText(this, "This app requires a WiFi connection.", Toast.LENGTH_LONG);
            mWifiEnableToast.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedItemId = item.getItemId();

        if (selectedItemId == R.id.action_restart) {
            NetworkUtils.changePowerSavingTimeOut(this, 10);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private class UpdateDataTask extends AsyncTask<Void, Void, Void> {

        private boolean wifiAvailable = true;
        private boolean jiofiAvailable = true;

        private JioFiData jioFiData;

        TextView deviceCountTextView = (TextView) findViewById(R.id.tv_devices_count);
//        TextView userNameTextView = (TextView) findViewById(R.id.tv_user_name);
//        TextView userConnectedTextView = (TextView) findViewById(R.id.tv_user_connected);

        @Override
        protected Void doInBackground(Void... voids) {
            Context context = MainActivity.this;
            jioFiData = new JioFiData();

            try {

                while (!isCancelled()) {

                    if (NetworkUtils.wifiEnabled(context)) {
                        wifiAvailable = true;
                        if (NetworkUtils.jiofiAvailableCheck()) {
                            jiofiAvailable = true;
                            jioFiData.loadDeviceInfo(context);
                            jioFiData.loadLteInfo(context);
                            jioFiData.loadLanInfo(context);
                        } else {
                            jiofiAvailable = false;
                            sleep(1000);
                        }
                    } else {
                        wifiAvailable = false;
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

            if (wifiAvailable && jiofiAvailable) {

                // Device Info
                String batteryLevelString = jioFiData.batteryLevel + " %";
//                batteryLevelTextView.setText(batteryLevelString);
//                batteryStatusTextView.setText(jioFiData.batteryStatus);


                // Lan info
                deviceCountTextView.setText(String.valueOf(jioFiData.userCount));

                StringBuilder usersName = new StringBuilder();
                StringBuilder userConnected = new StringBuilder();

                for (int i = 0; i < jioFiData.userNameList.size(); ++i) {
                    usersName.append(jioFiData.userNameList.get(i)).append("\n\n");
                    if (jioFiData.userConnectedList.get(i))
                        userConnected.append("Connected" + "\n\n");
                    else
                        userConnected.append("Disconnected" + "\n\n");
                }
//
//                userNameTextView.setText(usersName.toString());
//                userConnectedTextView.setText(userConnected.toString());

                Log.v("Update", String.valueOf(jioFiData.lteBand));

            } else if (jioFiData != null) {
                if (mWifiEnableToast != null) mWifiEnableToast.cancel();
//                mWifiEnableToast = Toast.makeText(MainActivity.this, "JioFi not found.", Toast.LENGTH_LONG);
//                mWifiEnableToast.show();
            }
        }
    }

}
