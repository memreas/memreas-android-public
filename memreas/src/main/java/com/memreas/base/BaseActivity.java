package com.memreas.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.memreas.R;
import com.memreas.gallery.GalleryActivity;
import com.memreas.memreas.ViewMemreasActivity;
import com.memreas.more.MoreActivity;
import com.memreas.notifications.GcmBroadcastReceiver;
import com.memreas.notifications.NotificationList;
import com.memreas.notifications.NotificationPopup;
import com.memreas.queue.QueueActivity;
import com.memreas.sax.handler.AddCommentParser;
import com.memreas.search.SearchPopup;
import com.memreas.share.AddShareActivity;
import com.memreas.util.MExceptionHandler;
import com.memreas.util.MemreasProgressDialog;

import java.io.File;
import java.lang.ref.SoftReference;

public class BaseActivity extends FragmentActivity {

	protected MemreasProgressDialog mLoadingDialog;
	public static final int GALLERY_INDEX = 1;
	public static final int MEMREAS_INDEX = 2;
	public static final int SHARE_INDEX = 3;
	public static final int QUEUE_INDEX = 4;
	public static final int MORE_INDEX = 5;
	protected static int PREV_INDEX = GALLERY_INDEX;
	public static boolean isPauseUploadDownload = false;
	private NotificationPopup mNotificationPopup;
	private SearchPopup mSearchPopup;
	protected Typeface fontCentury;
	public static MemreasApplication mApplication;
	public static Bitmap defaultBitmap;
	public String audioCommentMediaId = "";
	public static boolean isAudioComment;
	public static boolean galleryReloadFlag = true;
	public static boolean memreasReloadFlag = true;
	public final int LOGIN_MAX_FAIL_LIMIT = 10;
	public static boolean isReturningFromBackground = false;


