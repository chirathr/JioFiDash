package com.chirathr.jiofidash.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.data.DeviceViewModel;
import com.chirathr.jiofidash.viewholders.DeviceViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private List<DeviceViewModel> viewModels;

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices_list_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bindData(viewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public void setDeviceViewModels(List<DeviceViewModel> models) {
        viewModels = models;
    }
}