package com.daehee.smartel.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.widget.ImageView;

import com.daehee.smartel.R;

/**
 * Created by daehee on 2017. 9. 17..
 */

public class SplashActivity extends Activity {
    private String AUTO_LOGIN_INFO = "AUTO_LOGIN_INFO";
    private SharedPreferences prefs;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Fade fade = new Fade();
//            fade.excludeTarget(R.id.appBar, true);
//            fade.excludeTarget(android.R.id.statusBarBackground, true);
//            fade.excludeTarget(android.R.id.navigationBarBackground, true);
//
//            getWindow().setEnterTransition(fade);
//            getWindow().setExitTransition(fade);
//        }

        imageView = (ImageView) findViewById(R.id.ivLogo);

        if (isAutoLogin()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    // TODO 스플래시 액티비티 타이머 구현, 로그인 액티비티 구현
                }
            }, 1000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(SplashActivity.this,
                                    imageView,
                                    ViewCompat.getTransitionName(imageView));
                    startActivity(intent, options.toBundle());
                }
            }, 1000);
        } // end of if~else

    } // end of onCreate

    public boolean isAutoLogin() {
        boolean result = false;
        prefs = getSharedPreferences(AUTO_LOGIN_INFO, MODE_PRIVATE);
        result = prefs.getBoolean("AUTO_LOGIN", false);
        return result;
    } // end of isAutoLogin

    @Override
    public void onBackPressed() {
        // Do nothing.
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
} // end of class
