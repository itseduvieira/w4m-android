package br.eco.wash4me.activity.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class W4MActivity extends AppCompatActivity {
    protected Context context = this;

    public W4MApplication getW4MApplication() {
        return (W4MApplication) getApplication();
    }

    protected void log(String msg) {
        Log.d("w4m.app", msg);
    }
}
