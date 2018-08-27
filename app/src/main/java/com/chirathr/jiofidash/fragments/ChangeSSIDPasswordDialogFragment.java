package com.chirathr.jiofidash.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.utils.NetworkUtils;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ChangeSSIDPasswordDialogFragment extends DialogFragment {

    private static final String TAG = ChangeSSIDPasswordDialogFragment.class.getSimpleName();

    private TextInputEditText ssidEditText;
    private TextInputEditText passwordEditText;
    private ProgressBar loadingProgressBar;
    private TextView tooManyAttemptsTextView;

    private LoadSSIDPasswordTask loadSSIDPasswordTask;

    private OnChangeSSIDCompleteListener mListener;

    public static final String FRGAMENT_TAG = "change-ssid-password-fragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.change_ssid_password_dialog, null);

        ssidEditText = view.findViewById(R.id.ssid_input);
        passwordEditText = view.findViewById(R.id.password_input);
        loadingProgressBar = view.findViewById(R.id.progressBar);
        tooManyAttemptsTextView = view.findViewById(R.id.tv_error_too_many_attempts);

        builder.setView(view)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog alertDialog = (AlertDialog) getDialog();

        if (alertDialog != null) {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tooManyAttemptsTextView.setVisibility(View.GONE);
                    if (ssidEditText.getText().toString().isEmpty()) {
                        ssidEditText.setError(getString(R.string.login_error_blank));
                    } else if (passwordEditText.getText().toString().isEmpty()) {
                        passwordEditText.setError(getString(R.string.login_error_blank));
                    } else {
                        new SetSSIDPasswordTask().execute();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSSIDPasswordTask = new LoadSSIDPasswordTask();
        loadSSIDPasswordTask.execute();
        tooManyAttemptsTextView.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnChangeSSIDCompleteListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement OnChangeSSIDCompleteListener.");
        }
    }

    private class LoadSSIDPasswordTask extends AsyncTask<Void, Void, Void> {

        private boolean isSuccessful;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "Loading");
            showLoading();
            isSuccessful = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            isSuccessful = NetworkUtils.loadCurrentSSIDAndPassword(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                ssidEditText.setText(NetworkUtils.wiFiSSID);
                passwordEditText.setText(NetworkUtils.wiFiPassword);
                hideLoading();
            } catch (Exception ignore) { }
            if (!isSuccessful) {
                mListener.onLoadSSIDFailedListener();
                dismiss();
            }

        }
    }

    private class SetSSIDPasswordTask extends AsyncTask<Void, Void, Void> {

        private boolean isSuccessful;
        private String SSIDName;
        private String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SSIDName = ssidEditText.getText().toString();
            password = passwordEditText.getText().toString();
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            isSuccessful = NetworkUtils.changeSSIDAndPassword(getActivity(), SSIDName, password);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideLoading();
            if (isSuccessful) {
                mListener.onChangeSSIDCompleteListener();
                try {
                    dismiss();
                } catch (Exception ignored) { }
            } else {
                try {
                    tooManyAttemptsTextView.setVisibility(View.VISIBLE);
                } catch (Exception ignored) { }
            }
        }
    }

    public void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
    }

    public interface OnChangeSSIDCompleteListener {
        void onLoadSSIDFailedListener();
        void onChangeSSIDCompleteListener();
    }
}
