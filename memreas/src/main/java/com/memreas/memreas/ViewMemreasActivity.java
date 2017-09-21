package com.memreas.memreas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.memreas.MemreasEventBean.EventType;
import com.memreas.memreas.load.LoadMemreasEventsAsyncTask;
import com.memreas.share.AddShareActivity;
import com.memreas.util.MemreasProgressDialog;

public class ViewMemreasActivity extends BaseActivity {

	private enum LastTab {
		ME, FRIEND, PUBLIC
	};

	public static LastTab lastTab = LastTab.ME;
	public static boolean isPaused = false;
	private boolean isCallBackNeededForEventId = false;
	private String event_id;
	private Button meBtn;
	private Button friendBtn;
	private Button publicBtn;

	private GridView meMemreasGridView;
	private ListView friendMemreasList;
	private ListView publicMemreasList;

	private Button btnAddMemreas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_view_memreas_ref);
		PREV_INDEX = MEMREAS_INDEX;

		// Set full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// disable the keyboard input
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mLoadingDialog = MemreasProgressDialog.getInstance(this);
		mLoadingDialog.setMessage("loading memreas...");
		mLoadingDialog.show();

		meBtn = (Button) this.findViewById(R.id.meBtn);
		friendBtn = (Button) this.findViewById(R.id.friendBtn);
		publicBtn = (Button) this.findViewById(R.id.publicBtn);

		btnAddMemreas = (Button) this.findViewById(R.id.btnAddMemreas);

		meMemreasGridView = (GridView) this
				.findViewById(R.id.meMemreasGridView);
		friendMemreasList = (ListView) this.findViewById(R.id.friendList);
		publicMemreasList = (ListView) this.findViewById(R.id.publicList);

		// Creating View tab so set it
		clickLastTab();

		// Show Ads...
		AdmobView();

		// menuBar
		menuConfiguration();

		// release dialog...
		mLoadingDialog.dismiss();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		memreasReloadFlag = true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		clickLastTab();
		super.onResume();
	}

	public void clickLastTab() {

		/*
		 * Set data as needed for onCreate / onResume...
		 */
		meBtn.setOnClickListener(meTabListener);
		friendBtn.setOnClickListener(friendTabListener);
		publicBtn.setOnClickListener(publicTabListener);
		btnAddMemreas.setOnClickListener(addMemreasButtonListener);

		/*
		 * Load data from Server Async...
		 */
		loadMemreasDataAsync();

		if (lastTab == LastTab.ME) {
			MemreasEventsMeAdapter.getInstance().notifyDataSetChanged();
			meBtn.performClick();
		} else if (lastTab == LastTab.FRIEND) {
			MemreasEventsFriendsAdapter.getInstance().notifyDataSetChanged();
			friendBtn.performClick();
		} else if (lastTab == LastTab.PUBLIC) {
			MemreasEventsPublicAdapter.getInstance().notifyDataSetChanged();
			publicBtn.performClick();
		}

		event_id = getIntent().getStringExtra("event_id");
		if (event_id != null) {
			if (!isCallBackNeededForEventId) {
				onDataLoadFinishCallBack();
			}
		}

		// release dialog...
		mLoadingDialog.dismiss();
	}

	OnClickListener meTabListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Set tab backgrounds
			meBtn.setBackgroundResource(R.drawable.btn_black);
			friendBtn.setBackgroundResource(R.drawable.btn_gray);
			publicBtn.setBackgroundResource(R.drawable.btn_gray);
			lastTab = LastTab.ME;

			// Remove any prior views...
			hideTabViews();

			// Setup this view
			meMemreasGridView.setAdapter(MemreasEventsMeAdapter.getInstance());
			meMemreasGridView.setVisibility(View.VISIBLE);
		}
	};

	OnClickListener friendTabListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Set tab backgrounds
			meBtn.setBackgroundResource(R.drawable.btn_gray);
			friendBtn.setBackgroundResource(R.drawable.btn_black);
			publicBtn.setBackgroundResource(R.drawable.btn_gray);
			lastTab = LastTab.FRIEND;

			// Remove any prior views...
			hideTabViews();

			// Setup this view
			friendMemreasList.setAdapter(MemreasEventsFriendsAdapter
					.getInstance());
			friendMemreasList.setVisibility(View.VISIBLE);
		}
	};

	OnClickListener publicTabListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Set tab backgrounds
			meBtn.setBackgroundResource(R.drawable.btn_gray);
			friendBtn.setBackgroundResource(R.drawable.btn_gray);
			publicBtn.setBackgroundResource(R.drawable.btn_black);
			lastTab = LastTab.PUBLIC;

			// Remove any prior views...
			hideTabViews();

			// Setup this view
			publicMemreasList.setAdapter(MemreasEventsPublicAdapter
					.getInstance());
			publicMemreasList.setVisibility(View.VISIBLE);

		}
	};

	OnClickListener addMemreasButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ViewMemreasActivity.this,
					AddShareActivity.class);
			startActivity(intent);
		}
	};

	public void hideTabViews() {
		meMemreasGridView.setVisibility(View.GONE);
		friendMemreasList.setVisibility(View.GONE);
		publicMemreasList.setVisibility(View.GONE);
	}

	public void loadMemreasDataAsync() {
		/*
		 * Reset adapters section
		 */
		if (memreasReloadFlag == true) {
			MemreasEventsMeAdapter.reset();
			MemreasEventsFriendsAdapter.reset();
			MemreasEventsPublicAdapter.reset();
			MemreasEventFinder.reset();
			isCallBackNeededForEventId = true;
		} else if ((MemreasEventsMeAdapter.asyncTaskComplete != true)
				|| (MemreasEventsFriendsAdapter.asyncTaskComplete != true)
				|| (MemreasEventsPublicAdapter.asyncTaskComplete != true)) {
			isCallBackNeededForEventId = true;
		}

		/*
		 * Setup Adapter Views and base initialize
		 */
		MemreasEventsMeAdapter.getInstance().setMemreasMeAdapterView(
				ViewMemreasActivity.this, R.layout.memreas_me_grid_item);
		MemreasEventsFriendsAdapter.getInstance().setMemreasFriendsAdapterView(
				ViewMemreasActivity.this, R.layout.memreas_friend_list_item);
		MemreasEventsPublicAdapter.getInstance().setMemreasPublicAdapterView(
				ViewMemreasActivity.this, R.layout.memreas_public_list_item);

		/*
		 * Load data from server if available
		 */
		if (mApplication.isOnline() && (memreasReloadFlag == true)) {
			// LoadMemreasEventsAsyncTask ME after
			new LoadMemreasEventsAsyncTask(ViewMemreasActivity.this,
					MemreasEventsMeAdapter.getInstance(), EventType.ME)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			// LoadMemreasEventsAsyncTask FRIENDS after
			new LoadMemreasEventsAsyncTask(ViewMemreasActivity.this,
					MemreasEventsFriendsAdapter.getInstance(),
					EventType.FRIENDS)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			// LoadMemreasEventsAsyncTask PUBLIC after
			new LoadMemreasEventsAsyncTask(ViewMemreasActivity.this,
					MemreasEventsPublicAdapter.getInstance(), EventType.PUBLIC)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			memreasReloadFlag = false;
		}
	}

	public void onDataLoadFinishCallBack() {
		if ((event_id != null) && (!event_id.equalsIgnoreCase(""))) {
			// If search touch sends in event_id then go to event
			MemreasEventFinder.EventShortDetails eventShortDetails = MemreasEventFinder
					.getInstance().find(event_id);
			if (eventShortDetails.sub_position == -1) {
				onItemClickViewEvent(
						Integer.valueOf(eventShortDetails.position), null,
						eventShortDetails.type);
			} else if (eventShortDetails.sub_position != -1) {
				onItemClickViewEvent(
						Integer.valueOf(eventShortDetails.position),
						Integer.valueOf(eventShortDetails.sub_position),
						eventShortDetails.type);
			}
			mLoadingDialog.showWithDelay(
					"finding memreas from search failed...", 2000);
		}
	}

	public void onItemClickViewEvent(Integer position, Integer subPosition,
			MemreasEventBean.EventType type) {
		// Start Image Pager...
		Intent intent = new Intent(this, ViewMemreasEventActivity.class);
		intent.putExtra("position", position.intValue());
		if (subPosition != null) {
			intent.putExtra("subPosition", subPosition.intValue());
		}
		intent.putExtra("type", type.toString());
		startActivity(intent);
	};

}
