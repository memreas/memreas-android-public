package com.memreas.memreas;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.memreas.MemreasEventBean.CommentShortDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.memreas.util.MemreasMediaPlayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

public class MemreasEventsCommentsListAdapter extends BaseAdapter {

	private static MemreasEventsCommentsListAdapter instance;
	protected Activity context;
	protected int resource;
	protected LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected AnimateFirstDisplayListener animateProfileListener;
	protected ImageLoader memreasImageLoader;
	protected LinkedList<CommentShortDetails> mCommentList;

	public void setMemreasEventsCommentsListAdapterView(Activity context,
			int resource) {
		this.context = context;
		this.resource = resource;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		animateGalleryListener = new AnimateFirstDisplayListener();
		animateGalleryListener.setFailImage(R.drawable.gallery_img);
		animateProfileListener = new AnimateFirstDisplayListener();
		animateProfileListener.setFailImage(R.drawable.profile_img);
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
	}

	protected MemreasEventsCommentsListAdapter() {
	}

	public static MemreasEventsCommentsListAdapter getInstance() {
		// Assumes adapter is set
		if (instance == null) {
			instance = new MemreasEventsCommentsListAdapter();
		}
		return instance;
	}

	public LinkedList<CommentShortDetails> getmCommentList() {
		return mCommentList;
	}

	public void setmCommentList(LinkedList<CommentShortDetails> mCommentList) {
		this.mCommentList = mCommentList;
		this.notifyDataSetChanged();
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

			holder.memreas_event_comment_linearlayout = (LinearLayout) convertView
					.findViewById(R.id.memreas_event_comment_linearlayout);
			holder.commenterProfile = (ImageView) convertView
					.findViewById(R.id.memreas_event_comment_profile);
			holder.memreas_event_comment_text_framelayout = (FrameLayout) convertView
					.findViewById(R.id.memreas_event_comment_text_framelayout);
			holder.memreas_event_comment_audio_framelayout = (FrameLayout) convertView
					.findViewById(R.id.memreas_event_comment_audio_framelayout);
			holder.commentTextView = (TextView) convertView
					.findViewById(R.id.memreas_event_comment_text);
			holder.mAudioPlayer = new MemreasMediaPlayer(context);
			holder.mAudioPlayer.event_media_play = (ImageView) convertView
					.findViewById(R.id.event_audio_play);
			holder.mAudioPlayer.event_media_play.setOnClickListener(playListener);
			holder.mAudioPlayer.event_seek_bar = (SeekBar) convertView
					.findViewById(R.id.event_seek_bar);
			holder.mAudioPlayer.event_media_duration = (TextView) convertView
					.findViewById(R.id.event_audio_duration);
			holder.mAudioPlayer.event_seek_bar
					.setOnSeekBarChangeListener(holder.mAudioPlayer.seekBarChangeListener);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * Set specific items here
		 */
		CommentShortDetails commentShortDetails = mCommentList.get(position);
		memreasImageLoader.displayImage(commentShortDetails.profilePicUrl,
				holder.commenterProfile, optionsGallery, animateProfileListener);
		if (commentShortDetails.text != null) {
			holder.commentTextView.setText(commentShortDetails.text);
			holder.memreas_event_comment_text_framelayout
					.setVisibility(View.VISIBLE);
		} else {
			holder.memreas_event_comment_text_framelayout
					.setVisibility(View.GONE);
		}

		if (commentShortDetails.audio_media_url != null) {
			
			holder.mAudioPlayer.media_url = commentShortDetails.audio_media_url;
			
			String time = "00:00";
			try { 
				time = holder.mAudioPlayer.fetchDuration();
			} catch (Exception e) {
				//do nothing
				Log.e("fetchDuration failed--->", e.getMessage());
			}
			if (time != null) {
				holder.mAudioPlayer.event_media_duration.setText(time);
			}
			holder.memreas_event_comment_audio_framelayout
					.setVisibility(View.VISIBLE);
		} else {
			holder.memreas_event_comment_audio_framelayout
					.setVisibility(View.GONE);
		}

		holder.position = position;
		holder.mAudioPlayer.event_media_play.setTag(holder);
		convertView.setTag(holder);
		return convertView;
	}

	@Override
	public int getCount() {
		if (mCommentList != null) {
			return mCommentList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return mCommentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	/*
	 * Play Section
	 */
	OnClickListener playListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ViewHolder holder = (ViewHolder) v.getTag();
			// TODO Auto-generated method stub
			if (!holder.mAudioPlayer.isPlaying) {
				try { 
					holder.mAudioPlayer.startPlaying();
				} catch (Exception e) {
					//do nothing
					Log.e("playListener failed--->", e.getMessage());
				}
			} else {
				holder.mAudioPlayer.stopPlaying();
			}
		}
	};

	private class ViewHolder {
		public LinearLayout memreas_event_comment_linearlayout;
		public ImageView commenterProfile;
		public FrameLayout memreas_event_comment_text_framelayout;
		public FrameLayout memreas_event_comment_audio_framelayout;
		public TextView commentTextView;
		public MemreasMediaPlayer mAudioPlayer;
		public int position;
	}
}
