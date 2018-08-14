package com.chirathr.jiofidash.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.chirathr.jiofidash.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class RestartWiFiDialogFragment extends DialogFragment {
    private RestartWiFiConfirmListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.restart_dialog_message)
                .setPositiveButton(R.string.restart_dialog_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.restartWiFiConfirmListener();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.restartWiFiCancelListener();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (RestartWiFiConfirmListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement OnChangeSSIDCompleteListener.");
        }
    }

    public interface RestartWiFiConfirmListener {
        void restartWiFiConfirmListener();
        void restartWiFiCancelListener();
    }
}
