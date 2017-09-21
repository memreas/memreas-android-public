
package com.memreas.search;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.sax.handler.AddFriendParser;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class UserTagAdapter extends ArrayAdapter<Object> {

	protected Context context;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected AnimateFirstDisplayListener animateProfileFirstListener;
	protected ImageLoader memreasImageLoader;

	public UserTagAdapter(Context context, List<Object> items) {
		super(context, R.layout.search_popupwindow_taguser_item_ref, items);
		this.context = context;
		animateGalleryListener = new AnimateFirstDisplayListener();
		animateGalleryListener.setFailImage(R.drawable.gallery_img);
		animateProfileFirstListener = new AnimateFirstDisplayListener();
		animateProfileFirstListener.setFailImage(R.drawable.profile_img);
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.search_popupwindow_taguser_item_ref, parent, false);
		}
		ImageView userAvatar = (ImageView) convertView
				.findViewById(R.id.iv_user_avatar);
		ImageView btnAddFriend = (ImageView) convertView
				.findViewById(R.id.btn_add_friend);
		TextView tvUserName = (TextView) convertView
				.findViewById(R.id.tv_user_name);
		View tvAdded = convertView.findViewById(R.id.tv_added);

		TagUser item = (TagUser) getItem(position);
		tvUserName.setText(item.getUsername());
		memreasImageLoader.displayImage(item.getProfile_photo(), userAvatar,
				optionsGallery, animateProfileFirstListener);
		if (item.getFriend_request_sent() == null) {
			btnAddFriend.setOnClickListener(addFriendListener);
			btnAddFriend.setTag(item);
			btnAddFriend.setImageResource(R.drawable.search_btn_add_friend);
			tvAdded.setVisibility(View.GONE);
			btnAddFriend.setVisibility(View.VISIBLE);
		} else if (item.getFriend_request_sent().equals("1")) {
			btnAddFriend.setOnClickListener(null);
			btnAddFriend
			.setImageResource(R.drawable.search_btn_friend_request_sent);
			tvAdded.setVisibility(View.GONE);
			btnAddFriend.setVisibility(View.VISIBLE);
		} else {
			tvAdded.setVisibility(View.VISIBLE);
			btnAddFriend.setVisibility(View.GONE);
		}

		return convertView;
	}

	OnClickListener addFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			TagUser friend = (TagUser) v.getTag();
			 ((ImageView) v)
			 .setImageResource(R.drawable.search_btn_friend_request_sent);
			 new AddFriendParser(context, friend).executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR);
		}
	};
}
