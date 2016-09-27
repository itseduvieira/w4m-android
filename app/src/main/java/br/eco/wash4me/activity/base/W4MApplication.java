package br.eco.wash4me.activity.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.GregorianCalendar;

import br.eco.wash4me.entity.User;

import static br.eco.wash4me.utils.Constants.*;

public class W4MApplication extends Application {
    private static W4MApplication w4mApplication;

    private User loggedUser;

    @Override
    public void onCreate() {
        super.onCreate();

        w4mApplication = this;
    }

    public static W4MApplication getInstance() {
        return w4mApplication;
    }

    public Integer getQtdRequestsDebug(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return settings.getInt("qtdRequests", 0);
    }

    public Long getLoginDateDebug(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return settings.getLong("loginDate", 0);
    }

    private User getSavedUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String userJSON = settings.getString("user", null);
        User user = null;

        if(userJSON != null) {
            Gson json = new GsonBuilder().create();
            user = json.fromJson(userJSON, User.class);
        }

        return user;
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

    private void saveLoggedUser(Context context, User user) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.remove("user");
        editor.putString("user", new GsonBuilder().create().toJson(user));

        editor.apply();
    }

    public void clearDebugInformation(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.remove("qtdRequests");
        editor.remove("loginDate");

        editor.apply();
    }

    public User getLoggedUser(Context context) {
        return loggedUser == null ? getSavedUser(context) : loggedUser;
    }

    public void setLoggedUser(Context context, User loggedUser) {
        this.loggedUser = loggedUser;

        saveLoggedUser(context, loggedUser);
    }

    public Boolean isLogged(Context context) {
        return getLoggedUser(context) != null;
    }

    public String getWsUrl() {
        return getBaseUrl() + "/api/v1";
    }

    public String getBaseUrl() {
        return "http://adm.wash4me.eco.br";
    }

    public static void log(String msg) {
        Log.i("w4m.app.general", msg);
    }
}
