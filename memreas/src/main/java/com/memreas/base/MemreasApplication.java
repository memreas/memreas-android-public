package com.memreas.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.WindowManager;

import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

public class MemreasApplication extends Application {

	private BaseActivity mCurActivity = null;
	public static final int BUF_SIZE = 8 * 1024;
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		/**
		 * Determine Screen Size
		 */
		getScreenSize();
		/**
		 * Init the Image Loager
		 */
		initImageLoader();
		/**
		 * Init the Foreground tracker
		 */
		Foreground.init(MemreasApplication.this);
	}

	@Override
	public void onTerminate() {
		try {
			ImageLoader.getInstance().clearMemoryCache();
			ImageLoader.getInstance().clearDiskCache();
			super.onTerminate();;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BaseActivity getCurActivity() {
		return mCurActivity;
	}

	public void setCurActivity(BaseActivity activity) {
		mCurActivity = activity;
	}

	/********************************************
	 * ** ** ** ** network relative ** ** ** ** *
	 ********************************************/
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			int i = netInfo.getType();
			return true;
		} else {
			if (mCurActivity != null)
				mCurActivity.WebNetworkAlert();
			return false;
		}

		// return true;

	}

	/****************************************
	 * ** ** ** ** GCM Relative ** ** ** ** *
	 ****************************************/

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void getScreenSize() {
		WindowManager windowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
			Point size = new Point();
			windowManager.getDefaultDisplay().getSize(size);
			SCREEN_WIDTH = size.x;
			SCREEN_HEIGHT = size.y;
	}

	private void initImageLoader() {
		MemreasImageLoader.getInstance(getApplicationContext());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		//ImageLoader.getInstance().clearMemoryCache();
		System.gc();
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


} // end MemreasApplication