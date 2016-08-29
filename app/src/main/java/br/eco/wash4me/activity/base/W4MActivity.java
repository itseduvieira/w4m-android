package br.eco.wash4me.activity.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import br.eco.wash4me.activity.LoginActivity;

import static br.eco.wash4me.activity.base.W4MApplication.log;

public class W4MActivity extends AppCompatActivity {
    private static ProgressDialog progressDialog;

    protected Context context = this;

    public W4MApplication getW4MApplication() {
        return (W4MApplication) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("w4m.app.lifecycle", "[" + getClass().getSimpleName() + ".onCreate] onCreate called");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("w4m.app.lifecycle", "[" + getClass().getSimpleName() + ".onResume] onResume called");

        //context.registerReceiver(networkChange, new IntentFilter(ACTION_CONNECTIVITY_CHANGE));
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("w4m.app.lifecycle", "[" + getClass().getSimpleName() + ".onPause] onPause called");

        //context.unregisterReceiver(networkChange);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("w4m.app.lifecycle", "[" + getClass().getSimpleName() + ".onRestart] onRestart called");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("w4m.app.lifecycle", "[" + getClass().getSimpleName() + ".onStart] onStart called");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("w4m.app.lifecycle", "[" + getClass().getSimpleName() + ".onStop] onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("w4m.app.lifecycle", "[" + getClass().getSimpleName() + ".onDestroy] onDestroy called");
    }

    protected void logout() {
        getW4MApplication().setLoggedUser(null);
        getW4MApplication().clearDebugInformation(context);

        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void showProgress(String message, IBinder... tokens) {
        for (IBinder token : tokens) {
            hideKeyboard(token);
        }

        hideProgress();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static boolean isProgressShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }

    public static void hideProgress() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception ex) {
                log("[MBActivity.toggleProgressOff] Dialog Error: " + ex.getMessage());
            }
        }
    }

    public void hideKeyboard(IBinder key) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(key, 0);
    }
}
