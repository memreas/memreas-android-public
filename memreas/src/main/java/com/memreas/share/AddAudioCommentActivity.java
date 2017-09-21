package com.memreas.share;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;

public class AddAudioCommentActivity extends BaseActivity {

	public static int requestCode = 1004;
	private EditText addCommentEditText;
	private Button mOkBtn;
	private Button mCancelBtn;
	private ImageView mBtnCancel;
	private ImageView mBtnRecord;
	private ImageView mBtnPlay;
	private ImageView mBtnSubmit;
	private TextView mTvDuration;
	private TextView mTvMessage;
	private SeekBar mSeekBar;
	private boolean isRecording = false;
	private boolean isPlaying = false;
	private boolean isPaused = false;
	private MediaPlayer mPlayer;
	private MediaRecorder mRecorder;
	private String mFileName;
	private String mFilePath;
	private File mFile;
	private Uri mFileUri;
	private boolean holdAudioFileForSubmit = false;
	private DecimalFormat mTimeFormater = new DecimalFormat("00");
	private int mProgress = 0;
	private boolean isExistingEvent = false;
	private int current_location = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

		int contentView = R.layout.screen_audio_comment;
		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			contentView = getIntent().getExtras().getInt("contentView");
			current_location = getIntent().getExtras().getInt("current_location");
			isExistingEvent = true;
		}
		setContentView(contentView);

		// Setup Add Share
		mBtnCancel = (ImageView) findViewById(R.id.btnCancel);
		mBtnRecord = (ImageView) findViewById(R.id.btn_record);
		mBtnPlay = (ImageView) findViewById(R.id.btn_play);
		mBtnSubmit = (ImageView) findViewById(R.id.btn_submit);
		mTvDuration = (TextView) findViewById(R.id.tv_duration);
		mTvMessage = (TextView) findViewById(R.id.tv_msg);
		mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
		mSeekBar.setEnabled(false);
		addCommentEditText = (EditText) findViewById(R.id.addCommentEditText);
		//addCommentEditText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
		//addCommentEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mBtnCancel.setOnClickListener(cancelListener);
		mBtnRecord.setOnClickListener(recordListener);
		mBtnPlay.setOnClickListener(playListener);
		mBtnSubmit.setOnClickListener(submitListener);

		// Setup Add Share
		if (isExistingEvent) {
			mCancelBtn = (Button) findViewById(R.id.cancelBtn);
			mOkBtn = (Button) findViewById(R.id.okBtn);
			mOkBtn.setOnClickListener(okListener);
			mCancelBtn.setOnClickListener(cancelListener);
		}

		if ((savedInstanceState != null)
				&& (savedInstanceState.getString("mFilePath") != null)) {
			mFilePath = savedInstanceState.getString("mFilePath");
		}
		if ((this.getIntent() != null)
				&& (this.getIntent().getExtras() != null)) {
			Bundle bundle = this.getIntent().getExtras();
			if (bundle.getString("mFilePath") != null) {
				// setup player if prior recording...
				mFilePath = bundle.getString("mFilePath");
				mBtnPlay.setVisibility(View.VISIBLE);
				if (!isExistingEvent) {
					mBtnSubmit.setVisibility(View.VISIBLE);
				}
				getAudioFilePathAndName();
				mTvDuration.setText(getTime(bundle.getInt("mProgress", 0)));
			}

		}

	}

	OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			releaseResources();
			onActivityResult(AddAudioCommentActivity.requestCode,
					RESULT_CANCELED, null);
		}
	};

	OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mFile != null) {
				holdAudioFileForSubmit = true;
			}
			releaseResources();
			Intent data = new Intent();
			data.putExtra("mFilePath", mFilePath);
			data.putExtra("mProgress", mProgress);
			data.putExtra("current_location", current_location);
			onActivityResult(AddAudioCommentActivity.requestCode, RESULT_OK,
					data);
		}
	};

	OnClickListener okListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (isPlaying) {
				stopPlaying();
			}
			if (isRecording) {
				stopRecording();
			}

			if (mFile != null) {
				holdAudioFileForSubmit = true;
			}
			releaseResources();
			Intent data = new Intent();
			if (!TextUtils.isEmpty(addCommentEditText.getText())) {
				data.putExtra("addCommentEditText", addCommentEditText
						.getText().toString());
			}
			data.putExtra("mFilePath", mFilePath);
			data.putExtra("mProgress", mProgress);
			onActivityResult(AddAudioCommentActivity.requestCode, RESULT_OK,
					data);
		}
	};

	OnClickListener recordListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if ((!isRecording) && (!isPlaying)) {
				startRecording();
			} else {
				stopRecording();
			}
		}
	};

	public void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// mp3
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(getAudioFilePathAndName());
		mRecorder.setMaxDuration(Common.AUDIO_COMMENT_MAX_TIME);
		mRecorder.setOnErrorListener(recordErrorListener);
		mRecorder.setOnInfoListener(recordInfoListener);
		mBtnRecord.setImageResource(R.drawable.record_microphone3);
		mSeekBar.setMax(Common.AUDIO_COMMENT_MAX_TIME);
		mProgress = 0;
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(getClass().getName(), "mRecorder.prepare() failed");
		}
		mRecorder.start();
		isRecording = true;
		mRecordingRunnable.run();
	}

	private void stopRecording() {
		mBtnRecord.setImageResource(R.drawable.record_microphone2);
		isRecording = false;
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		mBtnPlay.setVisibility(View.VISIBLE);
		if (!isExistingEvent) {
			mBtnSubmit.setVisibility(View.VISIBLE);
		}
	}

	private Handler mHandler = new Handler();
	private Runnable mRecordingRunnable = new Runnable() {
		@Override
		public void run() {
			if ((mRecorder != null) && (isRecording)
					&& (mProgress < Common.AUDIO_COMMENT_MAX_TIME)) {
				mSeekBar.setProgress((mProgress));
				mTvDuration.setText(getTime(mProgress));
				mHandler.postDelayed(this, 1000);
				mProgress += 1000;
				SystemClock.sleep(1000);
			}
		}
	};

	private String getTime(long millisecond) {
		millisecond /= 1000;
		long minute = millisecond / 60;
		return mTimeFormater.format(minute) + ":"
				+ mTimeFormater.format(millisecond % 60);
	}

	private String getAudioFilePathAndName() {
		if (mFilePath == null) {
			mFileName = UUID.randomUUID().toString() + Common.AUDIO_EXT_MP3; // .mp3
			mFile = new File(this.getFilesDir(), mFileName);
			mFilePath = mFile.getAbsolutePath();
		} else {
			mFile = new File(mFilePath);
			mFileName = mFile.getName();
		}

		return mFilePath;
	}

	/*
	 * Play Section
	 */
	OnClickListener playListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if ((!isRecording) && (!isPlaying)) {
				startPlaying();
			} else {
				stopPlaying();
			}
		}
	};

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFilePath);
			mPlayer.prepare();
		} catch (IOException e) {
			Log.e(getClass().getName(), "mPlayer.prepare() failed");
		}
		mSeekBar.setEnabled(true);
		mSeekBar.setMax(mPlayer.getDuration());
		mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		mPlayer.start();
		isPlaying = true;
		mBtnPlay.setImageResource(R.drawable.ic_stop_audio);
		mPlayerRunnable.run();
	}

	private Runnable mPlayerRunnable = new Runnable() {
		@Override
		public void run() {
			if ((mPlayer != null) && (isPlaying)
					&& (mProgress < mPlayer.getDuration())) {
				mSeekBar.setProgress(mPlayer.getCurrentPosition());
				mTvDuration.setText(getTime(mPlayer.getDuration()
						- mPlayer.getCurrentPosition()));
				mHandler.postDelayed(this, 500);
				SystemClock.sleep(500);
			}
		}
	};

	private void stopPlaying() {
		mBtnPlay.setImageResource(R.drawable.ic_play_audio);
		mPlayer.release();
		mPlayer = null;
		mSeekBar.setEnabled(false);
		mSeekBar.setOnSeekBarChangeListener(null);
		isPlaying = false;
	}

	OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

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
				mSeekBar.setProgress(progress);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(resultCode, data);
			finish();
		} else if (resultCode == RESULT_CANCELED) {
			setResult(resultCode, data);
			releaseResources();
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		releaseResources();
		super.onDestroy();
	}

	private void deleteTempFile() {
		try {
			if ((mFile != null) && mFile.exists()) {
				mFileName = null;
				mFilePath = null;
				mFile.delete();
			}
		} catch (Exception e) {
			// ignore...
		}
	}

	private void releaseResources() {
		try {
			if (!holdAudioFileForSubmit) {
				deleteTempFile();
			}
		} catch (Exception e) {
			// ignore...
		}

		try {
			if (mRecorder != null) {
				mRecorder.release();
				mRecorder = null;
			}
		} catch (Exception e) {
			// ignore...
		}

		try {
			if (mPlayer != null) {
				mPlayer.release();
				mPlayer = null;
			}
		} catch (Exception e) {
			// ignore...
		}
	}

	private MediaRecorder.OnErrorListener recordErrorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AddAudioCommentActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener recordInfoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
		}
	};
}
