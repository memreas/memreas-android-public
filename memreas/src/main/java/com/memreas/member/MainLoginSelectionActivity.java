
package com.memreas.member;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.base.SessionManager;
import com.memreas.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

public class MainLoginSelectionActivity extends BaseActivity implements
		AnimationListener {

	private FrameLayout mVersionLyt;
	private FrameLayout mButtonLyt;
	private ProgressBar progressBar;

	private Thread mThread;
	private Animation mAnimFadeIn = null;
	private Animation mAnimFadeOut = null;
	private static final int AS_NONE = 0;
	private static final int AS_FADEIN = 1;
	private static final int AS_FADEOUT = 2;
	private static LinearLayout sliderLayout;

	private int mAnimState = AS_NONE;

	private int slidePosition = 0;
	private int[] slideImages = new int[] { R.drawable.slider1,
			R.drawable.slider2, R.drawable.slider3, R.drawable.slider4,
			R.drawable.slider5, R.drawable.slider6, R.drawable.slider7 };
	private java.util.Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.screen_main_login);
		//Clear any prior session...
		SessionManager.resetSessionManager();

		mVersionLyt = (FrameLayout) findViewById(R.id.buttonLogin);
		mButtonLyt = (FrameLayout) findViewById(R.id.buttonSignUp);
		progressBar = (ProgressBar) findViewById(R.id.processBar);
		boolean isSigned = false;
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				final String currentSignature = Base64.encodeBytes(md.digest());
				//Log.w("PXR", currentSignature);
				if (Common.SIGNATURE.equals(currentSignature)) {
					isSigned = true;
				}
			}
			if (!isSigned) {
				//disable buttons and toast
				mVersionLyt.setVisibility(View.INVISIBLE);
				mButtonLyt.setVisibility(View.INVISIBLE);
				Toast.makeText(this, "access denied - invalid signature", Toast.LENGTH_LONG);
			}
		} catch (PackageManager.NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}

		mButtonLyt.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.INVISIBLE);

		mAnimFadeIn = AnimationUtils.loadAnimation(
				MainLoginSelectionActivity.this, R.anim.fade_in);
		mAnimFadeIn.setAnimationListener(this);

		mAnimFadeOut = AnimationUtils.loadAnimation(
				MainLoginSelectionActivity.this, R.anim.fade_out);
		mAnimFadeOut.setAnimationListener(this);

		Common.DEVICEID = Secure.getString(getApplicationContext()
				.getContentResolver(), Secure.ANDROID_ID);
		//Log.i("MainLoginSelectionActivity", "Device Id : " + Common.DEVICEID);

		mThread = new Thread(new Timer());
		mThread.start();
		PREV_INDEX = 2;

		mTimer = new java.util.Timer();
		sliderLayout = (LinearLayout) findViewById(R.id.sliderImage);
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {

				slidePosition++;
				if (slidePosition == slideImages.length) {
					slidePosition = 0;
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						sliderLayout
								.setBackgroundResource(slideImages[slidePosition]);
					}
				});

				//System.gc();
			}
		}, 4000, 4000);

	}

	public void signUpBtn(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click));
		progressBar.setVisibility(View.VISIBLE);
		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click));

		Intent i = new Intent(getApplicationContext(),
				RegistrationActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}
	}

	public void loginBtn(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click));
		progressBar.setVisibility(View.VISIBLE);
		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click));
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		startActivityForResult(i, 1000);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class Timer implements Runnable {

		@Override
		public void run() {

			try {
				Thread.sleep(3000);
				handler.sendEmptyMessage(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 0) {

				mAnimState = AS_FADEOUT;
				mVersionLyt.startAnimation(mAnimFadeOut);
			}
		}
	};

	@Override
	public void onAnimationEnd(Animation animation) {

		if (mAnimState == AS_FADEOUT) {
			mAnimState = AS_FADEIN;
			mButtonLyt.startAnimation(mAnimFadeIn);
		} else if (mAnimState == AS_FADEIN) {
			mAnimState = AS_NONE;
			mButtonLyt.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 1000 && arg1 == RESULT_OK) {
			finish();
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
}
