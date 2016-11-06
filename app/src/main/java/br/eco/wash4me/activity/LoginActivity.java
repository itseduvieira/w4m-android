package br.eco.wash4me.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import br.eco.wash4me.R;
import br.eco.wash4me.activity.base.W4MActivity;
import br.eco.wash4me.entity.Account;
import br.eco.wash4me.entity.User;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.activity.base.W4MApplication.log;
import static br.eco.wash4me.data.DataAccess.getDataAccess;

public class LoginActivity extends W4MActivity {
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mPasswordSignupView;
    private TextInputEditText mPasswordConfirmSignupView;
    private View mLoginFormView;
    private View loginUserType;
    private Button mEmailSignInButton;
    private Button btnVisitor;
    private Button btnMember;
    private LoginButton loginButton;
    private LoginButton loginButtonSignup;
    private RelativeLayout mainView;
    private Account credendials;
    private View forgotPassForm;
    private View btnShowForgotPass;
    private Button btnShowSignup;
    private Button btnSignup;

    private CallbackManager callbackManagerLogin;
    private CallbackManager callbackManagerSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        bindViews();

        setupToolbarBack();

        setupViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManagerLogin.onActivityResult(requestCode, resultCode, data);
        callbackManagerSignup.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (mLoginFormView.getVisibility() == View.VISIBLE) {
                    mLoginFormView.setVisibility(View.GONE);
                    loginUserType.setVisibility(View.VISIBLE);

                    btnShowSignup.setVisibility(View.VISIBLE);

                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else if (forgotPassForm.getVisibility() == View.VISIBLE) {
                    forgotPassForm.setVisibility(View.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);

                    btnShowSignup.setVisibility(View.VISIBLE);

                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    if(AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                } else if (findViewById(R.id.signup_form).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.signup_form).setVisibility(View.GONE);
                    loginUserType.setVisibility(View.VISIBLE);

                    btnShowSignup.setVisibility(View.VISIBLE);

                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindViews() {
        mLoginFormView = findViewById(R.id.login_form);
        loginUserType = findViewById(R.id.user_type_form);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordSignupView = (TextInputEditText) findViewById(R.id.password_signup);
        mPasswordConfirmSignupView = (TextInputEditText) findViewById(R.id.password_confirm_signup);
        mEmailView = (TextInputEditText) findViewById(R.id.email);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        btnVisitor = (Button) findViewById(R.id.btn_visitor);
        btnMember = (Button) findViewById(R.id.btn_member);
        callbackManagerLogin = CallbackManager.Factory.create();
        callbackManagerSignup = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButtonSignup = (LoginButton) findViewById(R.id.facebook_signup_button);
        mainView = (RelativeLayout) findViewById(R.id.login_main_view);
        credendials = getW4MApplication().getAccount(context);
        btnShowForgotPass = findViewById(R.id.btn_show_forgot_pass);
        forgotPassForm = findViewById(R.id.forgot_form);
        btnShowSignup = (Button) findViewById(R.id.btn_show_signup);
        btnSignup = (Button) findViewById(R.id.btn_signup);
    }

    @Override
    protected void setupViews() {
        mEmailView.requestFocus();

        getW4MApplication().clearCurrentRequest();

        mEmailView.setError(null);
        mPasswordView.setError(null);

        if (credendials != null) {
            mEmailView.setText(credendials.getUsername());
            mPasswordView.setText(credendials.getPassword());
        }

        mPasswordView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/brandon_med.otf"));
        mPasswordView.setTransformationMethod(new PasswordTransformationMethod());

        mPasswordSignupView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/brandon_med.otf"));
        mPasswordSignupView.setTransformationMethod(new PasswordTransformationMethod());

        mPasswordConfirmSignupView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/brandon_med.otf"));
        mPasswordConfirmSignupView.setTransformationMethod(new PasswordTransformationMethod());

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        btnVisitor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getW4MApplication().setLoggedUser(context, User.VISITOR);

                startActivity(new Intent(LoginActivity.this, StepsActivity.class));
            }
        });

