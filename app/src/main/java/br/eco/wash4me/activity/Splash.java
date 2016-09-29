package br.eco.wash4me.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;

public class Splash extends W4MActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(getW4MApplication());

        setContentView(R.layout.activity_splash);

        if (getW4MApplication().isLogged(context)) {
            startActivity(new Intent(Splash.this, StepsActivity.class));

            Splash.this.finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, LoginActivity.class));

                    Splash.this.finish();
                }
            }, 3000);
        }
    }
}
