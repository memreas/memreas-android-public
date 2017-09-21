
package com.memreas.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.memreas.ViewMemreasActivity;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class EventTagAdapter extends ArrayAdapter<Object> {

	private Context context;
	private View eventProfileView;
	private ImageView memreas_event_profile;
	private TextView memreas_event_profile_name;
	private View btnAddMe;
	private TextView tv_added;
	private ImageView eventThumb;
	private ImageView eventThumbPlayIcon;
	private TextView tvName;

	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected AnimateFirstDisplayListener animateProfileFirstListener;
	protected ImageLoader memreasImageLoader;

	public EventTagAdapter(Context context, List<Object> items) {
		super(context, R.layout.search_popupwindow_tagevent_item_ref, items);
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
					R.layout.search_popupwindow_tagevent_item_ref, parent, false);
		}
		eventProfileView = (View) convertView.findViewById(R.id.eventProfileView);
		memreas_event_profile = (ImageView) eventProfileView.findViewById(R.id.memreas_event_profile);
		memreas_event_profile_name = (TextView) eventProfileView.findViewById(R.id.memreas_event_profile_name);
		btnAddMe = convertView.findViewById(R.id.btn_add_me);
		eventThumb = (ImageView) convertView.findViewById(R.id.iv_media_thumb);
		eventThumbPlayIcon = (ImageView) convertView.findViewById(R.id.iv_media_thumb_play_icon);
		tvName = (TextView) convertView.findViewById(R.id.tv_event_name);
		tv_added =  (TextView) convertView.findViewById(R.id.tv_added);
		TagEvent item = (TagEvent) getItem(position);

		tvName.setText(item.getName());
		memreas_event_profile_name.setText(item.getEvent_creator_name());
		memreasImageLoader.displayImage(item.getEvent_creator_pic(), memreas_event_profile,
				optionsGallery, animateProfileFirstListener);
		memreasImageLoader.displayImage(item.getEvent_media_url(), eventThumb,
				optionsGallery, animateProfileFirstListener);
		if (item.getType().equalsIgnoreCase("video")) {
			eventThumbPlayIcon.setVisibility(View.VISIBLE);
		} else {
			eventThumbPlayIcon.setVisibility(View.GONE);
		}

		if (!item.isAdded()) {
			btnAddMe.setOnClickListener(addMeListener);
			btnAddMe.setVisibility(View.VISIBLE);
		} else {
			btnAddMe.setVisibility(View.GONE);
			tv_added.setVisibility(View.VISIBLE);
			tvName.setTag(item.event_id);
			tvName.setTextColor(Color.BLUE);
			tvName.setOnClickListener(onClickListener);
			eventThumb.setTag(item.event_id);
			eventThumb.setOnClickListener(onClickListener);
		}
		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TagEvent item = (TagEvent) getItem(position);
				if (item.isAdded()) {
					/*
					 * TODO - Add in NewMemreasDetailActivity...
					 */
					Log.e("MISSING----->", "NewMemreasDetailActivity");
					// Intent intent = new Intent(getContext(),
					// NewMemreasDetailActivity.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// intent.putExtra("event_id", item.getId());
					// intent.putExtra("creator_id", item.getUserId());
					// getContext().startActivity(intent);
				}
			}
		});

		return convertView;
	}

	OnClickListener addMeListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// v.setVisibility(View.INVISIBLE);
			// ((TagEvent) getItem(position)).setAdded(true);
			// new AddFriendToEventParser().executeOnExecutor(
			// AsyncTask.THREAD_POOL_EXECUTOR,
			// ((TagEvent) getItem(position)).getName());
		}
	};
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context, ViewMemreasActivity.class);
			intent.putExtra("event_id", v.getTag().toString());
			context.startActivity(intent);
		}
	};

}