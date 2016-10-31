package br.eco.wash4me.activity.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.lang.reflect.Field;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.ChatActivity;
import br.eco.wash4me.activity.LoginActivity;
import br.eco.wash4me.activity.MyOrdersActivity;
import br.eco.wash4me.activity.SettingsActivity;
import br.eco.wash4me.activity.StepsActivity;
import br.eco.wash4me.activity.SuppliersActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.eco.wash4me.activity.base.W4MApplication.log;

public class W4MActivity extends AppCompatActivity {
    private static ProgressDialog progressDialog;

    protected Context context = this;
    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected CircleImageView profileImage;
    protected TextView userName;
    protected TextView userEmail;
    protected Boolean closeHome = false;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if(closeHome) {
                    finish();
                } else {
                    hideKeyboard(toolbar.getApplicationWindowToken());

                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void bindViews() { };

    protected void setupViews() { };

    protected void setupToolbarMenu() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        closeHome = false;

        changeToolbarTypeface(toolbar);
    }

    protected void setupToolbarBack() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        closeHome = true;

        changeToolbarTypeface(toolbar);
    }

    protected void setupToolbarBack(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);

        closeHome = true;

        changeToolbarTypeface(toolbar);
    }

    protected void setupToolbarClose() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        closeHome = true;

        changeToolbarTypeface(toolbar);
    }

    protected void changeToolbarTypeface(Toolbar toolbar) {
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            TextView title = ((TextView) f.get(toolbar));
            if(title != null) {
                title.setTypeface(Typeface.createFromAsset(context.getAssets(),
                        "fonts/brandon_med.otf"));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void setupNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View hView =  navigationView.getHeaderView(0);
        profileImage = (CircleImageView) hView.findViewById(R.id.profile_image);
        userName = (TextView) hView.findViewById(R.id.name_user);
        userEmail = (TextView) hView.findViewById(R.id.email_user);

        Bitmap bitmap = getW4MApplication().getLoggedUser(context).getProfilePicture();

        if(bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.example_profile);
        }

        profileImage.setImageBitmap(bitmap);
        userName.setText(getW4MApplication().getLoggedUser(context).getName());
        userEmail.setText(getW4MApplication().getLoggedUser(context).getEmail());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                switch(id) {
                    case R.id.navigation_item_settings:
                        startActivity(new Intent(context, SettingsActivity.class));
                        break;
                    case R.id.navigation_item_new_order:
                        startActivity(new Intent(context, StepsActivity.class));
                        break;
                    case R.id.navigation_item_my_orders:
                        startActivity(new Intent(context, MyOrdersActivity.class));
                        break;
                    case R.id.navigation_item_suppliers:
                        startActivity(new Intent(context, SuppliersActivity.class));
                        break;
                    case R.id.navigation_item_chat:
                        startActivity(new Intent(context, ChatActivity.class));
                        break;
                    case R.id.navigation_item_exit:
                        logout();
                        break;
                    default:
                        Snackbar.make(drawerLayout, menuItem.getTitle(), Snackbar.LENGTH_LONG).show();
                }

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

            return;
        }

        super.onBackPressed();
    }

    protected void logout() {
        getW4MApplication().setLoggedUser(context, null);
        getW4MApplication().clearDebugInformation(context);

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }

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
