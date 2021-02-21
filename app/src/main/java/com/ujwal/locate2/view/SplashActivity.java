package com.ujwal.locate2.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.ujwal.locate2.R;
import com.ujwal.locate2.view.LauncherActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    ImageView bgSplash, logoSplash;
    TextView txtSplash;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        bgSplash = findViewById(R.id.bgsplash);
        logoSplash = findViewById(R.id.logosplash);
        txtSplash = findViewById(R.id.txtsplash);
        lottieAnimationView = findViewById(R.id.lottieanim);

        bgSplash.animate().translationY(-2500).setDuration(1000).setStartDelay(5000);
        logoSplash.animate().translationY(2000).setDuration(1000).setStartDelay(5000);
        txtSplash.animate().translationY(1800).setDuration(1000).setStartDelay(5000);
        lottieAnimationView.animate().translationY(1800).setDuration(1000).setStartDelay(5000);
        new Timer().schedule(new TimerTask(){
            public void run() {
                startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
                finish();
            }
        }, 3200);

    }
//


    }
