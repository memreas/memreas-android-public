
package com.memreas.share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.memreas.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressLint("HandlerLeak")
public class EventCommentsAdapter extends ArrayAdapter<EventComment> {
	private DisplayImageOptions options;
	private MediaPlayer mMediaPlayer;
	private int mPlayingPosition = -1;
	// private ViewHolder mCurrentViewHolder;
	private DecimalFormat mTimeFormater = new DecimalFormat("00");
	private boolean isPause = false;
	private ListView mList;
	private Set<Integer> hidingPositions = new LinkedHashSet<Integer>();

	private String getTime(int sec) {
		sec /= 1000;
		int minute = sec / 60;
		return mTimeFormater.format(minute) + ":"
				+ mTimeFormater.format(sec % 60);
	}

	private Handler mHandler;

	public EventCommentsAdapter(Context context, List<EventComment> items,
			MediaPlayer mediaPlayer, ListView list) {
		super(context, R.layout.event_comment_item, items);
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnLoading(R.drawable.profile_img)
				.showImageOnFail(R.drawable.profile_img)
				.showImageForEmptyUri(R.drawable.profile_img)
				.displayer(new SimpleBitmapDisplayer()).build();
		mList = list;
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				try {
					if (mList != null) {

						try {
							View v = mList.getChildAt(mPlayingPosition);
							TextView tvDuration = (TextView) v
									.findViewById(R.id.tv_duration);
							SeekBar seekBar = (SeekBar) v
									.findViewById(R.id.seek_bar);
							if (mMediaPlayer.isPlaying()) {
								seekBar.setProgress((int) (mMediaPlayer
										.getCurrentPosition() * 100.0 / mMediaPlayer
										.getDuration()));
								Log.d("MEDIAPLAYER",
										"Play: "
												+ mMediaPlayer
												.getCurrentPosition()
												+ "/"
												+ mMediaPlayer.getDuration());
								tvDuration.setText(getTime(mMediaPlayer
										.getCurrentPosition()));
							} else {
								if (!isPause) {
									seekBar.setProgress(100);
									tvDuration.setText(getTime(mMediaPlayer
											.getDuration()));
								}
							}
						} catch (Exception e) {

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mHandler.sendEmptyMessage(1);
						}
					}, 100);
				}
			}
		};
		mMediaPlayer = mediaPlayer;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null)
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.event_comment_item, parent, false);
		holder = new ViewHolder();
		holder.position = position;
		holder.avatar = (ImageView) convertView
				.findViewById(R.id.iv_user_avatar);
		holder.layoutCommentAudio = convertView
				.findViewById(R.id.layout_comment_audio);
		holder.layoutContent = convertView.findViewById(R.id.layout_content);
		holder.layoutCommentText = convertView
				.findViewById(R.id.layout_comment_text);
		holder.tvCommentText = (TextView) convertView
				.findViewById(R.id.tv_comment_text);
		holder.tvDuration = (TextView) convertView
				.findViewById(R.id.tv_duration);
		holder.icPlay = (ImageView) convertView.findViewById(R.id.btn_play);
		holder.seekBar = (SeekBar) convertView.findViewById(R.id.seek_bar);
		holder.seekBar.setEnabled(false);
		holder.btnShowHide = (Button) convertView
				.findViewById(R.id.btn_showhide);
		holder.tvUsername = (TextView) convertView
				.findViewById(R.id.tv_username);
		holder.tvTime = (TextView) convertView
				.findViewById(R.id.tv_time);
		// convertView.setTag(holder);
		EventComment item = getItem(position);
		holder.tvCommentText.setText(item.getCommentText());
		holder.tvTime.setText(item.getTimeString());
		ImageLoader.getInstance().displayImage(item.getProfilePicture(),
				holder.avatar, options);
		holder.tvUsername.setText(item.getUserName());
		if (item.getType().equals("audio")) {
			holder.layoutCommentText.setVisibility(View.GONE);
			holder.layoutCommentAudio.setVisibility(View.VISIBLE);
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
						stopMediaPlayer();
					} else {
						playAudio(holder, position);
					}
				}
			});
			if (mPlayingPosition == position) {
				// mCurrentViewHolder = holder;
				try {
					holder.seekBar.setProgress((int) (mMediaPlayer
							.getCurrentPosition() * 100.0 / mMediaPlayer
							.getDuration()));
					holder.tvDuration.setText(getTime(mMediaPlayer
							.getCurrentPosition()));
				} catch (Exception e) {

				}
			} else {
				holder.seekBar.setProgress(0);
				holder.tvDuration.setText("00:00");
			}
		} else {
			holder.layoutCommentText.setVisibility(View.VISIBLE);
			holder.layoutCommentAudio.setVisibility(View.GONE);
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
		}
		if(hidingPositions.contains(position)){
			holder.layoutContent.setVisibility(View.GONE);
			holder.btnShowHide.setText("Show");
		}else{
			holder.layoutContent.setVisibility(View.VISIBLE);
			holder.btnShowHide.setText("Hide");
		}
		holder.btnShowHide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!hidingPositions.contains(position)){
					holder.layoutContent.setVisibility(View.GONE);
					holder.btnShowHide.setText("Show");
					hidingPositions.add(position);
				}else{
					holder.layoutContent.setVisibility(View.VISIBLE);
					holder.btnShowHide.setText("Hide");
					hidingPositions.remove(position);
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		View layoutCommentText, layoutCommentAudio;
		ImageView avatar, icPlay;
		Button btnShowHide;
		TextView tvDuration, tvCommentText, tvUsername, tvTime;
		SeekBar seekBar;
		View layoutContent;
		int position;
	}

	public void stopMediaPlayer() {
		try {
			if (mMediaPlayer != null) {
				mMediaPlayer.stop();
			}

		} catch (Exception e) {
		}
	}

	public void playAudio(ViewHolder holder, int position) {
		mPlayingPosition = position;
		// mCurrentViewHolder = holder;
		isPause = false;

		mMediaPlayer.reset();
		try {
			holder.seekBar.setProgress(0);
			holder.tvDuration.setText("00:00");
		} catch (Exception e) {
		}
		try {
			mMediaPlayer.setDataSource(getContext(),
					Uri.parse(getItem(position).getAudioUrl()));
			mMediaPlayer.prepareAsync();
			mMediaPlayer
					.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mp) {
							mHandler.sendEmptyMessage(1);
							mMediaPlayer.start();
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mPlayingPosition = -1;
	}

}
