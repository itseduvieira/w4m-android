package br.eco.wash4me.activity.base;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.lang.reflect.Field;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.ChatActivity;
import br.eco.wash4me.activity.LoginActivity;
import br.eco.wash4me.activity.MyOrdersActivity;
import br.eco.wash4me.activity.PaymentActivity;
import br.eco.wash4me.activity.ProfileActivity;
import br.eco.wash4me.activity.StepsActivity;
import br.eco.wash4me.activity.SuppliersActivity;
import br.eco.wash4me.entity.User;
import br.eco.wash4me.utils.Callback;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.eco.wash4me.activity.base.W4MApplication.log;

public class W4MActivity extends AppCompatActivity {
    private static ProgressDialog progressDialog;

    private static final int TEXT_DIALOG_PADDING = 25;
    private static final int DIALOG_TEXT_SIZE = 17;
    public static final int MY_PERMISSIONS = 1;

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
        Boolean visitor = getW4MApplication().getLoggedUser(context).getType().equals(User.Type.VISITOR);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if(visitor) {
            View header = getLayoutInflater().inflate(R.layout.drawer_header_visitor, navigationView, false);
            navigationView.addHeaderView(header);
            navigationView.inflateMenu(R.menu.drawer_visitor);
            profileImage = (CircleImageView) header.findViewById(R.id.profile_image);
            Button btnSignIn = (Button) header.findViewById(R.id.btn_visitor_signin);
            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    drawerLayout.closeDrawers();

                    int id = menuItem.getItemId();

                    switch (id) {
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
        } else {
            View header = getLayoutInflater().inflate(R.layout.drawer_header, navigationView, false);
            navigationView.addHeaderView(header);
            navigationView.inflateMenu(R.menu.drawer);
            profileImage = (CircleImageView) header.findViewById(R.id.profile_image);
            userName = (TextView) header.findViewById(R.id.name_user);
            userEmail = (TextView) header.findViewById(R.id.email_user);

            userName.setText(getW4MApplication().getLoggedUser(context).getName());
            userEmail.setText(getW4MApplication().getLoggedUser(context).getUsername());

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    drawerLayout.closeDrawers();

                    int id = menuItem.getItemId();

                    switch (id) {
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
                        case R.id.navigation_item_profile:
                            startActivity(new Intent(context, ProfileActivity.class));
                            break;
                        case R.id.navigation_item_finance:
                            startActivity(new Intent(context, PaymentActivity.class));
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

        getW4MApplication().getProfilePicture(context, new Callback<Bitmap>() {
            @Override
            public void execute(Bitmap bitmap) {
                profileImage.setImageBitmap(bitmap);
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
        getW4MApplication().clearCurrentRequest();
        getW4MApplication().clearProfilePicture();
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
                log("[W4MActivity.toggleProgressOff] Dialog Error: " + ex.getMessage());
            }
        }
    }

    public void hideKeyboard(IBinder key) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(key, 0);
    }

    protected void showOkAlert(String msg) {
        getBasicAlert(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create().show();
    }

    protected void showOkAlert(String msg, DialogInterface.OnClickListener action) {
        getBasicAlert(msg)
                .setPositiveButton("OK", action)
                .create().show();
    }

    private AlertDialog.Builder getBasicAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
                .setView(getDialogView(msg))
                .setCancelable(false);

        return alertDialogBuilder;
    }

    private View getDialogView(String message) {
        final TextView input = new TextView(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        int padding = applyDimensionDIP(TEXT_DIALOG_PADDING);
        input.setText(message);
        input.setLayoutParams(lp);
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIALOG_TEXT_SIZE);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout linear = new LinearLayout(context);
        linear.setPadding(padding, padding, padding, 0);
        linear.setLayoutParams(lp);
        linear.addView(input);

        return linear;
    }

    protected Integer applyDimensionDIP(Integer value) {
        return Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics())).intValue();
    }

    protected Boolean hasPermissions() {
        int permissionCheckCoarse = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionCheckFine = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        Boolean hasPermission = permissionCheckCoarse == PackageManager.PERMISSION_GRANTED &&
                permissionCheckFine == PackageManager.PERMISSION_GRANTED;

        return hasPermission;
    }

    protected Boolean checkPermissions() {
        int permissionCheckCoarse = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionCheckFine = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        Boolean hasPermission = permissionCheckCoarse == PackageManager.PERMISSION_GRANTED &&
                permissionCheckFine == PackageManager.PERMISSION_GRANTED;

        if (!hasPermission) {
            showOkAlert("O aplicativo PRECISA da sua permissão para funcionar corretamente.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestPermissionsAndRestart();
                }
            });
        }

        return hasPermission;
    }

    private void requestPermissionsAndRestart() {
        ActivityCompat.requestPermissions(this,
                new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION },
                MY_PERMISSIONS);
    }
}
