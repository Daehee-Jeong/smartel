package com.daehee.smartel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.daehee.smartel.R;

/**
 * Created by daehee on 2017. 9. 17..
 */

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    } // end of onCreate

    @Override
    public void onBackPressed() {
        finish();
    }
} // end of class
