package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.utils.NetworkUtils;

public class OnBoarding extends AppCompatActivity {

    private static final String TAG = OnBoarding.class.getSimpleName();

    private Button jiofi6SelectButton;
    private Button olderJiofiWebUIButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        jiofi6SelectButton = findViewById(R.id.select_button_jiofi_6);
        olderJiofiWebUIButton = findViewById(R.id.select_button_other_jiofi);

        jiofi6SelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();

                JioFiPreferences.getInstance().setDevice(context, NetworkUtils.DEVICE_6_ID);
                startActivity(new Intent(context, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        olderJiofiWebUIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(NetworkUtils.DEFAULT_HOST);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else{
                    //Page not found
                    Log.v(TAG, "page not found, open web page.");
                }
            }
        });
    }
}
