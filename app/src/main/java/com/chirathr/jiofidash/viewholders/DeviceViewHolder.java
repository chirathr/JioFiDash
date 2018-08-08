package com.chirathr.jiofidash.viewholders;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.adapters.DeviceListAdapter;
import com.chirathr.jiofidash.data.DeviceViewModel;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.utils.NetworkUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    private TextView deviceNameTextView;
    private TextView macAddressTextView;
    private TextView ipAddressTextView;
    private TextView isConnectedTextView;
    private ImageView blockButton;

    private boolean isBlocked = false;

    private DeviceListAdapter.OnClickListener mListener;

    public DeviceViewHolder(@NonNull View itemView) {
        super(itemView);

        deviceNameTextView = itemView.findViewById(R.id.tv_device_name);
        macAddressTextView = itemView.findViewById(R.id.tv_device_mac);
        ipAddressTextView = itemView.findViewById(R.id.tv_device_ip);
        isConnectedTextView = itemView.findViewById(R.id.tv_device_is_connected);
        blockButton = itemView.findViewById(R.id.cancel_image);
    }

    public void bindData(DeviceViewModel viewModel, final int id, DeviceListAdapter.OnClickListener onClickListener) {
        deviceNameTextView.setText(viewModel.getDeviceName());
        macAddressTextView.setText(viewModel.getMacAddress());
        ipAddressTextView.setText(viewModel.getIpAddress());
        isConnectedTextView.setText(viewModel.getIsConnectedString());
        if (viewModel.getIsConnected()) {
            isConnectedTextView.setTextColor(Color.parseColor("#9cff57"));
        } else {
            isConnectedTextView.setTextColor(Color.parseColor("#ff7539"));
        }

        isBlocked = viewModel.getIsBlocked();
        if (isBlocked) {
            isConnectedTextView.setText("Blocked");
            isConnectedTextView.setTextColor(Color.parseColor("#c30000"));
            ipAddressTextView.setText("");
            blockButton.setImageResource(R.drawable.ic_round_unblock_24px);
        } else {
            blockButton.setImageResource(R.drawable.ic_round_block_24px);
        }

        if (viewModel.getIpAddress().equals(JioFiPreferences.ipAddressString)) {
            blockButton.setVisibility(View.INVISIBLE);
        }

        mListener = onClickListener;
        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlocked) {
                    mListener.onClickUnBlockListener(id);
                } else {
                    mListener.onClickBlockListener(id);
                }
            }
        });
    }
}
