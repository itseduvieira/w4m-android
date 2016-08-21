package br.eco.wash4me.activity.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.GregorianCalendar;

import static br.eco.wash4me.utils.Constants.*;

public class W4MApplication extends Application {
    private static W4MApplication w4MApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        w4MApplication = this;
    }

    public static W4MApplication getInstance() {
        return w4MApplication;
    }

    public Integer getQtdRequestsDebug(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return settings.getInt("qtdRequests", 0);
    }

    public Long getLoginDateDebug(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return settings.getLong("loginDate", 0);
    }

    public void setDebugInformation(Context context, Integer qtdRequests, GregorianCalendar loginDate) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.remove("qtdRequests");
        editor.putInt("qtdRequests", qtdRequests);

        if(loginDate != null) {
            editor.remove("loginDate");
            editor.putLong("loginDate", loginDate.getTimeInMillis());
        }

        editor.apply();
    }

    public String getWsUrl() {
        return "http://localhost:3000/api";
    }
}
