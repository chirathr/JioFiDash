package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.chirathr.jiofidash.adapters.JioFiDeviceListAdapter;
import com.chirathr.jiofidash.data.JioFiDeviceViewModel;
import com.chirathr.jiofidash.data.JioFiDevicesData;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.utils.NetworkUtils;

import java.util.List;

import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_4_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_5_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_6_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_JIOFI_M2S_ID;
import static com.chirathr.jiofidash.utils.NetworkUtils.DEVICE_OTHER_ID;

public class OnBoarding extends AppCompatActivity implements JioFiDeviceListAdapter.JioFiDeviceOnClickLisetner {

    private static final String TAG = OnBoarding.class.getSimpleName();
    private RecyclerView jioFiDevicesRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        jioFiDevicesRecyclerView = findViewById(R.id.jioFiDeviceRecyclerView);
        List<JioFiDeviceViewModel> jioFiDeviceViewModels = JioFiDevicesData.getDevices();

        jioFiDevicesRecyclerView.setHasFixedSize(true);
        jioFiDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jioFiDevicesRecyclerView.setAdapter(new JioFiDeviceListAdapter(this, this, jioFiDeviceViewModels));

    }

    @Override
    public void selectDevice(int deviceId) {
        switch (deviceId) {
            case DEVICE_OTHER_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_4_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_5_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_M2S_ID: {
                openWebUI();
                break;
            }
            case DEVICE_JIOFI_6_ID: {
                setDeviceAndStartMainActivity(NetworkUtils.DEVICE_JIOFI_6_ID);
                break;
            }
        }
    }

    public void openWebUI() {
        Uri webPage = Uri.parse(NetworkUtils.DEFAULT_HOST);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            //Page not found
            Log.v(TAG, "page not found, open web page.");
        }
    }

    public void setDeviceAndStartMainActivity(int deviceId) {
        JioFiPreferences.getInstance().setDevice(this, deviceId);
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
