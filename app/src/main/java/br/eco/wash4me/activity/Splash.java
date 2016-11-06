package br.eco.wash4me.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.User;

public class Splash extends W4MActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (getW4MApplication().isLogged(context) &&
                getW4MApplication().getLoggedUser(context).getType().equals(User.Type.MEMBER)) {
            startActivity(new Intent(Splash.this, StepsActivity.class));

            Splash.this.finish();
        } else {
            getW4MApplication().setLoggedUser(context, null);

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
