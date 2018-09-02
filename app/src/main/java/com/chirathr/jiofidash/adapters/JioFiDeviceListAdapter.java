package com.chirathr.jiofidash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.data.JioFiDeviceViewModel;
import com.chirathr.jiofidash.viewholders.JioFiDeviceViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JioFiDeviceListAdapter extends RecyclerView.Adapter<JioFiDeviceViewHolder> {

    private List<JioFiDeviceViewModel> jioFiDeviceViewModelList;
    private JioFiDeviceOnClickLisetner mListener;
    private Context mContext;

    public JioFiDeviceListAdapter(
            Context context,
            JioFiDeviceOnClickLisetner onClickListener, List<JioFiDeviceViewModel> viewModelList) {
        jioFiDeviceViewModelList = viewModelList;
        mListener = onClickListener;
        mContext = context;
    }

    @NonNull
    @Override
    public JioFiDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_select_card, parent, false);
        return new JioFiDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JioFiDeviceViewHolder holder, int position) {
        holder.bind(mContext, jioFiDeviceViewModelList.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        if (jioFiDeviceViewModelList == null)
            return 0;
        return jioFiDeviceViewModelList.size();
    }

    public void setJioFiDeviceViewModelList(List<JioFiDeviceViewModel> viewModelList) {
        jioFiDeviceViewModelList = viewModelList;
        notifyDataSetChanged();
    }

    public interface JioFiDeviceOnClickLisetner {
        void selectDevice(int deviceId);
    }

}
