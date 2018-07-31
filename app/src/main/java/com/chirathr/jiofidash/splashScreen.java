package com.chirathr.jiofidash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class splashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();

    AnimationDrawable launcherAnimation;

    @Override
    protected void onStart() {
        super.onStart();

        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView launcherIcon = (ImageView) findViewById(R.id.launcher_animated);
        launcherIcon.setBackgroundResource(R.drawable.ic_launcher_animated);
        launcherAnimation = (AnimationDrawable) launcherIcon.getBackground();

        launcherAnimation.start();

//        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }

    private void launch() {
        if (!isFinishing()) {
            startActivity(new Intent(this, MainActivity.class));
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
