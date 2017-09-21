package com.memreas.share;

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

public class AddShareMediaActivity extends BaseActivity {

	public static int requestCode = 1002;
	private GridView mediaGridView;
	private Button okBtn;
	private Button cancelBtn;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_page_add_media);

		// Setup
		mProgressBar = (ProgressBar) findViewById(R.id.processBar);
		mProgressBar.setVisibility(View.VISIBLE);
		mediaGridView = (GridView) findViewById(R.id.gridview);
		ShareMediaAdapter.getInstance(AddShareMediaActivity.this,
				R.layout.shareitem_main);
		mediaGridView.setAdapter(ShareMediaAdapter.getInstance());
		okBtn = (Button) findViewById(R.id.okBtn);
		okBtn.setOnClickListener(okBtnListener);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(cancelBtnListener);
		mProgressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			ShareMediaSelectedAdapter.getInstance().notifyDataSetChanged();
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
		// setUpMapIfNeeded();
	}

	OnClickListener okBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ShareMediaSelectedAdapter.getInstance().refreshSelectedMedia();
			ShareMediaSelectedAdapter.getInstance().notifyDataSetChanged();
			onActivityResult(AddShareMediaActivity.requestCode, RESULT_OK, null);
		}
	};

	OnClickListener cancelBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ShareMediaAdapter.getInstance().clearCancelList();
			ShareMediaSelectedAdapter.getInstance().refreshSelectedMedia();
			ShareMediaSelectedAdapter.getInstance().notifyDataSetChanged();
			onActivityResult(AddShareMediaActivity.requestCode,
					RESULT_CANCELED, null);
		}
	};
}
