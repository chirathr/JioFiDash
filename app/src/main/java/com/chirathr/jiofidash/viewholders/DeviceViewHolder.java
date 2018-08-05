package com.chirathr.jiofidash.viewholders;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.data.DeviceViewModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    private TextView deviceNameTextView;
    private TextView macAddressTextView;
    private TextView ipAddressTextView;
    private TextView isConnectedTextView;

    public DeviceViewHolder(@NonNull View itemView) {
        super(itemView);

        deviceNameTextView = itemView.findViewById(R.id.tv_device_name);
        macAddressTextView = itemView.findViewById(R.id.tv_device_mac);
        ipAddressTextView = itemView.findViewById(R.id.tv_device_ip);
        isConnectedTextView = itemView.findViewById(R.id.tv_device_is_connected);
    }

    public void bindData(DeviceViewModel viewModel) {
        deviceNameTextView.setText(viewModel.getDeviceName());
        macAddressTextView.setText(viewModel.getMacAddress());
        ipAddressTextView.setText(viewModel.getIpAddress());
        isConnectedTextView.setText(viewModel.getIsConnectedString());
        if (viewModel.getIsConnected()) {
            isConnectedTextView.setTextColor(Color.parseColor("#9cff57"));
        } else {
            isConnectedTextView.setTextColor(Color.parseColor("#ff7539"));
        }
    }
}
