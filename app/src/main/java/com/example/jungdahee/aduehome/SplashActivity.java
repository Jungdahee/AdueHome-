package com.example.jungdahee.aduehome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Jung dahee on 2017-08-21.
 */

public class SplashActivity extends Activity{
    protected void onCreate(Bundle savedlnstanceState){
        super.onCreate(savedlnstanceState);
        setContentView(R.layout.splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();

            }
        }, 3000);
    }
}
