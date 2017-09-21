package com.memreas.share;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.memreas.R;
import com.memreas.base.BaseActivity;
import com.memreas.gallery.GalleryActivity;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;
import com.memreas.gallery.MediaIdManager;
import com.memreas.memreas.MemreasAddShareBean;
import com.memreas.memreas.ViewMemreasActivity;
import com.memreas.queue.MemreasTransferModel;
import com.memreas.queue.MemreasTransferModel.MemreasQueueStatus;
import com.memreas.queue.MemreasTransferModel.Type;
import com.memreas.queue.QueueAdapter;
import com.memreas.queue.QueueService;
import com.memreas.sax.handler.AddCommentParser;
import com.memreas.sax.handler.AddEventParser;
import com.memreas.sax.handler.AddFriendToShareParser;
import com.memreas.util.MemreasProgressDialog;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class AddShareActivity extends BaseActivity {

	private String TAG = getClass().getName();

	//progress dialog failing in asyntasks...
	public MemreasProgressDialog mDialog;


	// Sections
	private ScrollView layoutMemreasDetails;
	private LinearLayout layoutMediaDetails;

	private Button btnMembersDetails;
	private GridView shareMediaGridView;

	private Button btnFriend;
	private Button btnMedia;
	private Button btnNext;
	private Button btnDone;
	private Button btnCancel;

	// Details...
	private EditText editTxtName;
	private TextView txtInputDate;
	private ImageButton imgBtnDate;
	private EditText editTextLocation;
	private String mAddress = "";
	private LatLng mLatLng;
	private ImageButton cbFriendsCanPost;
	private ImageButton cbFriendsCanAddFriends;
	private ImageButton cbPublic;
	private ImageButton cbViewable;
	private TextView editTextViewableFrom;
	private ImageButton imgBtnViewableFrom;
	private TextView editTextViewableTo;
	private ImageButton imgBtnViewableTo;
	private ImageButton cbGhost;
	private TextView editTextGhost;
	private ImageButton imgBtnGhostCal;
	private MemreasAddShareBean mMemreasAddShareBean;
	private boolean detailsSet = false;
	private int checkboxSelected = R.drawable.selected;
	private int checkboxUnSelected = R.drawable.unselected;

	// Media
	private EditText editTxtComment;
	private ImageView btnAudioComment;
	private String mAudioCommentFilePath;
	private int mAudioCommentDurationInMillis = 0;
	private GalleryBean mAudioBean;
	private QueueService queueService;
	private boolean isQueueBound = false;

	// Friends
	private LinearLayout layoutFriendDetails;
	private ListView friendListView;
	private ImageButton imgbtnCreateGroup;
	private EditText groupName;

	// Web Service
	private AddEventParser mAddEventParser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_share_page_ref);
		PREV_INDEX = SHARE_INDEX;

		//fetch progress dialog to avoid leaks
		mDialog = MemreasProgressDialog.getInstance(this);


		// disable the keyboard input
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		/*
		 * Activity entries here...
		 */

		// Layouts
		layoutMemreasDetails = (ScrollView) this
				.findViewById(R.id.layoutMembersDetails);
		layoutFriendDetails = (LinearLayout) this
				.findViewById(R.id.layoutFriendDetails);
		layoutMediaDetails = (LinearLayout) this
				.findViewById(R.id.layoutMediaDetails);

		// Button Sections
		btnMembersDetails = (Button) findViewById(R.id.btnMemreasDetails);
		btnMembersDetails.setTag("details");
		btnMedia = (Button) findViewById(R.id.btnMedia);
		btnMedia.setTag("media");
		btnFriend = (Button) findViewById(R.id.btnFriends);
		btnFriend.setTag("friend");

		btnMembersDetails.setOnClickListener(onClickAccordions);
		btnMedia.setOnClickListener(onClickAccordions);
		btnFriend.setOnClickListener(onClickAccordions);

		// Button processing
		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(nextListener);
		btnDone = (Button) findViewById(R.id.btnDone);
		btnDone.setOnClickListener(doneListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(cancelListener);

		/*
		 * TODO - Add details section
		 */
		editTxtName = (EditText) findViewById(R.id.editTextName);
		editTxtName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		txtInputDate = (TextView) findViewById(R.id.txtInputDate);
		txtInputDate.setTag(Integer.valueOf(R.id.txtInputDate));
		txtInputDate.setOnClickListener(datelistener);
		imgBtnDate = (ImageButton) findViewById(R.id.imgBtnDate);
		imgBtnDate.setTag(Integer.valueOf(R.id.imgBtnDate));
		imgBtnDate.setOnClickListener(datelistener);

		/*
		 * TODO - Address Location
		 */
		editTextLocation = (EditText) findViewById(R.id.editTextLocation);
		editTextLocation.setImeOptions(EditorInfo.IME_ACTION_DONE);
		;
		editTextLocation.setOnClickListener(locationListener);

		/*
		 * TODO - Details Options Section
		 */
		cbFriendsCanPost = (ImageButton) findViewById(R.id.cbFriendsCanPost);
		cbFriendsCanPost.setOnClickListener(detailsOptionsListener);

		cbFriendsCanAddFriends = (ImageButton) findViewById(R.id.cbFriendsCanAddFriends);
		cbFriendsCanAddFriends.setOnClickListener(detailsOptionsListener);

		cbPublic = (ImageButton) findViewById(R.id.cbPublic);
		cbPublic.setOnClickListener(detailsOptionsListener);

		cbViewable = (ImageButton) findViewById(R.id.cbViewable);
		cbViewable.setOnClickListener(detailsOptionsListener);
		editTextViewableFrom = (TextView) findViewById(R.id.editTextViewableFrom);
		editTextViewableFrom.setTag(Integer.valueOf(R.id.editTextViewableFrom));
		editTextViewableFrom.setOnClickListener(datelistener);
		imgBtnViewableFrom = (ImageButton) findViewById(R.id.imgBtnViewableFrom);
		imgBtnViewableFrom.setTag(Integer.valueOf(R.id.imgBtnViewableFrom));
		imgBtnViewableFrom.setOnClickListener(datelistener);
		editTextViewableTo = (TextView) findViewById(R.id.editTextViewableTo);
		editTextViewableTo.setTag(Integer.valueOf(R.id.editTextViewableTo));
		editTextViewableTo.setOnClickListener(datelistener);
		imgBtnViewableTo = (ImageButton) findViewById(R.id.imgBtnViewableTo);
		imgBtnViewableTo.setTag(Integer.valueOf(R.id.imgBtnViewableTo));
		imgBtnViewableTo.setOnClickListener(datelistener);

		cbPublic = (ImageButton) findViewById(R.id.cbPublic);
		cbPublic.setOnClickListener(detailsOptionsListener);

		cbGhost = (ImageButton) findViewById(R.id.cbGhost);
		cbGhost.setOnClickListener(detailsOptionsListener);
		editTextGhost = (TextView) findViewById(R.id.editTextGhost);
		editTextGhost.setTag(Integer.valueOf(R.id.editTextGhost));
		editTextGhost.setOnClickListener(datelistener);
		imgBtnGhostCal = (ImageButton) findViewById(R.id.imgBtnGhostCal);
		imgBtnGhostCal.setTag(Integer.valueOf(R.id.imgBtnGhostCal));
		imgBtnGhostCal.setOnClickListener(datelistener);

		/**
		 * Media section
		 */
		shareMediaGridView = (GridView) findViewById(R.id.gridview);
		ShareMediaSelectedAdapter.getInstance(AddShareActivity.this,
				R.layout.shareitem_main);
		shareMediaGridView.setAdapter(ShareMediaSelectedAdapter.getInstance());

		/** 20-JAN-2016 - removing comment on share add media to reduce complexity */
		//editTxtComment = (EditText) findViewById(R.id.addComment);
		//btnAudioComment = (ImageView) findViewById(R.id.addCommentAudio);
		//btnAudioComment.setOnClickListener(addAudioCommentListener);

		/*
		 * TODO - friends section
		 */
		friendListView = (ListView) findViewById(R.id.selectedFriendListView);
		ShareFriendSelectedAdapter.getInstance(AddShareActivity.this,
				R.layout.share_page_add_friends_row_item);
		friendListView.setAdapter(ShareFriendSelectedAdapter.getInstance());

		/*
		 * Resore Data if stopped or resumed...
		 */
		restoreActivityData();

		// Configure menu
		menuConfiguration();

		// Show Ads...
		AdmobView();
	}

	private void restoreActivityData() {
		try {
			if ((mMemreasAddShareBean != null)
					&& (mMemreasAddShareBean.isRecreateable())) {
				this.editTxtName.setText(mMemreasAddShareBean.getName());
				this.txtInputDate.setText(mMemreasAddShareBean.getDate());
				this.editTextLocation.setText(mMemreasAddShareBean
						.getLocation());
				// this.(LatLng) detailsHashMap.get("mLatLng");
				this.cbFriendsCanPost
						.setImageResource(fetchCheckBoxResId(mMemreasAddShareBean
								.isFriendsCanPost()));
				this.cbFriendsCanAddFriends
						.setImageResource(fetchCheckBoxResId(mMemreasAddShareBean
								.isFriendsCanAddFriends()));
				this.cbPublic
						.setImageResource(fetchCheckBoxResId(mMemreasAddShareBean
								.isPublicShare()));
				this.cbViewable
						.setImageResource(fetchCheckBoxResId(mMemreasAddShareBean
								.isViewable()));

				this.editTextViewableFrom.setText(mMemreasAddShareBean
						.getViewableFrom());
				this.editTextViewableTo.setText(mMemreasAddShareBean
						.getViewableTo());

				this.cbGhost
						.setImageResource(fetchCheckBoxResId(mMemreasAddShareBean
								.isGhost()));
				this.editTextGhost.setText(mMemreasAddShareBean
						.getGhostEndDate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int fetchCheckBoxResId(boolean isCheckBoxSelected) {
		if (isCheckBoxSelected) {
			return checkboxSelected;
		} else {
			return checkboxUnSelected;
		}
	}

	// Handle Completed Tab
	@Override
	protected void onResume() {
		super.onResume();
		// Start the service if it's not running...
		startQueueService();

		// Setup LocalBroadcastManager
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver, new IntentFilter("queue-progress"));

		restoreActivityData();
	}

	@Override
	protected void onPause() {
		// Unregister since the activity is not visible
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mMessageReceiver);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		/** avoid dialog crash and leaks in async tasks */
		mDialog.dismiss();
		super.onDestroy();
	}



	// Next button Listener
	OnClickListener nextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (layoutMemreasDetails.getVisibility() == View.VISIBLE) {
				btnNext.setVisibility(View.VISIBLE);
				btnMedia.performClick();
			} else if (layoutMediaDetails.getVisibility() == View.VISIBLE) {
				btnNext.setVisibility(View.VISIBLE);
				btnFriend.performClick();
			} else if (layoutFriendDetails.getVisibility() == View.VISIBLE) {
				// Commit Transaction here...
				btnNext.setVisibility(View.GONE);
			}
		}
	};

	private OnClickListener onClickAccordions = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String tag = v.getTag().toString();
			if (tag.equalsIgnoreCase("details")) {
				layoutFriendDetails.setVisibility(View.GONE);
				layoutMediaDetails.setVisibility(View.GONE);
				layoutMemreasDetails.setVisibility(View.VISIBLE);
			} else if (tag.equalsIgnoreCase("media")) {
				storeMemreasAddShareBean();
				layoutMemreasDetails.setVisibility(View.GONE);
				layoutFriendDetails.setVisibility(View.GONE);
				layoutMediaDetails.setVisibility(View.VISIBLE);
				launchMediaDialogActivity();
			} else if (tag.equalsIgnoreCase("friend")) {
				storeMemreasAddShareBean();
				layoutMemreasDetails.setVisibility(View.GONE);
				layoutMediaDetails.setVisibility(View.GONE);
				layoutFriendDetails.setVisibility(View.VISIBLE);
				btnNext.setVisibility(View.GONE);
				launchFriendDialogActivity();
			}
		}
	};

	// Details Date Listener
	OnClickListener datelistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			DatePickerFragment dateFragment = new DatePickerFragment();
			dateFragment.setCallerId(((Integer) v.getTag()).intValue());
			dateFragment.show(getSupportFragmentManager(), "datePicker");
		}
	};

	// DatePickerDialog
	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		int callerId;

		public int getCallerId() {
			return callerId;
		}

		public void setCallerId(int callerId) {
			this.callerId = callerId;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			String monthString;
			switch (month) {
			case 0:
				monthString = "JAN";
				break;
			case 1:
				monthString = "FEB";
				break;
			case 2:
				monthString = "MAR";
				break;
			case 3:
				monthString = "APR";
				break;
			case 4:
				monthString = "MAY";
				break;
			case 5:
				monthString = "JUN";
				break;
			case 6:
				monthString = "JUL";
				break;
			case 7:
				monthString = "AUG";
				break;
			case 8:
				monthString = "SEP";
				break;
			case 9:
				monthString = "OCT";
				break;
			case 10:
				monthString = "NOV";
				break;
			case 11:
				monthString = "DEC";
				break;
			default:
				monthString = "Invalid month";
				break;
			}

			String sDate = String.valueOf(day) + "-" + monthString + "-"
					+ String.valueOf(year);

			switch (getCallerId()) {
			case R.id.txtInputDate:
				txtInputDate.setText(sDate);
				break;
			case R.id.imgBtnDate:
				txtInputDate.setText(sDate);
				break;
			case R.id.editTextViewableFrom:
				editTextViewableFrom.setText(sDate);
				break;
			case R.id.imgBtnViewableFrom:
				editTextViewableFrom.setText(sDate);
				break;
			case R.id.editTextViewableTo:
				editTextViewableTo.setText(sDate);
				break;
			case R.id.imgBtnViewableTo:
				editTextViewableTo.setText(sDate);
				break;
			case R.id.editTextGhost:
				editTextGhost.setText(sDate);
				break;
			case R.id.imgBtnselfdestCal:
				editTextGhost.setText(sDate);
				break;
			default:
				break;
			}
		}

	}

	// Details Location Listener
	OnClickListener locationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Check for Google Play Services...
			if (checkPlayServices()) {
				Intent intent = new Intent(AddShareActivity.this,
						AddShareLocationActivity.class);
				startActivityForResult(intent,
						AddShareLocationActivity.requestCode);
			}
		}
	};

	// Details Location Listener
	OnClickListener detailsOptionsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			/*
			 * Rules:
			 * 
			 * @rule 1 - if public friends can post and add by default
			 * 
			 * @rule 2 - if viewable from date must be current date or later and
			 * to date can't be later earlier than current date
			 * 
			 * @rule 3 - ghost nullifies viewable
			 */

			mMemreasAddShareBean = MemreasAddShareBean.getInstance();
			int view_id = v.getId();
			ImageButton cb = null;
			boolean turnOff = false;
			switch (view_id) {
			case R.id.cbFriendsCanPost:
				cb = cbFriendsCanPost;
				turnOff = checkSelection(cb.getTag().toString());
				if (!turnOff) {
					// If not selected user wants to turn on ...
					flipCheckBox(cb, true);
					flipCheckBox(cbPublic, false);
				} else {
					flipCheckBox(cb, false);
				}
				break;
			case R.id.cbFriendsCanAddFriends:
				cb = cbFriendsCanAddFriends;
				turnOff = checkSelection(cb.getTag().toString());
				if (!turnOff) {
					// If not selected user wants to turn on ...
					flipCheckBox(cb, true);
					flipCheckBox(cbPublic, false);
				} else {
					flipCheckBox(cb, false);
				}
				break;
			case R.id.cbPublic:
				cb = cbPublic;
				turnOff = checkSelection(cb.getTag().toString());
				if (!turnOff) {
					// If not selected user wants to turn on ...
					flipCheckBox(cb, true);
					flipCheckBox(cbFriendsCanPost, false);
					flipCheckBox(cbFriendsCanAddFriends, false);
				} else {
					flipCheckBox(cb, false);
				}
				break;
			case R.id.cbViewable:
				cb = cbViewable;
				turnOff = checkSelection(cb.getTag().toString());
				if (!turnOff) {
					// If not selected user wants to turn on ...
					flipCheckBox(cb, true);
					flipCheckBox(cbGhost, false);
					editTextGhost.setText("");
				} else {
					flipCheckBox(cb, false);
				}
				break;
			case R.id.cbGhost:
				cb = cbGhost;
				turnOff = checkSelection(cb.getTag().toString());
				if (!turnOff) {
					// If not selected user wants to turn on ...
					flipCheckBox(cb, true);
					flipCheckBox(cbViewable, false);
					editTextViewableFrom.setText("");
					editTextViewableFrom.setText("");
				} else {
					flipCheckBox(cb, false);
				}
				break;
			default:
				break;
			} // end switch

		}
	};

	protected boolean checkSelection(String selection) {
		if (selection.equalsIgnoreCase("selected")) {
			return true;
		} else {
			return false;
		}
	}

	protected void flipCheckBox(ImageButton cb, boolean turnOn) {
		if (turnOn) {
			cb.setBackgroundResource(checkboxSelected);
			cb.setTag("selected");
		} else {
			cb.setBackgroundResource(checkboxUnSelected);
			cb.setTag("unselected");
		}
	}

	// addAudioComment listener
	OnClickListener addAudioCommentListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			launchAddAudioCommentDialogActivity();
		}
	};

	protected void launchAddAudioCommentDialogActivity() {
		Intent intent = new Intent(AddShareActivity.this,
				AddAudioCommentActivity.class);
		if (mAudioCommentFilePath != null) {
			intent.putExtra("mFilePath", mAudioCommentFilePath);
			intent.putExtra("mProgress", mAudioCommentDurationInMillis);
		}
		startActivityForResult(intent, AddAudioCommentActivity.requestCode);
	}

	protected void launchMediaDialogActivity() {
		Intent intent = new Intent(AddShareActivity.this,
				AddShareMediaActivity.class);
		startActivityForResult(intent, AddShareMediaActivity.requestCode);
	}

	protected void launchFriendDialogActivity() {
		Intent intent = new Intent(AddShareActivity.this,
				AddShareFriendActivity.class);
		startActivityForResult(intent, AddShareFriendActivity.requestCode);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// Map Location
			if (requestCode == AddShareLocationActivity.requestCode) {
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				mAddress = data.getStringExtra("address");
				if (latitude != 0 || longitude != 0) {
					mLatLng = new LatLng(latitude, longitude);
					editTextLocation.setText(mAddress);
				}
			}
			// Add Media
			if (requestCode == AddShareMediaActivity.requestCode) {
				ShareMediaAdapter.getInstance().notifyDataSetChanged();
			}
			// Add Audio Comment
			if (requestCode == AddAudioCommentActivity.requestCode) {
				mAudioCommentFilePath = data.getStringExtra("mFilePath");
				mAudioCommentDurationInMillis = data
						.getIntExtra("mProgress", 0);
				Toast.makeText(AddShareActivity.this, "audio comment added...",
						Toast.LENGTH_LONG).show();
			}
			// Add Friends
			if (requestCode == AddShareFriendActivity.requestCode) {
				ShareFriendSelectedAdapter.getInstance().refreshFriendSelectedList();
				ShareFriendSelectedAdapter.getInstance().setAsyncTaskContactsComplete(true);
				ShareFriendSelectedAdapter.getInstance().notifyDataSetChanged();
			}
		}
	}

	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		if (status != ConnectionResult.SUCCESS) {
			Toast.makeText(
					AddShareActivity.this,
					"you must install or update Google Play Services for location support",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// cancel button listener
	OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(getApplicationContext(),
					GalleryActivity.class));
			finish();
		}
	};

	/*
	 * Done section ...
	 */
	// done button listener
	OnClickListener doneListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			/**
			 * Rules
			 * - done can be called from details, media, or friends
			 * 1 - addevent must be called which calls remaining functions aSyncTask onPostExecute
			 * 2 - all functions called and if null skipped
			 * 3 - memreas screen shown next
			 * 4 - if media then call handleMedia, addTextAndAudioCommentOnDone, then go to memreas screen from aSyncTask
			 * 5 - if friends then call handleMedia, addTextAndAudioCommentOnDone, handleFriends, then go to memreas screen from aSyncTask
			 * 6 - clear lists and objects
			 */
			handleDetails();
		}
	};

	private void storeMemreasAddShareBean() {
		mMemreasAddShareBean = MemreasAddShareBean.getInstance();
		HashMap<String, Object> detailsHashMap = new HashMap<String, Object>();
		detailsHashMap.put("editTxtName", editTxtName);
		detailsHashMap.put("txtInputDate", txtInputDate);
		detailsHashMap.put("mAddress", mAddress);
		detailsHashMap.put("mLatLng", mLatLng);
		detailsHashMap.put("cbFriendsCanPost", cbFriendsCanPost);
		detailsHashMap.put("cbFriendsCanAddFriends", cbFriendsCanAddFriends);
		detailsHashMap.put("cbPublic", cbPublic);
		detailsHashMap.put("cbViewable", cbViewable);
		detailsHashMap.put("editTextViewableFrom", editTextViewableFrom);
		detailsHashMap.put("editTextViewableTo", editTextViewableTo);
		detailsHashMap.put("cbGhost", cbGhost);
		detailsHashMap.put("editTextGhost", editTextGhost);
		mMemreasAddShareBean.setDetails(detailsHashMap);

		/** 20-JAN-2016 - removing text and audio comment to reduce complexity, groups also
		if (mAudioCommentFilePath != null) {
			mMemreasAddShareBean.setAudioCommentFilePath(mAudioCommentFilePath);
		}
		if (!TextUtils.isEmpty(editTxtComment.getText())) {
			mMemreasAddShareBean.setTextComment(editTxtComment.getText()
					.toString());
		}
		// mMemreasAddShareBean.setGroupCheckBoxSelected(groupCheckBoxSelected);
		// mMemreasAddShareBean.setGroupName(groupName);
		 */
	}

	private boolean handleDetails() {
		try {
			if (!detailsSet) {
				if (TextUtils.isEmpty(editTxtName.getText())) {
					Toast.makeText(AddShareActivity.this,
							"name must be set at a minimum...",
							Toast.LENGTH_SHORT).show();
					btnMembersDetails.performClick();
					return false;
				}

				// Store details section...
				storeMemreasAddShareBean();
				try {
					mAddEventParser = new AddEventParser(
							this);

					if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
						mAddEventParser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					else {
						mAddEventParser.execute();
					}

					//
					// Check result
					//
					if (mAddEventParser.getHandlerStatus()) {
						detailsSet = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					detailsSet = false;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(AddShareActivity.this,
					"an error has occurred " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			detailsSet = false;
		}
		return detailsSet;
	}

	public boolean isDetailsSet() {
		return detailsSet;
	}

	public void setDetailsSet(boolean detailsSet) {
		this.detailsSet = detailsSet;
	}

	public MemreasAddShareBean getmMemreasAddShareBean() {
		return mMemreasAddShareBean;
	}

	public void handleMedia() {
		LinkedList<GalleryBean> selectedMediaLinkedList = ShareMediaSelectedAdapter
				.getInstance().getSelectedMediaLinkedList();
		if ((selectedMediaLinkedList != null)
				&& (selectedMediaLinkedList.size() > 0)) {
			// Start the service if it's not running...
			startQueueService();

			Iterator<GalleryBean> iterator = selectedMediaLinkedList.iterator();
			GalleryBean media;
			while (iterator.hasNext()) {
				media = iterator.next();
				if (!media.isAddedToQueue()) {
					//fetch a media if the media is not sync
					if (media.getType() == GalleryType.NOT_SYNC) {
						//new GenerateMediaIdParser(media)
						//		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						media.setMediaId(MediaIdManager.getInstance().fetchNextMediaId());
					}

					// Sync the media - add to the queue
					if (addedToQueue(media)) {
						media.setAddedToQueue(true);
					} else {
						media.setAddedToQueue(false);
					}
				} // end if media.getType() == GalleryType.NOT_SYNC
			} // end while
			ShareMediaSelectedAdapter.getInstance().clearSelectedList();
		}
	}

	public boolean addedToQueue(GalleryBean media) {
		try {
			if (!media.isAddedToQueue()) {
				MemreasTransferModel transferModel = new MemreasTransferModel(
						media);
				transferModel.setEventId(mMemreasAddShareBean.getEventId());
				transferModel.setType(Type.UPLOAD);
				QueueAdapter.getInstance().getSelectedTransferModelQueue()
						.add(transferModel);
				QueueAdapter
						.getInstance()
						.getSelectedMediaHashMap()
						.put(transferModel.getName(),
								QueueAdapter.getInstance()
										.getSelectedTransferModelQueue()
										.indexOf(transferModel));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void addTextAndAudioCommentOnDone() {

		/*
		 * Upload Audio Comment
		 */
		try {
			if ((mAudioCommentFilePath != null) && (mAudioBean == null)) {
				File mAudioFile = new File(mAudioCommentFilePath);
				mAudioBean = new GalleryBean(GalleryType.NOT_SYNC);
				mAudioBean.setEventId(mMemreasAddShareBean.getEventId());
				mAudioBean.setMediaName(mAudioFile.getName());
				mAudioBean.setLocalMediaPath(mAudioCommentFilePath);
				mAudioBean.setMediaType("audio");
				mAudioBean.setMimeType("audio/mpeg");
				if (addedToQueue(mAudioBean)) {
					mAudioBean.setAddedToQueue(true);
				} else {
					mAudioBean.setAddedToQueue(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Add Text and/or audio Comment
		 */
		try {
			if (!TextUtils.isEmpty(editTxtComment.getText())
					&& (mAudioBean != null)) {
				AddCommentParser addCommentParser = new AddCommentParser(this,
						mAudioBean);
				addCommentParser.execute(mMemreasAddShareBean.getEventId(), "",
						editTxtComment.getText().toString(), "");
			} else if (!TextUtils.isEmpty(editTxtComment.getText())) {
				AddCommentParser addCommentParser = new AddCommentParser(this,
						mAudioBean);
				addCommentParser.execute(mMemreasAddShareBean.getEventId(), "",
						editTxtComment.getText().toString(), "");
			} else if (mAudioBean != null) {
				AddCommentParser addCommentParser = new AddCommentParser(this,
						mAudioBean);
				addCommentParser.execute(mMemreasAddShareBean.getEventId(), "",
						"");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleFriends() {
		try {
			if ((ShareFriendSelectedAdapter.getInstance().getmFriendOrGroup() != null) && (ShareFriendSelectedAdapter.getInstance().getmFriendOrGroup()
					.size() > 0)) {
				AddFriendToShareParser addFriendToEventParser = new AddFriendToShareParser(
						this);
				// adds memreas friends, sends emails, and sms messages
				addFriendToEventParser.execute();
				ShareFriendSelectedAdapter.getInstance().clearInstanceFriendsList();
			}
			// if we're here then done button must have been called
			displayMemreas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showProgress(String msg) {
		if (!this.isDestroyed()) {
			mDialog.setMessage(msg);
			mDialog.show();
		}
	}

	public void dismiss() {
		if (!this.isDestroyed()) {
			mDialog.dismiss();
		}
	}

	public void displayMemreas() {
		Intent intent = new Intent(AddShareActivity.this,
				ViewMemreasActivity.class);
		intent.putExtra("eventId", mMemreasAddShareBean.getEventId());
		//reload memreas since we added
		memreasReloadFlag = true;
		startActivity(intent);
		finish();
	}

	private void startQueueService() {
		// Start the background service here and bind to it
		Intent intent = new Intent(this, QueueService.class);
		intent.putExtra("service", "start");
		startService(intent);
	}

	// handler for received Intents for the ... event
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent

			final String name = intent.getStringExtra("transferModelName");
			final MemreasQueueStatus status = MemreasQueueStatus.valueOf(intent
					.getStringExtra("status"));

			if (name.equalsIgnoreCase("QueueService")
					&& (status.equals(MemreasQueueStatus.COMPLETED))) {
				// Uploads are finished so update the views...
				AddShareActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						// UI code goes here - update background color to sync
						ShareMediaSelectedAdapter.getInstance()
								.refreshSelectedMedia();
						ShareMediaSelectedAdapter.getInstance()
								.notifyDataSetChanged();
					}
				});
			}
		} // end onReceive
	}; // end broadcast receiver...

} // end class
