
package com.memreas.share;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.memreas.R;
import com.memreas.member.FriendBean;
import com.memreas.member.GroupBean;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;

import java.util.Iterator;
import java.util.LinkedList;

@SuppressWarnings("rawtypes")
public class ShareFriendSelectedAdapter extends ShareFriendSuperAdapter {

	private static ShareFriendSelectedAdapter instance;
	private ShareFriendAdapter shareFriendAdapter;
	private ProgressBar mProgressBar;

	protected ShareFriendSelectedAdapter(Activity context, int resource) {
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
		this.mProgressBar.setVisibility(View.VISIBLE);
	}

	public static ShareFriendSelectedAdapter getInstance(Activity context, int resource) {
		if (instance == null) {
			instance = new ShareFriendSelectedAdapter(context, resource);
			/*
			 * Fetch Data...
			 */
			instance.refreshFriendSelectedList();
		}
		return instance;
	}

	public static ShareFriendSelectedAdapter getInstance() {
		// Assumes adapter is set
		return instance;
	}

	public void clearInstanceFriendsList() {
		// clears list of friends
		try {
			instance.getmFriendOrGroup().clear();
		} catch (Exception e) {
			// do nothing likely null..
		}
	}

	public  void refreshFriendSelectedList() {
		instance.mProgressBar.setVisibility(View.VISIBLE);
		this.shareFriendAdapter = ShareFriendAdapter.getInstance();


		if (this.shareFriendAdapter != null) {
			Iterator iterator = shareFriendAdapter.getmFriendOrGroup().iterator();
			super.mFriendOrGroup = new LinkedList();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				if (obj instanceof FriendBean) {
					FriendBean friend = (FriendBean) obj;
					if (friend.isSelected()) {
						super.add(obj);
					}
				} else if (obj instanceof GroupBean) {
					//handle group
				}
			}
		}

		instance.mProgressBar.setVisibility(View.GONE);
	}
}
