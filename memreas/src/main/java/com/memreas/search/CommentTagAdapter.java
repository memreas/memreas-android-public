
package com.memreas.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.memreas.ViewMemreasActivity;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentTagAdapter extends ArrayAdapter<Object> {

	protected Context context;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected AnimateFirstDisplayListener animateProfileFirstListener;
	protected ImageLoader memreasImageLoader;

	private TextView tv_hashtag_name;
	private View hashtagProfileView;
	private ImageView memreas_event_profile;
	private TextView memreas_event_profile_name;
	private LinearLayout eventThumbsLinearLayout;
	private TextView eventNameTextView;
	private TextView tv_comment;
	private ImageView ic_notification_type;
	private HashMap<String, String> media_map;

	public CommentTagAdapter(Context context, List<Object> items) {
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
					R.layout.search_popupwindow_tagcomment_item_ref, parent, false);
		}

		tv_hashtag_name = (TextView) convertView
				.findViewById(R.id.tv_hashtag_name);
		hashtagProfileView = (View) convertView
				.findViewById(R.id.hashtagProfileView);
		memreas_event_profile = (ImageView) hashtagProfileView
				.findViewById(R.id.memreas_event_profile);
		memreas_event_profile_name = (TextView) hashtagProfileView
				.findViewById(R.id.memreas_event_profile_name);
		tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
		ic_notification_type = (ImageView) convertView
				.findViewById(R.id.ic_notification_type);
		eventNameTextView = (TextView) convertView
				.findViewById(R.id.eventNameTextView);
		eventThumbsLinearLayout = (LinearLayout) convertView
				.findViewById(R.id.eventThumbsLinearLayout);

		TagComment item = (TagComment) getItem(position);
		tv_hashtag_name.setText(checkHashTag(item.getTag_name()));
		eventNameTextView.setText(item.getEvent_name());
		eventNameTextView.setTag(item.event_id);
		eventNameTextView.setOnClickListener(onClickListener);
		tv_comment.setText(checkHashTag(item.getComment()));
		memreas_event_profile_name.setText(item.getCommenter_name());
		memreasImageLoader.displayImage(item.getCommenter_photo(),
				memreas_event_profile, optionsGallery,
				animateProfileFirstListener);
		media_map = item.getEvent_media_url();

		for (Entry<String, String> entry : media_map.entrySet()) {
			String url = entry.getKey();
			String type = entry.getValue();

			// Inflate media layout
			View mediaView = LayoutInflater.from(getContext()).inflate(
					R.layout.search_popupwindow_tagcomment_item_ref_hscroll_media_item,
					parent, false);

			// Loop through the media map and add the thumbnails
			FrameLayout eventThumbsFrameLayout = (FrameLayout) mediaView
					.findViewById(R.id.eventThumbsFrameLayout);
			ImageView eventThumbsImageView = (ImageView) mediaView
					.findViewById(R.id.eventThumbsImageView);
			ImageView eventThumbsImagePlayIconView = (ImageView) mediaView
					.findViewById(R.id.eventThumbsImagePlayIconView);

			if (type.equalsIgnoreCase("video")) {
				eventThumbsImagePlayIconView.setVisibility(View.VISIBLE);
			} else {
				eventThumbsImagePlayIconView.setVisibility(View.GONE);
			}

			// add to the layout
			mediaView.setTag(item.event_id);
			mediaView.setOnClickListener(onClickListener);
			eventThumbsLinearLayout.addView(mediaView);
			memreasImageLoader.displayImage(url, eventThumbsImageView,
					optionsGallery, animateProfileFirstListener);
		}

		return convertView;
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context, ViewMemreasActivity.class);
			intent.putExtra("event_id", v.getTag().toString());
			context.startActivity(intent);
		}
	};

	public SpannableString checkHashTag(String text) {
		SpannableString hashText = new SpannableString(text);
		Matcher matcher = Pattern.compile("#([A-Za-z0-9_-]+)")
				.matcher(hashText);
		while (matcher.find()) {
			hashText.setSpan(new ForegroundColorSpan(Color.GREEN),
					matcher.start(), matcher.end(), 0);
		}
		return hashText;
	}

} // end class
