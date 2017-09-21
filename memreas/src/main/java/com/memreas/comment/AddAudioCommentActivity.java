package com.memreas.comment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

public class AddAudioCommentActivity
		extends BaseActivity {

	private MediaRecorder mRecorder = null;
	private ImageView mBtnRecord, mBtnPlay;
	private TextView mTvDuration, mTvMessage;
	private SeekBar mSeekBar;
	public static final String AUDIO_RECORDER_FOLDER = "memreas-audio";
	MediaPlayer mMediaplayer;
	private boolean isRecording = false, isRecorded = false, isPaused = false;
	private CountDownTimer mCountDownTimer;

	String fileName = null;
	private String eventId;
	private String mediaId;

	private DecimalFormat mTimeFormater = new DecimalFormat("00");

	private String getTime(long milisecond) {
		milisecond /= 1000;
		long minute = milisecond / 60;
		return mTimeFormater.format(minute) + ":"
				+ mTimeFormater.format(milisecond % 60);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {

				try {
					if (mMediaplayer.isPlaying()) {
						mSeekBar.setProgress((int) (mMediaplayer
								.getCurrentPosition() * 100.0 / mMediaplayer
								.getDuration()));
						mTvDuration.setText(getTime(mMediaplayer
								.getCurrentPosition()));
					} else {
						if (!isPaused) {
							mSeekBar.setProgress(100);
							mTvDuration.setText(getTime(mMediaplayer
									.getDuration()));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mMediaplayer != null && mMediaplayer.isPlaying()) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mHandler.sendEmptyMessage(1);
						}
					}, 100);
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
		setContentView(R.layout.screen_audio_comment);
		eventId = getIntent().getStringExtra("event_id");
		mediaId = getIntent().getStringExtra("media_id");

		fileName = getFilename();

		mBtnRecord = (ImageView) findViewById(R.id.btn_record);
		mBtnPlay = (ImageView) findViewById(R.id.btn_play);
		mTvDuration = (TextView) findViewById(R.id.tv_duration);
		mTvMessage = (TextView) findViewById(R.id.tv_msg);
		mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
		mSeekBar.setMax(100);
		mSeekBar.setEnabled(false);

		mBtnRecord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isRecording) {
					if (mMediaplayer != null && mMediaplayer.isPlaying()) {
						mMediaplayer.stop();
					}
					startRecording();
				} else {
					stopRecording();
				}
			}
		});

		mBtnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mMediaplayer != null && mMediaplayer.isPlaying()) {
					pauseAudio();
				} else {
					playAudio();
				}
			}
		});
	}

	public void DoneBtn(View v) {
		finish();
	}

	public void save(View v) {

		if (isAudioComment) {
			/*
			 * TODO - Refactor upload / download 
			 */

//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//				new AddAudioCommentToServer(AddAudioCommentActivity.this,
//						eventId, mediaId) {
//					public void onSuccess() {
//						sendBroadcast(new Intent(
//								"com.memreas.comment.AddedAudioComment"));
//						finish();
//					};
//				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileName);
//			else
//				new AddAudioCommentToServer(AddAudioCommentActivity.this,
//						eventId, mediaId) {
//					public void onSuccess() {
//						sendBroadcast(new Intent(
//								"com.memreas.comment.AddedAudioComment"));
//						finish();
//					};
//				}.execute(fileName);

		}
	}

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,
				AddAudioCommentActivity.AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		String fileName = file.getAbsolutePath() + "/" + new Date().getTime()
				+ ".mp3";
		file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (fileName);
	}

	private void startRecording() {
		isRecording = true;
		mBtnRecord.setImageResource(R.drawable.record_microphone3);
		mBtnPlay.setImageResource(R.drawable.ic_play_audio);

		mBtnPlay.setEnabled(false);

		mRecorder = new MediaRecorder();
		isAudioComment = false;
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(fileName);

		mRecorder.setOnErrorListener(errorListener);
		mRecorder.setOnInfoListener(infoListener);
		mRecorder.setMaxDuration(Common.AUDIO_COMMENT_MAX_TIME);
		mCountDownTimer = new CountDownTimer(Common.AUDIO_COMMENT_MAX_TIME, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				mSeekBar.setProgress((int) (100 - millisUntilFinished * 100.0
						/ Common.AUDIO_COMMENT_MAX_TIME));
				if (mRecorder != null) {
					mTvDuration.setText(getTime(Common.AUDIO_COMMENT_MAX_TIME
							- millisUntilFinished));
				}
			}

			@Override
			public void onFinish() {
				mSeekBar.setProgress(100);
				stopRecording();
			}
		};

		try {
			mRecorder.prepare();
			mRecorder.start();
			mCountDownTimer.start();
			mHandler.sendEmptyMessage(2);
			mTvMessage.setText("Recording...");
		} catch (Exception e) {
			mBtnRecord.setImageResource(R.drawable.record_microphone2);
			isRecording = false;
		}
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {

		@Override
		public void onError(MediaRecorder mr, int what, int extra) {

			Toast.makeText(AddAudioCommentActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {

		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {

		}
	};

	private void stopRecording() {
		mTvMessage.setText("Start Now");
		mBtnPlay.setEnabled(true);
		isRecording = false;
		isRecorded = true;

		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		mBtnRecord.setImageResource(R.drawable.record_microphone2);
		if (null != mRecorder) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			isAudioComment = true;
			save(null);
		}
	}

	public void playAudio() {
		if (isRecorded) {
			isPaused = false;
			mBtnPlay.setImageResource(R.drawable.ic_stop_audio);
			try {
				if (mMediaplayer == null) {
					mMediaplayer = new MediaPlayer();
				} else {
					mMediaplayer.reset();
				}
				mMediaplayer.setDataSource(this,
						Uri.fromFile(new File(fileName)));

				mMediaplayer
						.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

							@Override
							public void onPrepared(MediaPlayer mp) {
								mMediaplayer.start();
								mHandler.sendEmptyMessage(1);
							}
						});
				mMediaplayer.prepareAsync();
				mMediaplayer
						.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								mBtnPlay.setImageResource(R.drawable.ic_play_audio);
							}
						});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void pauseAudio() {
		mBtnPlay.setImageResource(R.drawable.ic_play_audio);
		mMediaplayer.pause();
		isPaused = true;
	}

	@Override
	protected void onDestroy() {
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		if (mMediaplayer != null) {
			mMediaplayer.release();
			mMediaplayer = null;
		}
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		super.onDestroy();
	}
}
