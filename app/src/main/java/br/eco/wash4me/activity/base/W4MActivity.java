package br.eco.wash4me.activity.base;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.eco.wash4me.activity.LoginActivity;

public class W4MActivity extends AppCompatActivity {
    protected Context context = this;

    public W4MApplication getW4MApplication() {
        return (W4MApplication) getApplication();
    }

    protected void logout() {
        getW4MApplication().setLoggedUser(null);
        getW4MApplication().clearDebugInformation(context);

        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    protected void log(String msg) {
        Log.d("w4m.app", msg);
    }
}
