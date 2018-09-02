package com.chirathr.jiofidash.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.adapters.JioFiDeviceListAdapter;
import com.chirathr.jiofidash.data.JioFiDeviceViewModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JioFiDeviceViewHolder extends RecyclerView.ViewHolder {

    private JioFiDeviceListAdapter.JioFiDeviceOnClickLisetner mListener;

    private ImageView deviceImageView;
    private TextView deviceNameTextView;
    private TextView descriptionTextView;
    private Button deviceSelectButton;

    public JioFiDeviceViewHolder(@NonNull View itemView) {
        super(itemView);
        deviceImageView = itemView.findViewById(R.id.jiofiDeviceImage);
        deviceNameTextView = itemView.findViewById(R.id.jioFiName);
        descriptionTextView = itemView.findViewById(R.id.jioFiDescription);
        deviceSelectButton = itemView.findViewById(R.id.jioFiSelectButton);
    }

    public void bind(final JioFiDeviceViewModel viewModel, JioFiDeviceListAdapter.JioFiDeviceOnClickLisetner listener) {
        mListener = listener;

        deviceImageView.setImageDrawable(viewModel.getDeviceImage());
        deviceNameTextView.setText(viewModel.getDeviceName());
        descriptionTextView.setText(viewModel.getDescription());

        if (viewModel.isSupported()) {
            deviceSelectButton.setText(R.string.select_button_text);
        } else {
            deviceSelectButton.setText(R.string.web_ui_button_text);
        }

        deviceSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.selectDevice(viewModel.getDeviceId());
            }
        });
    }
}
