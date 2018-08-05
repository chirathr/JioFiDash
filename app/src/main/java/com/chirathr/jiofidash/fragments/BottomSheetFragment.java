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
    public static final int OPTION_SETTINGS_ID = 2;
    public static final int OPTION_ABOUT_ID = 3;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_fragment_card, container, false);

        restartTextView = (TextView) view.findViewById(R.id.action_restart);

        restartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOptionSelected(OPTION_RESTART_ID);
            }
        });

        return view;
    }

    public interface onOptionSelectedListener {
        void onOptionSelected(int optionId);
    }
}
