package com.memreas.util;

import java.io.IOException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.memreas.R;

public class MemreasMediaPlayer {

	public Activity activity;
	public static MediaPlayer mPlayer;
	public ImageView event_media_play;
	public ImageView event_media_expand;			
	public SeekBar event_seek_bar;
	public TextView event_media_duration;
	public DecimalFormat mTimeFormater = new DecimalFormat("00");
	public int mProgress = 0;
	public boolean isPlaying = false;
	public String media_url;


	public MemreasMediaPlayer(Activity activity) {
		this.activity = activity;
		prepPlayer();
	}

	public void prepPlayer() {
		if (mPlayer == null)  {
			mPlayer = new MediaPlayer();
		} else if (mPlayer.isPlaying()) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
			mPlayer = new MediaPlayer();
		}
	}

	public String fetchDuration() throws Exception {
		String time = "00:00";
		try {
			mPlayer = MediaPlayer.create(activity, Uri.parse(media_url));
			time = getTime(mPlayer.getDuration());
			mPlayer.release();
			mPlayer = null;
		} catch (Exception e) {
			Log.e(getClass().getName(), "fetchDuration() failed");
			throw e;
		}
		return time;
	}

	public void startPlaying() throws IOException {
		try {
			MemreasProgressDialog.getInstance(activity).setAndShow(
					"audio loading...");
			;
			prepPlayer();
			mPlayer.setDataSource(activity, Uri.parse(media_url));
			mPlayer.prepare();
			mPlayer.setOnCompletionListener(completeListener);
		} catch (IOException e) {
			Log.e(getClass().getName(), "mPlayer.prepare() failed");
			throw e;
		}
		event_seek_bar.setEnabled(true);
		event_seek_bar.setMax(mPlayer.getDuration());
		event_seek_bar.setOnSeekBarChangeListener(seekBarChangeListener);
		MemreasProgressDialog.getInstance(activity).dismiss();
		mPlayer.start();
		isPlaying = true;
		event_media_play.setImageResource(R.drawable.ic_stop_audio);
		mPlayerRunnable.run();
	}

	public void stopPlaying() {
		event_media_play.setImageResource(R.drawable.ic_play_audio);
		try {
			mPlayer.release();
		} catch (Exception e) {
			// do nothing
		}
		mPlayer = null;
		event_seek_bar.setEnabled(false);
		event_seek_bar.setOnSeekBarChangeListener(null);
		isPlaying = false;
	}

	private Handler mHandler = new Handler();
	private Runnable mPlayerRunnable = new Runnable() {
		@Override
		public void run() {
			if ((mPlayer != null) && (isPlaying)
					&& (mProgress < mPlayer.getDuration())) {
				event_seek_bar.setProgress(mPlayer.getCurrentPosition());
				event_media_duration.setText(getTime(mPlayer.getDuration()
						- mPlayer.getCurrentPosition()));
				mHandler.postDelayed(this, 500);
				SystemClock.sleep(500);
			}
		}
	};

	private String getTime(long millisecond) {
		millisecond /= 1000;
		long minute = millisecond / 60;
		return mTimeFormater.format(minute) + ":"
				+ mTimeFormater.format(millisecond % 60);
	}

	public OnCompletionListener completeListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			// reset audio
			if (mPlayer != null) {
				mPlayer.seekTo(0);
				event_media_play.setImageResource(R.drawable.ic_play_audio);
				event_seek_bar.setProgress(0);
				event_media_duration.setText(getTime(mPlayer.getDuration()));
				mPlayer.release();
				mPlayer = null;
			}
		}
	};

	public OnErrorListener errorListener = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			mp.release();
			return true;
		}
	};


	public OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			int progress = seekBar.getProgress();
			mPlayer.seekTo(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if ((mPlayer != null) && fromUser) {
				event_seek_bar.setProgress(progress);
			}
		}
	};
} // end MemreasAudioPlayer