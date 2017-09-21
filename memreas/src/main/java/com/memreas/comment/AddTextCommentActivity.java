package com.memreas.comment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;
import com.memreas.sax.handler.EventCommentHandler;
import com.memreas.sax.handler.SaxParser;
import com.memreas.sax.handler.XMLGenerator;
import com.memreas.share.EventComment;
import com.memreas.share.EventCommentsAdapter;
import com.memreas.share.OptionMemreasActivity;

import java.util.ArrayList;
import java.util.List;

public class AddTextCommentActivity extends BaseActivity implements
		AnimationListener {

	public static final String ACTION_ADDED_COMMENT = "com.memreas.comment.Added";
	private EditText commentText;
	private ListView mListComment;
	private EventCommentsAdapter mAdapter;
	private MediaPlayer mMediaPlayer;
	private String eventId;
	private String mediaId;

	private Animation mAnim = null;

	private final int AS_NONE = 0;
	private final int AS_OPTION = 1;
	private final int AS_OK = 2;
	private final int AS_CANCEL = 3;
	private final int AS_AUDIO = 4;
	public List<EventComment> mComments;

	private int state_Anim = AS_NONE;
	private ProgressBar processBar;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
		setContentView(R.layout.activity_add_comment);

		eventId = getIntent().getStringExtra("event_id");
		mediaId = getIntent().getStringExtra("media_id");
		if (mediaId == null) {
			mediaId = "";
		}
		mComments = (List<EventComment>) getIntent().getSerializableExtra(
				"comments");

		processBar = (ProgressBar) findViewById(R.id.processBar);

		// For hiding soft keyboard
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		commentText = (EditText) findViewById(R.id.addComment);
		mAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.button_click);
		mAnim.setAnimationListener(this);
		mListComment = (ListView) findViewById(R.id.list);
		mMediaPlayer = new MediaPlayer();

		if (mComments != null) {
			setComments(mComments);
		} else {
			new EventCommentParse()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		audioCommentMediaId = "";
		registerReceiver(receiver, new IntentFilter(
				"com.memreas.comment.AddedAudioComment"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	private void setComments(List<EventComment> items) {
		List<EventComment> comments = new ArrayList<EventComment>();
		for (int i = 0; i < items.size(); i++) {
			comments.add(0, items.get(i));
		}
		mAdapter = new EventCommentsAdapter(this, comments, mMediaPlayer,
				mListComment);
		mListComment
				.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mListComment.setAdapter(mAdapter);
		mAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				mListComment.setSelection(mAdapter.getCount() - 1);
			}
		});
	}

	@Override
	public void onAnimationEnd(Animation animation) {

		if (state_Anim == AS_OPTION) {
			startActivityForResult(
					new Intent(this, OptionMemreasActivity.class), 1001);
		}

		if (state_Anim == AS_OK) {
			addComment(AddTextCommentActivity.this, eventId, mediaId,
					commentText.getText().toString().trim(), false);
			setResult(RESULT_OK);
			finish();
		}

		if (state_Anim == AS_CANCEL)
			finish();

		if (state_Anim == AS_AUDIO) {
			Intent intent = new Intent(AddTextCommentActivity.this,
					AddAudioCommentActivity.class);
			intent.putExtra("event_id", eventId);
			intent.putExtra("media_id", mediaId);
			startActivityForResult(intent, 1000);
		}
		state_Anim = AS_NONE;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	public void optionBtn(View v) {

		state_Anim = AS_OPTION;
		v.startAnimation(mAnim);
	}

	public void okButton(View v) {

		state_Anim = AS_OK;
		v.startAnimation(mAnim);
	}

	public void cancelButton(View v) {

		state_Anim = AS_CANCEL;
		v.startAnimation(mAnim);
	}

	public void addCommentAudioBtn(View v) {

		state_Anim = AS_AUDIO;
		v.startAnimation(mAnim);
	}
	public void onClose(View v) {
		finish();
	}

	@Override
	protected void onPause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			try {
				mMediaPlayer.stop();
			} catch (Exception e) {

			}
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		releaseMediaplayer();
		super.onDestroy();
	}

	private void playAudio(String url) {
		releaseMediaplayer();
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepare(); // might take long! (for buffering, etc)
			mMediaPlayer.start();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void releaseMediaplayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private class EventCommentParse extends
			AsyncTask<String, Void, List<EventComment>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			processBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<EventComment> doInBackground(String... params) {
			EventCommentHandler handler = new EventCommentHandler();
			String xmlData = XMLGenerator.getCommentXML(eventId, mediaId, 0,
					1000);
			SaxParser.parse(Common.SERVER_URL + Common.GET_COMMENT_EVENT_ACTION,
					xmlData, handler, "xml");

			return handler.getComments();
		}

		@Override
		protected void onPostExecute(List<EventComment> result) {
			super.onPostExecute(result);
			try {
				if (result != null && result.size() > 0) {
					if (mComments == null) {
						mComments = new ArrayList<EventComment>();
					}

					mComments.addAll(result);
					setComments(mComments);
				}
				processBar.setVisibility(View.GONE);
			} catch (Exception e) {
			}
		}

	}

}