
package com.memreas.memreas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.memreas.MemreasEventBean.EventType;
import com.memreas.memreas.MemreasEventFriendsBean.FriendEventDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;

import org.json.JSONArray;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class MemreasEventsFriendsAdapter extends MemreasEventsSuperAdapter {

	private static MemreasEventsFriendsAdapter instance;
	public static boolean asyncTaskComplete = false;
	private AnimateFirstDisplayListener animateProfileFirstListener;

	protected void setMemreasFriendsAdapterView(ViewMemreasActivity context,
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

	protected MemreasEventsFriendsAdapter() {
		mMemreasList = new LinkedList<MemreasEventFriendsBean>();
	}

	public static MemreasEventsFriendsAdapter getInstance() {
		// Assumes adatpter is set
		if (instance == null) {
			instance = new MemreasEventsFriendsAdapter();
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

			holder.profileImageSmall = (ImageView) convertView
					.findViewById(R.id.rounded_profile_img_small);
			holder.txtFriendName = (TextView) convertView
					.findViewById(R.id.txtFriendName);
			holder.friendEventItemsLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.friendEventItemsLinearLayout);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * Set viewable list data based on position
		 */
		MemreasEventFriendsBean memreasEventFriendsBean = (MemreasEventFriendsBean) mMemreasList
				.get(position);
		String url = memreasEventFriendsBean.getProfile_pic();
		memreasImageLoader.displayImage(url, holder.profileImageSmall,
				optionsGallery, animateProfileFirstListener);
		holder.txtFriendName.setText("@"
				+ memreasEventFriendsBean.getEvent_creator());
		LinkedList<MemreasEventFriendsBean.FriendEventDetails> friendEventDetailsList = memreasEventFriendsBean
				.getFriendEventDetailsList();
		holder.friendEventItemsLinearLayout.removeAllViewsInLayout();
		Iterator<FriendEventDetails> iterator = friendEventDetailsList
				.iterator();
		int subPos =0;
		while (iterator.hasNext()) {
			MemreasEventFriendsBean.FriendEventDetails friendEventDetails = iterator
					.next();
			ViewHolderDetails holderDetails = new ViewHolderDetails();
			View eventView = mInflater.inflate(
					R.layout.memreas_friend_hscroll_item, parent, false);
			holderDetails.memreas_event_thumb = (ImageView) eventView
					.findViewById(R.id.memreas_event_thumb);
			holderDetails.position=subPos++;
			holder.holderDetails = holderDetails;
			holderDetails.memreas_event_thumb.setTag(holder);
			holderDetails.memreas_event_thumb.setOnClickListener(memreasEventGalleryListener);
			holderDetails.memreas_event_name = (TextView) eventView
					.findViewById(R.id.memreas_event_name);

			// Populate...
			try {
				int random_index;
				if ((friendEventDetails.mediaShortDetailsList != null)
						&& (friendEventDetails.mediaShortDetailsList.size() > 0)) {
					random_index = new Random()
							.nextInt(friendEventDetails.mediaShortDetailsList
									.size());
					JSONArray jsonArray = friendEventDetails.mediaShortDetailsList
							.get(random_index).media_98x78;
					url = jsonArray.getString(0);
				} else {
					if (friendEventDetails.mediaShortDetailsList.get(0).media_type
							.equalsIgnoreCase("image")) {
						url = friendEventDetails.mediaShortDetailsList.get(0).media_url;
					}
				}
			} catch (Exception e) {
				// use fail image...
			}
			memreasImageLoader.displayImage(url,
					holderDetails.memreas_event_thumb, optionsGallery,
					animateFirstListener);

			holderDetails.memreas_event_name.setText("!"
					+ friendEventDetails.event_name);
			holder.friendEventItemsLinearLayout.addView(eventView);
		}

		holder.position = position;
		convertView.setTag(holder);
		return convertView;
	}

	@SuppressWarnings("unchecked")
	public void add(MemreasEventFriendsBean memreasEventBean) {
		mMemreasList.add(memreasEventBean);
		this.notifyDataSetChanged();
	}

	protected class ViewHolder {
		public ImageView profileImageSmall;
		public TextView txtFriendName;
		public LinearLayout friendEventItemsLinearLayout;
		public ViewHolderDetails holderDetails;
		public int position;
	}

	protected class ViewHolderDetails {
		public ImageView memreas_event_thumb;
		public TextView memreas_event_name;
		public int position;
	}

	protected OnClickListener memreasEventGalleryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ViewHolder holder = (ViewHolder) v.getTag();
			((ViewMemreasActivity) context).onItemClickViewEvent(holder.position, holder.holderDetails.position, EventType.FRIENDS);
		}
	};
	

}
