package com.memreas.memreas;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.memreas.MemreasEventBean.EventType;
import com.memreas.memreas.MemreasEventBean.FriendShortDetails;
import com.memreas.memreas.MemreasEventBean.MediaShortDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;

public class MemreasEventsPublicAdapter extends MemreasEventsSuperAdapter {

	private static MemreasEventsPublicAdapter instance;
	public static boolean asyncTaskComplete = false;
	private AnimateFirstDisplayListener animateProfileFirstListener;

	protected void setMemreasPublicAdapterView(ViewMemreasActivity context,
			int resource) {
		super.setContext(context);
		super.resource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		animateFirstListener = new AnimateFirstDisplayListener();
		animateFirstListener.setFailImage(R.drawable.gallery_img);
		animateProfileFirstListener = new AnimateFirstDisplayListener();
		animateProfileFirstListener.setFailImage(R.drawable.profile_img);
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
	}

	protected MemreasEventsPublicAdapter() {
		mMemreasList = new LinkedList<MemreasEventPublicBean>();
	}

	public static MemreasEventsPublicAdapter getInstance() {
		// Assumes adatpter is set
		if (instance == null) {
			instance = new MemreasEventsPublicAdapter();
		}
		return instance;
	}

	public static void reset() {
		asyncTaskComplete = false;
		instance = null;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			/*
			 * Initialize Holder
			 */
			holder = new ViewHolder();
			convertView = mInflater.inflate(this.resource, parent, false);

			holder.memreas_event_thumb = (ImageView) convertView
					.findViewById(R.id.memreas_event_thumb);
			holder.memreas_event_thumb
					.setOnClickListener(memreasEventGalleryListener);
			holder.memreas_event_creator_thumb = (ImageView) convertView
					.findViewById(R.id.memreas_event_creator_thumb);
			holder.txtEventName = (TextView) convertView
					.findViewById(R.id.txtEventName);
			holder.btn_like = (ImageView) convertView
					.findViewById(R.id.btn_like);
			holder.txt_like = (TextView) convertView
					.findViewById(R.id.txt_like);
			holder.btn_comment = (ImageView) convertView
					.findViewById(R.id.btn_comment);
			holder.txt_comment = (TextView) convertView
					.findViewById(R.id.txt_comment);
			holder.friendsLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.friendsLinearLayout);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * Set viewable list data based on position
		 */
		MemreasEventPublicBean memreasEventPublicBean = (MemreasEventPublicBean) mMemreasList
				.get(position);
		LinkedList<MediaShortDetails> publicEventMediaList = memreasEventPublicBean
				.getPublicEventMediaList();
		LinkedList<FriendShortDetails> publicEventFriendsList = memreasEventPublicBean
				.getPublicEventFriendsList();
		String url = "";
		try {
			int random_index;
			if ((publicEventMediaList != null)
					&& (publicEventMediaList.size() > 0)) {
				random_index = new Random()
						.nextInt(publicEventMediaList.size());
				if (publicEventMediaList.get(random_index).media_type
						.equalsIgnoreCase("image"))
					url = publicEventMediaList.get(random_index).media_url;
				else if (publicEventMediaList.get(random_index).media_type
						.equalsIgnoreCase("video"))
					url = publicEventMediaList.get(random_index).media_98x78.getString(0);
				// audio use fail image
			}
		} catch (Exception e) {
			// use fail image...
		}
		memreasImageLoader.displayImage(url, holder.memreas_event_thumb,
				optionsGallery, animateFirstListener);

		// Event owner profile pic
		memreasImageLoader.displayImage(
				memreasEventPublicBean.getProfile_pic(),
				holder.memreas_event_creator_thumb, optionsGallery,
				animateProfileFirstListener);

		// Event name
		holder.txtEventName.setText("!"
				+ memreasEventPublicBean.getEvent_name());
		holder.txt_like.setText(String.valueOf(memreasEventPublicBean
				.getEvent_like_total()));
		holder.txt_comment.setText(String.valueOf(memreasEventPublicBean
				.getEvent_comment_total()));
		holder.friendsLinearLayout.removeAllViewsInLayout();
		Iterator<FriendShortDetails> iterator = publicEventFriendsList
				.iterator();
		while (iterator.hasNext()) {
			FriendShortDetails publicEventFriends = iterator.next();
			ViewHolderFriend holderFriend = new ViewHolderFriend();
			View v = mInflater.inflate(R.layout.memreas_public_friend_item,
					parent, false);
			holderFriend.friendFrameLayout = (FrameLayout) v
					.findViewById(R.id.friendFrameLayout);
			holderFriend.img_friend = (ImageView) v
					.findViewById(R.id.img_friend);
			holder.friendsLinearLayout.addView(holderFriend.friendFrameLayout);
			memreasImageLoader.displayImage(
					publicEventFriends.friend_url_image,
					holderFriend.img_friend, optionsGallery,
					animateProfileFirstListener);
		}

		holder.position = position;
		holder.memreas_event_thumb.setTag(Integer.valueOf(position));
		convertView.setTag(holder);
		return convertView;
	}

	@SuppressWarnings("unchecked")
	public void add(MemreasEventPublicBean memreasEventBean) {
		mMemreasList.add(memreasEventBean);
		this.notifyDataSetChanged();
	}

	protected class ViewHolder {
		public LinearLayout friendsLinearLayout;
		public ImageView memreas_event_thumb;
		public ImageView memreas_event_creator_thumb;
		public TextView txtEventName;
		public ImageView btn_like;
		public TextView txt_like;
		public ImageView btn_comment;
		public TextView txt_comment;
		public int position;
	}

	protected class ViewHolderFriend {
		public FrameLayout friendFrameLayout;
		public ImageView img_friend;
	}

	protected OnClickListener memreasEventGalleryListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Integer position = (Integer) v.getTag();
			((ViewMemreasActivity) context).onItemClickViewEvent(position,
					null, EventType.PUBLIC);
		}
	};
}
