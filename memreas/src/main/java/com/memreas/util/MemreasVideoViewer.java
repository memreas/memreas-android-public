package com.memreas.util;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.text.DecimalFormat;

public class MemreasVideoViewer {

	/**
	 * Note sample code for HLS below
	 */
	// VideoView v = (VideoView) findViewById(R.id.video_view);
	// v.setMediaController(new MediaController(this));
	// v.setOnCompletionListener(this);
	// v.setVideoURI(Uri.parse("http://myurl/resource.m3u8"));
	// v.requestFocus();
	// v.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	// public void onPrepared(MediaPlayer mp) {
	// mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
	// mp.start();
	// mp.seekTo(0);
	// }
	// });
	public Context context;
	public int currentPosition;
	public MediaController mediaController;
	public String media_url;
	public ImageView memreas_event_details_pager_image_view;
	public FrameLayout memreas_event_details_pager_framelayout;
	public ImageView memreas_event_details_pager_play_video_icon;
	public VideoView memreas_event_details_video_view;
	public ContextMenu menu;

	public MemreasVideoViewer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public OnClickListener playVideoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// VideoView memreas_event_details_video_view = (VideoView) v;

			if (memreas_event_details_video_view.isPlaying()) {
				memreas_event_details_video_view.pause();
				memreas_event_details_video_view.setVisibility(View.GONE);
				memreas_event_details_pager_image_view
						.setVisibility(View.VISIBLE);
				memreas_event_details_pager_play_video_icon
						.setVisibility(View.VISIBLE);
			} else {
				memreas_event_details_pager_framelayout.setBackgroundColor(Color.TRANSPARENT);
				memreas_event_details_pager_image_view.setVisibility(View.GONE);
				memreas_event_details_pager_play_video_icon
						.setVisibility(View.GONE);
				// mProgressDialog.setAndShow("preparing media...");
				memreas_event_details_video_view
						.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
				memreas_event_details_video_view.setVisibility(View.VISIBLE);
				memreas_event_details_video_view.requestFocus();
				if (mediaController == null) {
					mediaController = new MediaController(context);
				}
				memreas_event_details_video_view.setVideoURI(Uri
						.parse(media_url));
				mediaController.setAnchorView(memreas_event_details_video_view);
				mediaController
						.setMediaPlayer(memreas_event_details_video_view);
				memreas_event_details_video_view
						.setMediaController(mediaController);
				mediaController.setEnabled(true);
				setListeners();
				memreas_event_details_video_view.requestLayout();
				// mProgressDialog.dismiss();
			}
		}
	};

	public void setListeners() {
		memreas_event_details_video_view
				.setOnPreparedListener(onPreparedListener);
		memreas_event_details_video_view
				.setOnCompletionListener(onCompletionListener);
		memreas_event_details_video_view.setOnErrorListener(onErrorListener);
		memreas_event_details_pager_image_view
				.setOnClickListener(playVideoListener);
		memreas_event_details_pager_play_video_icon
				.setOnClickListener(playVideoListener);
		memreas_event_details_video_view.setOnFocusChangeListener(onFocusChangeListener);
	}

	public void startFullScreenAndSetListeners() {
		memreas_event_details_video_view
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		memreas_event_details_video_view.setVisibility(View.VISIBLE);
		memreas_event_details_video_view.requestFocus();
		if (mediaController == null) {
			mediaController = new MediaController(context);
		}
		memreas_event_details_video_view.setVideoURI(Uri.parse(media_url));
		mediaController.setAnchorView(memreas_event_details_video_view);
		mediaController.setMediaPlayer(memreas_event_details_video_view);
		memreas_event_details_video_view.setMediaController(mediaController);
		mediaController.setEnabled(true);
		memreas_event_details_video_view.requestLayout();
		memreas_event_details_video_view
				.setOnPreparedListener(onPreparedListener);
		memreas_event_details_video_view
				.setOnCompletionListener(onCompletionListener);
		memreas_event_details_video_view.setOnErrorListener(onErrorListener);
		memreas_event_details_video_view.setOnFocusChangeListener(onFocusChangeListener);
	}

	public void pauseVideoView() {
		// TODO Auto-generated method stub
		if (memreas_event_details_video_view.isPlaying()) {
			memreas_event_details_video_view.pause();
		} else {
			memreas_event_details_video_view.resume();
		}
	}

	public OnErrorListener onErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			mp.release();
			return false;
		}
	};

	public OnCompletionListener onCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// reset audio
			try {
				memreas_event_details_video_view.seekTo(0);
				memreas_event_details_video_view.setVisibility(View.GONE);
			} catch (Exception e) {
				//do nothing
			}
			memreas_event_details_pager_image_view.setVisibility(View.VISIBLE);
			memreas_event_details_pager_play_video_icon
					.setVisibility(View.VISIBLE);
		}
	};

	public String getTime(long millisecond) {
		DecimalFormat mTimeFormater = new DecimalFormat("00");

		millisecond /= 1000;
		long minute = millisecond / 60;
		return mTimeFormater.format(minute) + ":"
				+ mTimeFormater.format(millisecond % 60);
	}

	public OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			try {
				if ((v instanceof VideoView) && (!hasFocus)) {
					((VideoView) v).stopPlayback();
				}
			} catch (Exception e) {
				// do nothing
			}
		}
	};
	
	public OnPreparedListener onPreparedListener = new OnPreparedListener() {

		@SuppressWarnings("unused")
		@Override
		public void onPrepared(MediaPlayer mp) {
			if (memreas_event_details_video_view.getCurrentPosition() == 0) {
				memreas_event_details_video_view.start();
			} else if (mp.getCurrentPosition() != 0) {
				// fragmentHolder.memreas_event_details_video_view.seekTo(fragmentHolder.mProgress);
				memreas_event_details_video_view
						.seekTo(memreas_event_details_video_view
								.getCurrentPosition());
				memreas_event_details_video_view.start();
			} else {
				/*
				 * if we come from a resumed activity, video playback will be
				 * paused
				 */
				memreas_event_details_video_view.pause();
			}
		}
	};

	// private MediaPlayer.OnInfoListener mpOnInfoListener = new
	// MediaPlayer.OnInfoListener() {
	//
	// @Override
	// public boolean onInfo(MediaPlayer mp, int what, int extra) {
	// if ((mp.isPlaying())
	// && (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)) {
	// // mp.pause();
	// mp.setOnBufferingUpdateListener(mpOnBufferingUpdateListener);
	// } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
	// // mp.setOnBufferingUpdateListener(null);
	// // mProgressDialog.dismiss();
	// // mp.start();
	// }
	//
	// return false;
	// }
	// };
	//
	// private MediaPlayer.OnBufferingUpdateListener mpOnBufferingUpdateListener
	// = new MediaPlayer.OnBufferingUpdateListener() {
	// @Override
	// public void onBufferingUpdate(MediaPlayer mp, int percent) {
	// // TODO Auto-generated method stub
	// final int currentPercent = percent;
	// try {
	// if ((percent != 100) && mp.isPlaying()) {
	// final Handler myHandler = new Handler();
	// myHandler.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// MemreasProgressDialog mProgressDialog = MemreasProgressDialog
	// .getInstance(context);
	// mProgressDialog.setMessage("video buffering - "
	// + currentPercent + "%");
	// mProgressDialog.setCancelable(true);
	// mProgressDialog.show();
	// }
	// }, 3000);
	// }
	// } catch (Exception e) {
	// // do nothing...
	// }
	// }
	//
	// };
}