        btnMember.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginFormView.setVisibility(View.VISIBLE);
                loginUserType.setVisibility(View.GONE);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        loginButton.registerCallback(callbackManagerLogin, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                loginWithFacebook();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        loginButtonSignup.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        loginButtonSignup.registerCallback(callbackManagerSignup, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getDataAccess().getFacebookLoginData(context, new Callback<User>() {
                    @Override
                    public void execute(User user) {
                        ((TextView) findViewById(R.id.name_signup)).setText(user.getName());
                        ((TextView) findViewById(R.id.email_signup)).setText(user.getEmail());
                        findViewById(R.id.password_signup).requestFocus();
                    }
                }, new Callback<Void>() {
                    @Override
                    public void execute(Void aVoid) {
                        log("[setupViews.registerCallback] ERROR at getting data from Facebook");
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

        btnShowForgotPass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.login_form).setVisibility(View.GONE);
                findViewById(R.id.forgot_form).setVisibility(View.VISIBLE);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        btnShowSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignupForm();
            }
        });

        btnSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress("Fazendo Cadastro...", mEmailView.getWindowToken(), mPasswordView.getWindowToken());

                User user = new User();

                getDataAccess().doSignup(context, user, new Callback<User>() {
                    @Override
                    public void execute(User newUser) {
                        hideProgress();

                        Account account = new Account();
                        account.setUsername(newUser.getEmail());

                        getW4MApplication().saveAccount(context, account);

                        getW4MApplication().setLoggedUser(context, newUser);

                        finish();

                        startActivity(new Intent(LoginActivity.this, StepsActivity.class));
                    }
                }, new Callback<Void>() {
                    @Override
                    public void execute(Void aVoid) {
                        log("[btnSignup.onClick] ERROR at sign up");
                    }
                });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if(AccessToken.getCurrentAccessToken() != null) {
            loginWithFacebook();
        }
    }

    private void loginWithFacebook() {
        getDataAccess().getFacebookLoginData(context, new Callback<User>() {
            @Override
            public void execute(final User facebookUser) {
                Account account = new Account();
                account.setUsername(facebookUser.getEmail());
                account.setPassword(facebookUser.getFacebookId());

                getDataAccess().doFacebookLogin(context, account, new Callback<User>() {
                    @Override
                    public void execute(User user) {
                        getW4MApplication().setLoggedUser(context, user);

                        finish();

                        startActivity(new Intent(LoginActivity.this, StepsActivity.class));
                    }
                }, new Callback<Void>() {
                    @Override
                    public void execute(Void aVoid) {
                        Snackbar.make(findViewById(R.id.toolbar), "Complete seu cadastro no formulário acima.", Snackbar.LENGTH_LONG).show();

                        showSignupForm();

                        ((TextView) findViewById(R.id.name_signup)).setText(facebookUser.getName());
                        ((TextView) findViewById(R.id.email_signup)).setText(facebookUser.getEmail());
                        findViewById(R.id.password_signup).requestFocus();
                    }
                });
            }
        }, new Callback<Void>() {
            @Override
            public void execute(Void aVoid) {
                log("[setupViews.registerCallback] ERROR at getting data from Facebook");
            }
        });
    }

    private void showSignupForm() {
        findViewById(R.id.login_form).setVisibility(View.GONE);
        findViewById(R.id.forgot_form).setVisibility(View.GONE);
        findViewById(R.id.user_type_form).setVisibility(View.GONE);
        findViewById(R.id.signup_form).setVisibility(View.VISIBLE);
        btnShowSignup.setVisibility(View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

            getW4MApplication().saveAccount(context, account);

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

                getW4MApplication().setLoggedUser(context, user);

                finish();

                startActivity(new Intent(LoginActivity.this, StepsActivity.class));
            }
        }, new Callback<Void>() {
            @Override
            public void execute(Void aVoid) {
                hideProgress();

                Snackbar.make(mainView, "Usuário ou senha inválidos.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENTENDI", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .show();
            }
        });
    }


}

