
package com.memreas.member;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.sax.handler.ChangePasswordParser;
import com.memreas.sax.handler.ForgotPasswordParser;


public class ForgotPasswordActivity extends BaseActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mVerifyPassword;
    private EditText mActivationCode;

    private TextView mEmailNotify;
    private TextView mPasswordNotify;
    private TextView mVerifyNotify;
    private TextView mActivationNotify;

    private ProgressBar mProgressbar;

    private int PASSWORD_MIN = 6;// Min 6
    private int PASSWORD_MAX = 32;// Max 32

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_forgot_pwd);

        mEmail = (EditText) findViewById(R.id.editEmail);
        mPassword = (EditText) findViewById(R.id.editPassword);
        mVerifyPassword = (EditText) findViewById(R.id.editVerify);
        mActivationCode = (EditText) findViewById(R.id.editActionCode);

        mEmailNotify = (TextView) findViewById(R.id.emailnotifyView);
        mPasswordNotify = (TextView) findViewById(R.id.pwdnotifyView);
        mVerifyNotify = (TextView) findViewById(R.id.verifynotifyView);
        mActivationNotify = (TextView) findViewById(R.id.activenotifyView);

        mProgressbar = (ProgressBar) findViewById(R.id.processBar);

        mEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus && !isEmailValid(mEmail.getText())) {
                    mEmailNotify.setText(R.string.register_invalid_email);
                } else {
                    mEmailNotify.setText("");
                }
            }
        });

        mEmail.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mEmailNotify.setText("");

                return false;
            }
        });

        mPassword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View c, boolean hasFocus) {

                if (!hasFocus) {
                    checkPassword();
                } else {
                    mPasswordNotify.setText("");
                }
            }
        });

        mPassword.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mPasswordNotify.setText("");

                return false;
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                verifyPassword(mPassword, mPasswordNotify);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });

        mVerifyPassword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View c, boolean hasFocus) {

                if (!hasFocus) {
                    checkVerifiedPassword();
                } else {
                    mVerifyNotify.setText("");
                }
            }
        });

        mVerifyPassword.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mVerifyNotify.setText("");

                return false;
            }
        });

        mVerifyPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                verifyPassword(mVerifyPassword, mVerifyNotify);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });

        mActivationCode.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus && isEmptyString(mActivationCode.getText().toString())) {
                    mActivationNotify.setText(R.string.forgot_activationcode);
                } else {
                    mActivationNotify.setText("");
                }
            }
        });

        mActivationCode.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mActivationNotify.setText("");

                return false;
            }
        });
    }

    public void onClickSubmit1(View v) {

        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click));

        if (!isEmailValid(mEmail.getText())) {
            mEmailNotify.setText(R.string.register_invalid_email);
        } else {
            mEmailNotify.setText("");

            mProgressbar.setVisibility(View.VISIBLE);

            new ForgotPasswordParser(this, mProgressbar)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mEmail.getText().toString());
        }
    }

    public void onClickSubmit2(View v) {

        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click));

        boolean check = true;

        if (!checkPassword())
            check = false;

        if (!checkVerifiedPassword())
            check = false;

        if (isEmptyString(mActivationCode.getText().toString())) {
            mActivationNotify.setText(R.string.forgot_activationcode);
            check = false;
        } else {
            mActivationNotify.setText("");
        }

        if (check) {
            mEmailNotify.setText("");
            mProgressbar.setVisibility(View.VISIBLE);
            new ChangePasswordParser(this, mProgressbar)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            mPassword.getText().toString(),
                            mVerifyPassword.getText().toString(),
                            mActivationCode.getText().toString());
        }
    }

    public void OnClickBack(View v) {

        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click));
        finish();
    }

    private boolean isEmailValid(CharSequence email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void verifyPassword(TextView passwordText, TextView pwCheker) {

        String password = passwordText.getText().toString();
        if (password.matches("^[a-zA-Z0-9~!@#$%^&*_| +=]+$")) {
            pwCheker.setText("Strong");
            pwCheker.setTextColor(Color.parseColor("#3399CC"));
        }
        if (password.matches("^[a-zA-Z0-9]+$")) {
            pwCheker.setText("Medium");
            pwCheker.setTextColor(Color.GREEN);
        }
        if (password.matches("^[a-zA-Z]+$")) {
            pwCheker.setText("Weak");
            pwCheker.setTextColor(Color.RED);
        }
    }

    private boolean checkPassword() {
        String mainpassword = mPassword.getText().toString();
        boolean flag = false;

        if (mainpassword.length() < PASSWORD_MIN) {
            mPasswordNotify.setTextColor(Color.RED);
            mPasswordNotify.setText(R.string.register_password_short);
        } else if (mainpassword.length() > PASSWORD_MAX) {
            mPasswordNotify.setText(R.string.register_password_long);
            mPasswordNotify.setTextColor(Color.RED);
        } else {
            flag = true;
            verifyPassword(mPassword, mPasswordNotify);
        }

        return flag;
    }

    private boolean checkVerifiedPassword() {
        String password = mVerifyPassword.getText().toString();
        boolean flag = false;

        if (password.length() < PASSWORD_MIN) {
            mVerifyNotify.setTextColor(Color.RED);
            mVerifyNotify.setText(R.string.register_verify_password_short);
        } else if (password.length() > PASSWORD_MAX) {
            mVerifyNotify.setText(R.string.register_verify_password_long);
            mVerifyNotify.setTextColor(Color.RED);
        } else {
            if (password.length() != 0
                    && !(mPassword.getText().toString()).equals(password)) {
                mPasswordNotify.setText(R.string.register_pass_not_matched);
                mPasswordNotify.setTextColor(Color.RED);
                mVerifyNotify.setText(R.string.register_pass_not_matched);
                mVerifyNotify.setTextColor(Color.RED);
            } else {
                verifyPassword(mVerifyPassword, mVerifyNotify);
                verifyPassword(mPassword, mPasswordNotify);
                flag = true;
            }
        }

        return flag;
    }

    private boolean isEmptyString(String str) {

        if (str.equals(""))
            return true;

        return false;
    }
}
