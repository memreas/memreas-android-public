
package com.memreas.member;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.sax.handler.CommonHandler;
import com.memreas.sax.handler.LoginParser;

public class LoginActivity extends BaseActivity implements AnimationListener {

    private int loginCntr = 0;
    private EditText txtUserName;
    private EditText txtPassword;
    private String device_id;
    private ProgressBar progressBar;

    private final int AS_NONE = 0;
    private final int AS_BACK = 1;
    private final int AS_FORGOT = 2;
    private final int AS_LOGIN = 3;

    public Animation mAnim = null;

    public int state_Anim = AS_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_login);

        txtUserName = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        progressBar = (ProgressBar) findViewById(R.id.processBar);

        ((TextView) findViewById(R.id.lblUserName)).setTypeface(fontCentury);
        ((TextView) findViewById(R.id.lblPassword)).setTypeface(fontCentury);

        //DEV ONLY
        if (Common.ENV.equalsIgnoreCase("DEV")) {
            txtUserName.setText("jmeah1");
            txtPassword.setText("a123456");
        }

        mAnim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.button_click);
        mAnim.setAnimationListener(this);
        if (getIntent().getBooleanExtra("is_registered", false)) {
            userNotationAlert(R.string.alert_verify_email);
        }
    }

    public void UserLogin(View v) {

        state_Anim = AS_LOGIN;
        v.startAnimation(mAnim);

        // hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                txtPassword.getWindowToken(),
                0);

    }

    public void ForgotUserPass(View v) {

        state_Anim = AS_FORGOT;
        v.startAnimation(mAnim);
    }

    public void back(View v) {

        state_Anim = AS_BACK;
        v.startAnimation(mAnim);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        loginCntr = 0;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),
                MainLoginSelectionActivity.class));
        finish();
    }

    @Override
    public void onAnimationEnd(Animation animation) {

        if (state_Anim == AS_BACK) {

            startActivity(new Intent(getApplicationContext(),
                    MainLoginSelectionActivity.class));
            finish();
        }

        if (state_Anim == AS_FORGOT) {

            startActivity(new Intent(getApplicationContext(),
                    ForgotPasswordActivity.class));
        }

        if (state_Anim == AS_LOGIN) {

            String userName = txtUserName.getText().toString();
            String password = txtPassword.getText().toString();

            if ((!(userName.equals(""))) && (!(password.equals("")))) {
                if (mApplication.isOnline()) {
                    CommonHandler.commonList = null;
                    progressBar.setVisibility(View.VISIBLE);
                    device_id = Secure.getString(this.getContentResolver(),
                            Secure.ANDROID_ID);
                    SessionManager.getInstance().setDevice_id(device_id);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new LoginParser(this, progressBar).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, userName,
                                password, device_id);
                    } else {
                        new LoginParser(this, progressBar).execute(userName,
                                password, device_id);
                    }

                }
            } else {
                loginCntr++;
                if (loginCntr >= LOGIN_MAX_FAIL_LIMIT) {
                    Toast.makeText(LoginActivity.this, "maximum limit reached",
                            Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(this).setTitle("memreas")
                            .setMessage("use forgot password?")
                            .setPositiveButton("yes", new OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    return;
                                }
                            }).setNegativeButton("no", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            return;
                        }
                    }).show();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "You can not leave username or password blank",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        state_Anim = AS_NONE;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

}
