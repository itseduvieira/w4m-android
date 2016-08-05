package br.eco.wash4me.activity.base;

import android.support.v7.app.AppCompatActivity;

public class W4MActivity extends AppCompatActivity {


    public W4MApplication getW4MApplication() {
        return (W4MApplication) getApplication();
    }
}
