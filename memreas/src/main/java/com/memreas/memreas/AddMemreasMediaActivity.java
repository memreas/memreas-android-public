package com.memreas.memreas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.share.AddShareMediaActivity;

public class AddMemreasMediaActivity extends BaseActivity {

	public static int requestCode = 1002;
	private GridView mediaGridView;
	private Button okBtn;
	private Button cancelBtn;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.memreas_event_gallery_add_media);

		// Setup
		mProgressBar = (ProgressBar) findViewById(R.id.processBar);
		mProgressBar.setVisibility(View.VISIBLE);
		mediaGridView = (GridView) findViewById(R.id.addMemreasMediaGridView);
		MemreasEventsMediaSelectAdapter.getInstance(AddMemreasMediaActivity.this,
				R.layout.shareitem_main);
		mediaGridView.setAdapter(MemreasEventsMediaSelectAdapter.getInstance());
		okBtn = (Button) findViewById(R.id.okBtn);
		okBtn.setOnClickListener(okBtnListener);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(cancelBtnListener);
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
		@Override
		public void onClick(View v) {
			onActivityResult(AddShareMediaActivity.requestCode, RESULT_OK, null);
		}
	};

	OnClickListener cancelBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			MemreasEventsMediaSelectAdapter.getInstance().clearSelectedList();
			MemreasEventsMediaSelectAdapter.getInstance().notifyDataSetChanged();
			onActivityResult(AddShareMediaActivity.requestCode,
					RESULT_CANCELED, null);
		}
	};
}
