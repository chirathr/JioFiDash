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

    ColorArcProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ColorArcProgressBar) findViewById(R.id.bar1);
        progressBar.setCurrentValues(80);
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
}
