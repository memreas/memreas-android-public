
package com.memreas.memreas;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.memreas.MemreasEventBean.CommentShortDetails;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasMediaPlayer;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MemreasEventsCommentsPagerAdapter extends
		FragmentStatePagerAdapter {

	protected FragmentManager fragmentManager;
	protected Activity context;
	protected int resource;
	protected LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected AnimateFirstDisplayListener animateProfileFirstListener;
	protected ImageLoader memreasImageLoader;
	protected LinkedList<CommentShortDetails> mCommentsList;

	public MemreasEventsCommentsPagerAdapter(Activity context,
			FragmentManager fragmentManager) {
		super(fragmentManager);
		this.context = context;
		this.fragmentManager = fragmentManager;
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
	public Fragment getItem(int position) {
		return new CommentStripFragment(position);
	}

	@Override
	public int getCount() {
		return mCommentsList.size();
	}

	public LinkedList<CommentShortDetails> getmCommentsList() {
		return mCommentsList;
	}

	public void setmCommentsList(LinkedList<CommentShortDetails> mCommentsList) {
		this.mCommentsList = mCommentsList;
		this.notifyDataSetChanged();
	}

	public class CommentStripFragment extends Fragment {
		protected int position;

		public CommentStripFragment(int position) {
			super();
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("position", position);
			this.setArguments(args);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			position = getArguments() != null ? getArguments().getInt(
					"position") : 0;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			/*
			 * Inflate View
			 */
			View convertView = inflater.inflate(
					R.layout.memreas_event_gallery_comment_item, container,
					false);

			/*
			 * Initialize ViewHolder
			 */
			ViewHolder holder = new ViewHolder();

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
			holder.mAudioPlayer.event_seek_bar = (SeekBar) convertView
					.findViewById(R.id.event_seek_bar);
			holder.mAudioPlayer.event_media_duration = (TextView) convertView
					.findViewById(R.id.event_audio_duration);

			/*
			 * Set specific items here
			 */
			CommentShortDetails commentShortDetails = mCommentsList
					.get(position);
			memreasImageLoader.displayImage(commentShortDetails.profilePicUrl,
					holder.commenterProfile, optionsGallery,
					animateProfileFirstListener);
			if (!TextUtils.isEmpty(commentShortDetails.text)) {
				holder.commentTextView.setText(commentShortDetails.text);
				holder.memreas_event_comment_text_framelayout
						.setVisibility(View.VISIBLE);
			} else {
				holder.memreas_event_comment_text_framelayout
						.setVisibility(View.GONE);
			}

			if (commentShortDetails.audio_media_url != null) {
				holder.mAudioPlayer.media_url = commentShortDetails.audio_media_url;
				holder.mAudioPlayer.event_media_play.setOnClickListener(playListener);
				holder.mAudioPlayer.event_seek_bar
						.setOnSeekBarChangeListener(holder.mAudioPlayer.seekBarChangeListener);
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
				//show base no comment msg at a minimum...
				holder.memreas_event_comment_text_framelayout
				.setVisibility(View.VISIBLE);
			}

			holder.mAudioPlayer.event_media_play.setTag(holder);
			return convertView;
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
		}
	}

}
