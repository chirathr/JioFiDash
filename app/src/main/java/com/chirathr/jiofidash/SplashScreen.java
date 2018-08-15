package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.chirathr.jiofidash.data.JioFiPreferences;
import com.chirathr.jiofidash.utils.NetworkUtils;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();

    AnimationDrawable launcherAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initial setup that loads the device id and saved username and password.
        JioFiPreferences jioFiPreferences = JioFiPreferences.getInstance();
        jioFiPreferences.loadDeviceId(this);
        jioFiPreferences.loadUsernameAndPassword(this);

        // Animated icon
        ImageView launcherIcon = (ImageView) findViewById(R.id.launcher_animated);
        launcherIcon.setBackgroundResource(R.drawable.ic_launcher_animated);
        launcherAnimation = (AnimationDrawable) launcherIcon.getBackground();

        // show animation
        launcherAnimation.start();

        // Launch main activity after a time out to show animated logo
        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }

    private void launch() {
        if (!isFinishing()) {
            if (JioFiPreferences.currentDeviceId == NetworkUtils.DEVICE_NOT_SET_ID) {
                startActivity(new Intent(this, OnBoarding.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
