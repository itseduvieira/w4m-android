package br.eco.wash4me.activity.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.GregorianCalendar;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import br.eco.wash4me.entity.Account;
import br.eco.wash4me.entity.Car;
import br.eco.wash4me.entity.OrderRequest;
import br.eco.wash4me.entity.User;
import br.eco.wash4me.utils.FontOverride;

import static br.eco.wash4me.utils.Constants.*;

public class W4MApplication extends Application {
    private static W4MApplication w4mApplication;

    private User loggedUser;
    private OrderRequest orderRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(this);

        FontOverride.setDefaultFont(this, "SANS_SERIF", "fonts/brandon_med.otf");

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

    public User getLoggedUser(Context context) {
        return loggedUser == null ? getSavedUser(context) : loggedUser;
    }

    public void setLoggedUser(Context context, User loggedUser) {
        this.loggedUser = loggedUser;

        saveLoggedUser(context, loggedUser);
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

    public Account getAccount(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String credentialsJSON = settings.getString("account", null);
        Account credentials = null;

        if(credentialsJSON != null) {
            Gson json = new GsonBuilder().create();
            credentials = json.fromJson(credentialsJSON, Account.class);
        }

        return credentials;
    }

    public void saveAccount(Context context, Account credentials) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.remove("account");
        editor.putString("account", new GsonBuilder().create().toJson(credentials));

        editor.apply();
    }

    public OrderRequest getOrderRequest() {
        if(orderRequest == null) {
            orderRequest = new OrderRequest();
        }

        return orderRequest;
    }

    public void addCar(Context context, Car car) {
        User user = getLoggedUser(context);
        user.getMyCars().add(car);
        setLoggedUser(context, user);
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
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

    public void clearCurrentRequest() {
        setOrderRequest(null);
    }
}
