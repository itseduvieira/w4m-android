package br.eco.wash4me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.Account;
import br.eco.wash4me.entity.User;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.activity.base.W4MApplication.log;
import static br.eco.wash4me.data.DataAccess.getDataAccess;

public class LoginActivity extends W4MActivity {
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View loginUserType;
    private Button mEmailSignInButton;
    private Button btnVisitor;
    private Button btnMember;
    private LoginButton loginButton;
    private LinearLayout mainView;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(getW4MApplication());

        setContentView(R.layout.activity_login);

        bindViews();

        setupViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void bindViews() {
        mLoginFormView = findViewById(R.id.login_form);
        loginUserType = findViewById(R.id.login_user_type);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        btnVisitor = (Button) findViewById(R.id.btn_visitor);
        btnMember = (Button) findViewById(R.id.btn_member);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        mainView = (LinearLayout) findViewById(R.id.login_main_view);
    }

    private void setupViews() {
        mEmailView.requestFocus();

        mEmailView.setError(null);
        mPasswordView.setError(null);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        btnVisitor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                startActivity(new Intent(LoginActivity.this, ProductsActivity.class));
            }
        });

        btnMember.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginFormView.setVisibility(View.VISIBLE);
                loginUserType.setVisibility(View.GONE);
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getDataAccess().getFacebookLoginData(context, new Callback<User>() {
                    @Override
                    public void execute(User user) {
                        getW4MApplication().setLoggedUser(user);

                        finish();

                        startActivity(new Intent(LoginActivity.this, MyOrdersActivity.class));
                    }
                }, new Callback<Void>() {
                    @Override
                    public void execute(Void aVoid) {
                        log("[setupViews.registerCallback] ERROR at get data from Facebook");
                    }
                });

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress("Fazendo Login...", mEmailView.getWindowToken(), mPasswordView.getWindowToken());

            Account account = new Account();
            account.setUsername(email);
            account.setPassword(password);

            doLogin(account);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void doLogin(Account account) {
        getDataAccess().doLogin(context, account, new Callback<User>() {
            @Override
            public void execute(User user) {
                hideProgress();

                getW4MApplication().setLoggedUser(user);

                finish();

                startActivity(new Intent(LoginActivity.this, MyOrdersActivity.class));
            }
        }, new Callback<Void>() {
            @Override
            public void execute(Void aVoid) {
                hideProgress();

                Snackbar.make(mainView, "Usuário ou senha inválidos.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENTENDI", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) { }
                        })
                        .show();
            }
        });
    }
}

