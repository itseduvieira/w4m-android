package br.eco.wash4me.activity.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.GregorianCalendar;

import br.eco.wash4me.entity.User;

import static br.eco.wash4me.utils.Constants.*;

public class W4MApplication extends Application {
    private static W4MApplication w4MApplication;

    private User loggedUser;

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

    public void clearDebugInformation(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.remove("qtdRequests");
        editor.remove("loginDate");

        editor.apply();
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public Boolean isLogged() {
        return getLoggedUser() != null;
    }

    public String getWsUrl() {
        //Boolean local = isLogged() &&
        //        getLoggedUser().getEmail().contains("@stg.wash4me.eco.br");

        //if(local) {
        //    return "http://52.43.52.108/api/v1";
        //} else {
            return "http://adm.wash4me.eco.br/api/v1";
        //}
    }

    public static void log(String msg) {
        Log.i("w4m.app.general", msg);
    }
}
