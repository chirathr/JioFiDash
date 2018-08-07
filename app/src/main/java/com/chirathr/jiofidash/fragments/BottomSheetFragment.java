package com.chirathr.jiofidash.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chirathr.jiofidash.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    public static final int OPTION_RESTART_ID = 0;
    public static final int OPTION_WIFI_SETTINGS_ID = 1;
    public static final int OPTION_ADMIN_WEB_UI = 2;
    public static final int OPTION_SETTINGS_ID = 3;
    public static final int OPTION_ABOUT_ID = 4;

    private onOptionSelectedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (onOptionSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onOptionSelectedListener");
        }
    }

    private TextView restartTextView;
    private TextView wifiSettingSTextView;
    private TextView adminTextView;
    private TextView settingsTextView;
    private TextView aboutTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_fragment_card, container, false);

        restartTextView = (TextView) view.findViewById(R.id.action_restart);
        wifiSettingSTextView = (TextView) view.findViewById(R.id.action_wifi_settings);
        adminTextView = (TextView) view.findViewById(R.id.action_open_admin_web_ui);
        settingsTextView = (TextView) view.findViewById(R.id.action_settings);
        aboutTextView = (TextView) view.findViewById(R.id.action_about);

        restartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOptionSelected(OPTION_RESTART_ID);
            }
        });

        adminTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOptionSelected(OPTION_ADMIN_WEB_UI);
            }
        });

        wifiSettingSTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOptionSelected(OPTION_WIFI_SETTINGS_ID);
            }
        });


        aboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOptionSelected(OPTION_ABOUT_ID);
            }
        });

        return view;
    }

    public interface onOptionSelectedListener {
        void onOptionSelected(int optionId);
    }
}
