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
import android.widget.EditText;
import android.widget.ProgressBar;

import com.chirathr.jiofidash.R;
import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.utils.NetworkUtils;

import androidx.fragment.app.DialogFragment;

public class LoginDialog extends DialogFragment {

    private static final String TAG = LoginDialog.class.getSimpleName();

    private LoginTask loginTask;

    private ProgressBar progressBar;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private String actionToExcecuteAfterLogin = null;
    private LoginCompleteListener mLoginCompleteListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.login_card, null);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        usernameEditText = view.findViewById(R.id.ssid_input);
        passwordEditText = view.findViewById(R.id.password_input);

        JioFiPreferences preferences = JioFiPreferences.getInstance();

        if (preferences.isLoginDataAvailable(getContext())) {
            usernameEditText.setText(preferences.username);
            passwordEditText.setText(preferences.password);
        }

        builder.setView(view)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            Button positiveButton = (Button) alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            hideLoading();
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    JioFiPreferences preferences = JioFiPreferences.getInstance();
                    preferences.setUsernameAndPassword(username, password);
                    preferences.saveUsernameAndPassword(getContext());

                    Log.v(TAG, username + " " + password);

                    loginTask = new LoginTask();
                    loginTask.execute();
                }
            });
        }
    }

    public void closeDialog() {
        dismiss();
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {

        private boolean loginSuccessfull;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();

            Context context = getContext();

            if (context != null && !NetworkUtils.isOnline(context)) {
                cancel(true);
                closeDialog();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            loginSuccessfull = NetworkUtils.login(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (loginSuccessfull) {
                closeDialog();
                mLoginCompleteListener.loginCompleteListener(actionToExcecuteAfterLogin);
            }
            else {
                hideLoading();
                if (NetworkUtils.authenticationError) {
                    displayAuthError();
                }
            }
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void displayAuthError() {
        // TODO Make strings as constants
        usernameEditText.setError("Username or password wrong");
        passwordEditText.setError("Username or password wrong");
    }

    public void setActionAfterLogin(LoginCompleteListener context, String action) {
        mLoginCompleteListener = context;
        actionToExcecuteAfterLogin = action;
    }

    public interface LoginCompleteListener {
        public void loginCompleteListener(String action);
    }
}
