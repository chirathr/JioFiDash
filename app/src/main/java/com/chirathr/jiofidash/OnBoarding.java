package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chirathr.jiofidash.adapters.JioFiDeviceListAdapter;
import com.chirathr.jiofidash.data.JioFiDeviceViewModel;
import com.chirathr.jiofidash.data.JioFiDevicesData;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.utils.NetworkUtils;

import java.util.List;

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

    }
}
