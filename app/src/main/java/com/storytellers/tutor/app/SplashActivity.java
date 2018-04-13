package com.storytellers.tutor.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoImage = findViewById(R.id.logo);

        Animation startAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_with_alpha);
        logoImage.startAnimation(startAnim);

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, findViewById(R.id.logo), findViewById(R.id.logo).getTransitionName()).toBundle();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent, bundle);
                }
        }, 2500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
