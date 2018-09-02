package com.chirathr.jiofidash.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.chirathr.jiofidash.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ErrorSelectingDeviceDialogFragment extends DialogFragment {

    private ErrorSelectingButtonListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.error_selecting_device_dialog_message)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onRetrySelectListener();
                    }
                }).setNegativeButton(R.string.select_another, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onSelectAnotherListener();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ErrorSelectingButtonListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement the ErrorSelectingButtonListener");
        }
    }

    public interface ErrorSelectingButtonListener {
        void onRetrySelectListener();
        void onSelectAnotherListener();
    }
}
