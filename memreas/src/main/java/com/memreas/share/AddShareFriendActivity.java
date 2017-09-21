package com.memreas.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.memreas.R;
import com.memreas.base.BaseActivity;

public class AddShareFriendActivity extends BaseActivity {

	public static int requestCode = 1003;
	public static boolean isAsyncFriendLoadComplete = false;
	private Button okBtn;
	private Button cancelBtn;
	private ProgressBar mProgressBar;
	private ListView friendListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_page_add_friends);

		mProgressBar = (ProgressBar) findViewById(R.id.processBar);
		mProgressBar.setVisibility(View.VISIBLE);
		okBtn = (Button) findViewById(R.id.okBtn);
		okBtn.setOnClickListener(okBtnListener);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(cancelBtnListener);

		ShareFriendAdapter.getInstance(AddShareFriendActivity.this,
				R.layout.share_page_add_friends_row_item);
		ShareFriendSelectedAdapter.getInstance(AddShareFriendActivity.this,
				R.layout.share_page_add_friends_row_item);
		friendListView = (ListView) findViewById(R.id.friendListView);
		friendListView.setAdapter(ShareFriendAdapter.getInstance());
		mProgressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		} else if (resultCode == RESULT_CANCELED) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	OnClickListener okBtnListener = new OnClickListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			onActivityResult(AddShareFriendActivity.requestCode, RESULT_OK,
					null);
		}
	};

	OnClickListener cancelBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ShareFriendSelectedAdapter.getInstance() != null) {
				ShareFriendSelectedAdapter.getInstance().clearInstanceFriendsList();
			}
			onActivityResult(AddShareFriendActivity.requestCode,
					RESULT_CANCELED, null);

		}
	};

}
