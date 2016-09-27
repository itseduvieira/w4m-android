package br.eco.wash4me.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;

public class Splash extends W4MActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