	public SoftReference<BaseActivity> getSoftRefActivity() {
		return new SoftReference<BaseActivity>(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Foreground.getInstance().setForeground(true);
		mApplication = (MemreasApplication) this.getApplication();
		mApplication.setCurActivity(this);

		defaultBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.profile_img);

		fontCentury = Typeface.createFromAsset(getAssets(),
				"font/century_gothic.ttf");

		/** Set fatal crash handler to send bug reports */
		if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof MExceptionHandler)) {
			Thread.setDefaultUncaughtExceptionHandler(new MExceptionHandler());
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		//showNotification();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		//showNotification();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		//showNotification();
	}

	private void showNotification() {

		if (getIntent().getBooleanExtra("has_notification", false)) {
			View v = findViewById(R.id.optionBtn);
			if (v != null) {
				v.post(new Runnable() {

					@Override
					public void run() {
						findViewById(R.id.optionBtn).performClick();
					}
				});
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateNotificationNumber();
		mApplication.setCurActivity(this);
		Foreground.getInstance().setForeground(true);
	}

	// @Override
	protected void onPause() {

		clearReferences();
		super.onPause();
	}

	// @Override
	// protected void onDestroy() {
	//
	// clearReferences();
	// super.onDestroy();
	//
	// System.gc();
	// // trimCache(this);
	// }


	//Fires after the OnStop() state
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			trimCache(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	private void clearReferences() {
		Activity currActivity = mApplication.getCurActivity();
		if (currActivity != null && currActivity.equals(this))
			mApplication.setCurActivity(null);
	}

	//
	// Hide the status bar
	//
	public void hideStatusBar() {
		//
		// Hide the status bar
		//
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}

	}

	/**************************************************
	 * ** ** ** ** activity menu Relative ** ** ** ** *
	 ***************************************************/
	public void menuConfiguration() {

		switch (PREV_INDEX) {
			case GALLERY_INDEX:
				((ImageView) findViewById(R.id.menuGallery))
						.setImageResource(R.drawable.menu_gallery_select);
				((ImageView) findViewById(R.id.menuMemreas))
						.setImageResource(R.drawable.menu_more);
				((ImageView) findViewById(R.id.menuShare))
						.setImageResource(R.drawable.menu_share);
				((ImageView) findViewById(R.id.menuQueue))
						.setImageResource(R.drawable.menu_queue);
				((ImageView) findViewById(R.id.menuMore))
						.setImageResource(R.drawable.menu_more);
				break;
			case QUEUE_INDEX:
				((ImageView) findViewById(R.id.menuQueue))
						.setImageResource(R.drawable.menu_queue_select);
				((ImageView) findViewById(R.id.menuGallery))
						.setImageResource(R.drawable.menu_gallery);
				((ImageView) findViewById(R.id.menuMemreas))
						.setImageResource(R.drawable.menu_more);
				((ImageView) findViewById(R.id.menuShare))
						.setImageResource(R.drawable.menu_share);
				((ImageView) findViewById(R.id.menuMore))
						.setImageResource(R.drawable.menu_more);
				break;
			case SHARE_INDEX:
				((ImageView) findViewById(R.id.menuShare))
						.setImageResource(R.drawable.menu_share_select);
				((ImageView) findViewById(R.id.menuGallery))
						.setImageResource(R.drawable.menu_gallery);
				((ImageView) findViewById(R.id.menuMemreas))
						.setImageResource(R.drawable.menu_more);
				((ImageView) findViewById(R.id.menuQueue))
						.setImageResource(R.drawable.menu_queue);
				((ImageView) findViewById(R.id.menuMore))
						.setImageResource(R.drawable.menu_more);
				break;
			case MEMREAS_INDEX:
				((ImageView) findViewById(R.id.menuMemreas))
						.setImageResource(R.drawable.menu_more_select);
				((ImageView) findViewById(R.id.menuGallery))
						.setImageResource(R.drawable.menu_gallery);
				((ImageView) findViewById(R.id.menuShare))
						.setImageResource(R.drawable.menu_share);
				((ImageView) findViewById(R.id.menuQueue))
						.setImageResource(R.drawable.menu_queue);
				((ImageView) findViewById(R.id.menuMore))
						.setImageResource(R.drawable.menu_more);
				break;
			case MORE_INDEX:
				((ImageView) findViewById(R.id.menuMore))
						.setImageResource(R.drawable.menu_more_select);
				((ImageView) findViewById(R.id.menuGallery))
						.setImageResource(R.drawable.menu_gallery);
				((ImageView) findViewById(R.id.menuMemreas))
						.setImageResource(R.drawable.menu_more);
				((ImageView) findViewById(R.id.menuShare))
						.setImageResource(R.drawable.menu_share);
				((ImageView) findViewById(R.id.menuQueue))
						.setImageResource(R.drawable.menu_queue);
				break;
			default:
				break;
		}
	}

	public void menuSelection(int index) {

		switch (index) {
			case GALLERY_INDEX:
				//this flag reloads gallery on touch - replaced by pull down reload
				//galleryReloadFlag = true;
				((ImageView) findViewById(R.id.menuGallery))
						.setImageResource(R.drawable.menu_gallery_select);
				startActivityForResult(new Intent(this, GalleryActivity.class), 0);
				if (PREV_INDEX != GALLERY_INDEX)
					finish();
				break;
			case QUEUE_INDEX:
				((ImageView) findViewById(R.id.menuQueue))
						.setImageResource(R.drawable.menu_queue_select);
				startActivityForResult(new Intent(this, QueueActivity.class), 0);
				if (PREV_INDEX != GALLERY_INDEX)
					finish();
				break;
			case MEMREAS_INDEX:
				memreasReloadFlag = true;
				((ImageView) findViewById(R.id.menuMemreas))
						.setImageResource(R.drawable.menu_more_select);
				startActivityForResult(new Intent(this, ViewMemreasActivity.class),
						0);
				if (PREV_INDEX != GALLERY_INDEX)
					finish();
				break;
			case SHARE_INDEX:
				((ImageView) findViewById(R.id.menuShare))
						.setImageResource(R.drawable.menu_share_select);
				startActivityForResult(new Intent(this, AddShareActivity.class), 0);
				if (PREV_INDEX != GALLERY_INDEX)
					finish();
				break;
			case MORE_INDEX:
				((ImageView) findViewById(R.id.menuMore))
						.setImageResource(R.drawable.menu_more_select);
				startActivityForResult(new Intent(this,
								MoreActivity.class),
						0);
				if (PREV_INDEX != GALLERY_INDEX)
					finish();
				break;
			default:
				break;
		}

		PREV_INDEX = index;
	}

	public void resetSelection(int curIndex) {

		if (PREV_INDEX == curIndex)
			return;

		switch (PREV_INDEX) {
			case GALLERY_INDEX:
				((ImageView) findViewById(R.id.menuGallery))
						.setImageResource(R.drawable.menu_gallery);
				menuSelection(curIndex);
				break;
			case QUEUE_INDEX:
				((ImageView) findViewById(R.id.menuQueue))
						.setImageResource(R.drawable.menu_queue);
				menuSelection(curIndex);
				break;
			case SHARE_INDEX:
				((ImageView) findViewById(R.id.menuShare))
						.setImageResource(R.drawable.menu_share);
				menuSelection(curIndex);
				break;
			case MEMREAS_INDEX:
				((ImageView) findViewById(R.id.menuMemreas))
						.setImageResource(R.drawable.menu_more);
				menuSelection(curIndex);
				break;
			case MORE_INDEX:
				((ImageView) findViewById(R.id.menuMore))
						.setImageResource(R.drawable.menu_more);
				menuSelection(curIndex);
				break;
			default:
				break;
		}
	}

	// Back button handler(@param : Button)
	public void backEvent(View v) {
		finish();
	}

	// Menu Gallery button handler(@param : Button)
	public void menuGallery(View v) {
		resetSelection(GALLERY_INDEX);
	}

	// Menu Memreas button handler(@param : Button)
	public void menuMemreas(View v) {
		resetSelection(MEMREAS_INDEX);
	}

	// Menu share button handler(@param : Button)
	public void menuShare(View v) {
		resetSelection(SHARE_INDEX);
	}

	// Menu Queue button handler(@param : Button)
	public void menuQueue(View v) {
		resetSelection(QUEUE_INDEX);
	}

	// Menu More button handler(@param : Button)
	public void menuMore(View v) {
		resetSelection(MORE_INDEX);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Common.DEVICEID = savedInstanceState.getString("deviceId");
		SessionManager.getInstance().setSession_id(savedInstanceState.getString("session_id"));
		SessionManager.getInstance().setDevice_id(savedInstanceState.getString("device_id"));
		SessionManager.getInstance().setDevice_id(savedInstanceState.getString("device_token"));
		SessionManager.getInstance().setUser_id(savedInstanceState.getString("user_id"));
		SessionManager.getInstance().setUser_name(savedInstanceState.getString("user_name"));
		SessionManager.getInstance().setUser_email(
				savedInstanceState.getString("user_email"));
		SessionManager.getInstance().setUser_profile_picture(
				savedInstanceState.getString("user_profile_pic"));
		SessionManager.getInstance().setUser_plan_type(
				savedInstanceState.getString("user_plan_type"));
		SessionManager.getInstance().setUser_plan_name(
				savedInstanceState.getString("user_plan_name"));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("deviceId", Common.DEVICEID);
		outState.putString("session_id", SessionManager.getInstance().getSession_id());
		outState.putString("device_id", SessionManager.getInstance().getDevice_id());
		outState.putString("device_token", SessionManager.getInstance().getDevice_token());
		outState.putString("user_id", SessionManager.getInstance().getUser_id());
		outState.putString("user_name", SessionManager.getInstance().getUser_name());
		outState.putString("user_email", SessionManager.getInstance().getUser_email());
		outState.putString("user_profile_pic", SessionManager.getInstance().getUser_profile_picture());
		outState.putString("user_plan_type", SessionManager.getInstance().getUser_plan_type());
		outState.putString("user_plan_name", SessionManager.getInstance().getUser_plan_name());
		super.onSaveInstanceState(outState);
	}

	/******************************************
	 * ** ** ** ** Admob Relative ** ** ** ** *
	 ******************************************/
	public void AdmobView() {

		//
		// Set for testing now...
		//
		AdRequest request = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
				.addTestDevice(Common.DEVICEID)  // device id
				.build();

		//
		// Set AdView
		//
		AdView adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(Common.ADMOB_BANNER_UNIT_ID);
		LinearLayout lyaddon = (LinearLayout) findViewById(R.id.lyaddon);
		lyaddon.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();
		adRequest.isTestDevice(BaseActivity.this);
		adView.loadAd(adRequest);

	}

	public void WebNetworkAlert() {
		new AlertDialog.Builder(this).setTitle("Network Error")
				.setMessage("Internet connection not available.")
				.setPositiveButton("Back", new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				}).show();
	}

	public void userNotationAlert(int message) {
		Log.i("create group", "" + message);
		if (message != 0) {
			new AlertDialog.Builder(this)
					.setTitle("memreas alert")
					.setMessage(message)
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
													int arg1) {
									return;
								}
							}).show();
		}
	}

	public void navSearchBtn(View v) {
		showSearch(v);
	}

	public void showSearch(View v) {
		if (mSearchPopup == null) {
			mSearchPopup = new SearchPopup(this);
		}
		mSearchPopup.show(v);
	}

	public void navNotificationBtn(View v) {
		showNotification(v);
	}

	public void showNotification(View v) {
		if (mNotificationPopup == null) {
			mNotificationPopup = new NotificationPopup(this);
		}
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		mNotificationPopup.show(v);
	}

	public void updateNotificationNumber() {
		TextView navTvNotification = (TextView) findViewById(R.id.nav_tv_notification);
		if (navTvNotification != null
				&& NotificationList.getInstance().getNotificationList() != null) {
			if (NotificationList.getInstance().getNotificationList().size() > 0) {
				navTvNotification.setText(String.valueOf(NotificationList
						.getInstance().getNotificationList().size()));
				navTvNotification.setVisibility(View.VISIBLE);
			} else {
				navTvNotification.setVisibility(View.INVISIBLE);
			}
		}
	}

	/********************************************
	 * ** ** ** Dialog, Alert Relative ** ** ** *
	 ********************************************/
	// protected Dialog onCreateDialog(int id) {
	//
	// switch (id) {
	// case 0:
	// ProgressDialog dialog = new ProgressDialog(this);
	// dialog.setMessage("Loading . . .");
	// dialog.setIndeterminate(true);
	// dialog.setCancelable(true);
	// return dialog;
	// default:
	// return null;
	// }
	// }

	// public void NetworkAlert() {
	// new AlertDialog.Builder(this).setTitle("Network Error")
	// .setMessage("Image can't show")
	// .setPositiveButton("Back", new OnClickListener() {
	// public void onClick(DialogInterface arg0, int arg1) {
	// finish();
	// }
	// }).setNegativeButton("Continue", new OnClickListener() {
	// public void onClick(DialogInterface arg0, int arg1) {
	// return;
	// }
	// }).show();
	// }
	//
	//
	// public void userNotationAlert(String message) {
	//
	// new AlertDialog.Builder(this).setTitle("memreas alert")
	// .setMessage(message)
	// .setPositiveButton("ok", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface arg0, int arg1) {
	// return;
	// }
	// }).show();
	// }
	//
	// public static void addCommentOnMedia(BaseActivity activity, String
	// eventId,
	// String mediaId, String audiomediaID, boolean canFinished) {
	//
	// if (mediaId != null && mediaId.length() > 0) {
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	// new AddCommentParser(activity, canFinished).executeOnExecutor(
	// AsyncTask.THREAD_POOL_EXECUTOR, eventId, mediaId, "",
	// audiomediaID);
	// else
	// new AddCommentParser(activity, canFinished).execute(eventId,
	// mediaId, "", audiomediaID);
	// activity.audioCommentMediaId = "";
	// isAudioComment = false;
	// }
	// }
	//
	public static void addComment(BaseActivity activity, String eventId,
								  String mediaId, String message, boolean canFinish) {

		if (message.length() > 0
				&& (message.length() > 0 || activity.audioCommentMediaId
				.length() > 0)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				new AddCommentParser(activity, canFinish).executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, eventId, mediaId,
						message, activity.audioCommentMediaId, "url");
			else
				new AddCommentParser(activity, canFinish).execute(eventId,
						mediaId, message, activity.audioCommentMediaId, "url");

			activity.audioCommentMediaId = "";
			isAudioComment = false;
		}
	}

	// handler for received Intents for the ... event
	public BroadcastReceiver mGoogleCloudMessageReceiver = new BroadcastReceiver() {

		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(BaseActivity.this);
			// The getMessageType() intent parameter must be the intent you received
			// in your BroadcastReceiver.
			String messageType = gcm.getMessageType(intent);

			if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
				/*
				 * Filter messages based on message type. Since it is likely that GCM will be
	             * extended in the future with new message types, just ignore any message types you're
	             * not interested in, or that you don't recognize.
	             */
				// If it's a regular GCM message, do some work.
				if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
					// This loop represents the service doing some work.
					// for (int i = 0; i < 5; i++) {
					// Log.i(TAG, "Working... " + (i + 1)
					// + "/5 @ " + SystemClock.elapsedRealtime());
					// try {
					// Thread.sleep(5000);
					// } catch (InterruptedException e) {
					// }
					// }
					// Log.i(TAG, "Completed work @ " +
					// SystemClock.elapsedRealtime());
					// // Post notification of received message.
					// sendNotification("Received: " + extras.toString());
					// Log.i(TAG, "Received: " + extras.toString());
				}
			}
			// Release the wake lock provided by the WakefulBroadcastReceiver.
			GcmBroadcastReceiver.completeWakefulIntent(intent);
		} // end onReceive
	}; // end broadcast receiver...


}