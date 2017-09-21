
package com.memreas.share;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.memreas.R;
import com.memreas.base.MemreasApplication;
import com.memreas.member.load.LoadContactsAsyncTask;
import com.memreas.member.load.LoadMemreasFriendAsyncTask;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;

import java.util.LinkedList;

public class ShareFriendAdapter extends ShareFriendSuperAdapter {

	private static ShareFriendAdapter instance;
	private ProgressBar mProgressBar;

	protected ShareFriendAdapter(Activity context, int resource) {
		super.setContext(context);
		super.resource = resource;
		super.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		super.animateFirstListener = new AnimateFirstDisplayListener();
		super.animateFirstListener.setFailImage(R.drawable.profile_img_small);
		super.memreasImageLoader = MemreasImageLoader.getInstance();
		super.optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		super.optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
		super.mFriendOrGroup = new LinkedList();
		this.mProgressBar = (ProgressBar) context.findViewById(R.id.processBar);
	}

	public static ShareFriendAdapter getInstance(Activity context, int resource) {
		if (instance == null) {
			instance = new ShareFriendAdapter(context, resource);
			/*
			 * Fetch Data...
			 */
			instance.mProgressBar.setVisibility(View.VISIBLE);
			if (((MemreasApplication)context.getApplication()).isOnline()) {
				new LoadMemreasFriendAsyncTask(context,
						ShareFriendAdapter.getInstance(), instance.mProgressBar)
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new LoadContactsAsyncTask(context,
						ShareFriendAdapter.getInstance(), instance.mProgressBar)
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
			instance.mProgressBar.setVisibility(View.GONE);
		}
		return instance;
	}

	public static ShareFriendAdapter getInstance() {
		// Assumes adapter is set
		return instance;
	}

	public static void reset() {
		instance=null;
	}


}
