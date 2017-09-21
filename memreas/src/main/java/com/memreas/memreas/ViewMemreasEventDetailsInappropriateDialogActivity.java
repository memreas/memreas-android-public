
package com.memreas.memreas;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.util.MemreasProgressDialog;
import com.memreas.util.MemreasVideoViewer;

public class ViewMemreasEventDetailsInappropriateDialogActivity extends
		BaseActivity {

	public static int requestCode = 1006;
	protected MemreasVideoViewer memreasVideoViewer;
	protected View layoutView;
	protected MemreasProgressDialog mProgressDialog;
	protected int position;
	protected ImageButton imgChkBoxOffensiveSexualContent;
	protected TextView txtOffensiveSexualContent;
	protected ImageButton imgChkBoxOffensiveViolentContent;
	protected TextView txtOffensiveViolentContent;
	protected ImageButton imgChkBoxOffensiveHateContent;
	protected TextView txtOffensiveHateContent;
	protected ImageButton imgChkBoxOffensiveOtherContent;
	protected TextView txtOffensiveOtherContent;
	protected Button okBtn;
	protected Button cancelBtn;
	protected ArrayList<String> strTypeList;
	protected enum Type {SEXUAL, VIOLENT, HATE, OTHER};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.memreas_event_details_inappropriate_dialog);
		strTypeList = new ArrayList<String>();

		Bundle data = getIntent().getExtras();
		int position = data.getInt("position");
		ImageButton imgChkBoxOffensiveSexualContent = (ImageButton) findViewById(R.id.imgChkBoxOffensiveSexualContent);
		imgChkBoxOffensiveSexualContent.setTag(new ViewHolder(Type.SEXUAL));
		imgChkBoxOffensiveSexualContent.setOnClickListener(ckBoxListener);
		TextView txtOffensiveSexualContent = (TextView) findViewById(R.id.txtOffensiveSexualContent);

		ImageButton imgChkBoxOffensiveViolentContent = (ImageButton) findViewById(R.id.imgChkBoxOffensiveViolentContent);
		imgChkBoxOffensiveViolentContent.setTag(new ViewHolder(Type.VIOLENT));
		imgChkBoxOffensiveViolentContent.setOnClickListener(ckBoxListener);
		TextView txtOffensiveViolentContent = (TextView) findViewById(R.id.txtOffensiveViolentContent);

		ImageButton imgChkBoxOffensiveHateContent = (ImageButton) findViewById(R.id.imgChkBoxOffensiveHateContent);
		imgChkBoxOffensiveHateContent.setTag(new ViewHolder(Type.HATE));
		imgChkBoxOffensiveHateContent.setOnClickListener(ckBoxListener);
		TextView txtOffensiveHateContent = (TextView) findViewById(R.id.txtOffensiveHateContent);

		ImageButton imgChkBoxOffensiveOtherContent = (ImageButton) findViewById(R.id.imgChkBoxOffensiveOtherContent);
		imgChkBoxOffensiveOtherContent.setTag(new ViewHolder(Type.OTHER));
		imgChkBoxOffensiveOtherContent.setOnClickListener(ckBoxListener);
		TextView txtOffensiveOtherContent = (TextView) findViewById(R.id.txtOffensiveOtherContent);

		Button okBtn = (Button) findViewById(R.id.okBtn);
		okBtn.setOnClickListener(okListener);
		Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(cancelListener);
	}

	OnClickListener ckBoxListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ImageButton imgBtn = (ImageButton) v;
			ViewHolder holder = (ViewHolder) v.getTag();
			if (!holder.isSelected) {
				imgBtn.setImageResource(R.drawable.selected);
				strTypeList.add(holder.type.toString());
				holder.isSelected=true;
			} else {
				imgBtn.setImageResource(R.drawable.unselected);
				strTypeList.remove(holder.type.toString());
			}
		}
	};
	
	
	OnClickListener okListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (strTypeList.size() == 0) {
				Toast.makeText(ViewMemreasEventDetailsInappropriateDialogActivity.this, "no selection chosen...",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			Intent data = new Intent();
			data.putStringArrayListExtra("reasonTypeList", strTypeList);
			onActivityResult(
					ViewMemreasEventListCommentsDialogActivity.requestCode,
					RESULT_OK, data);
		}
	};
	
	OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onActivityResult(
					ViewMemreasEventListCommentsDialogActivity.requestCode,
					RESULT_CANCELED, null);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
		} else if (resultCode == RESULT_CANCELED) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private class ViewHolder {
		public ViewHolder(Type type) {
			this.type = type;
		}
		public boolean isSelected=false;
		public Type type;
	}
}
