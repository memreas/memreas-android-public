
package com.memreas.share;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.memreas.base.BaseActivity;

public class OptionMemreasActivity extends BaseActivity implements
		OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

//	private ListView lvNotification;
//	private ListView listFindTag;
//	private ProgressBar mProgressBar;
//
//	private NotificationAdapter adapter;
//	private List<NotificationGetterSetter> listNotification = new ArrayList<NotificationGetterSetter>();
//
//	private FindTag mFindTags;
//
//	// iShadowX MM-140
//	private boolean mFromLogin = false;
//	private XMLGenerator xmlGenerator;
//	private ProgressDialog mProgressDialog;
//
//	// Types of search
//	String mTypeFriend = "@";// friend
//	String mTypeComments = "#";// hashTag
//	String mTypeEvent = "!";// Event
//	FindTagsParser mFindTask = null;
//
//	@Override
//	protected void onResume() {
//
//		super.onResume();
//
//		Log.i("Faizan onResume", mTypeComments);
//
//		if (!mFromLogin) {
//			new NotificationCall(this, mProgressBar).execute("");
//			// Toast.makeText(OptionMemreasActivity.this, "Faizan onResume",
//			// Toast.LENGTH_SHORT).show();
//		}
//
//		final EditText myTextBox = (EditText) findViewById(R.id.et_search);
//		// myTextBox.
//		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(myTextBox.getWindowToken(), 0);
//
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.screen_view_memreas_option);
//		mFindTags = new FindTag();
//		mProgressBar = (ProgressBar) findViewById(R.id.processBar);
//		mProgressBar.setVisibility(View.GONE);
//		lvNotification = (ListView) findViewById(R.id.lv_notification);
//		listFindTag = (ListView) findViewById(R.id.tbl_searchResult);
//		lvNotification.setOnItemClickListener(this);
//		// tbl_Search
//		// .setOnItemClickListener(new AdapterView.OnItemClickListener() {
//		//
//		// @Override
//		// public void onItemClick(AdapterView<?> mAdapterView,
//		// View mView, int position, long Id) {
//		// TagSearchResult mTagSearchResult = (TagSearchResult) mAdapterView
//		// .getAdapter().getItem(position);
//		// if (mTagSearchResult.getUsername().startsWith(
//		// mTypeComments)) {
//		// MemreasDetail(mTagSearchResult.getUsername(),
//		// mTagSearchResult.getEvent_id(),
//		// mTagSearchResult.getEvent_photo(),
//		// mTagSearchResult.getCommenter_photo());
//		// }
//		// }
//		// });
//
//		Intent mIntent = getIntent();
//
//		if (mIntent != null) {
//			mFromLogin = mIntent.getBooleanExtra("from_login", false);
//		}
//		try {
//			NotificationGetterSetter getterSetter = NotificationHandler.getterSetter;
//			List<NotificationGetterSetter> list = getterSetter
//					.getNotificationList();
//			if (list != null) {
//				adapter = new NotificationAdapter(OptionMemreasActivity.this,
//						R.layout.notification_row, list);
//				lvNotification.setAdapter(adapter);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//
//		final EditText myTextBox = (EditText) findViewById(R.id.et_search);
//		myTextBox.addTextChangedListener(new TextWatcher() {
//
//			public void afterTextChanged(Editable s) {
//
//				String src = s.toString();
//				if (!src.equalsIgnoreCase("")) {
//					if (src.toString().startsWith("@")
//							|| src.toString().startsWith("!")
//							|| src.toString().startsWith("#")) {
//						if (s != null && s.toString().length() > 1) {
//							if (mFindTask != null) {
//								mFindTask.cancel(true);
//								mFindTask = null;
//							}
//							mFindTask = new FindTagsParser(
//									OptionMemreasActivity.this,
//									new FindTagsParser.FindTagCallBack() {
//
//										@Override
//										public void callBack(String result,
//												List<Object> results) {
//											if (result != null) {
//												if (result.equals("@")) {
//													listFindTag.setAdapter(new UserTagAdapter(
//															OptionMemreasActivity.this, results));
//												} else if (result.equals("!")) {
//													listFindTag.setAdapter(new EventTagAdapter(
//															OptionMemreasActivity.this, results));
//												} else if (result.equals("#")) {
//													listFindTag.setAdapter(new CommentTagAdapter(
//															OptionMemreasActivity.this, results));
//												}
//											}
//										}
//									});
//							mFindTask.execute(s.toString());
//						}
//					} else {
//						myTextBox.setText("");
//						showInvalidInputAlert();
//					}
//				}
//			}
//
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//
//				// iShadowX Changed text method call
//				TextView myOutputBox = (TextView) findViewById(R.id.et_search);
//				// myOutputBox.setText(s);
//
//				mProgressBar = (ProgressBar) findViewById(R.id.processBar);
//				mProgressBar.setVisibility(View.VISIBLE);
//
//				// SearchWebServicerser
//
//			}
//		});
//		xmlGenerator = new XMLGenerator(OptionMemreasActivity.this);
//
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//
//		NotificationGetterSetter dto = listNotification.get(arg2);
//
//		if (dto.isCheck())
//			dto.setCheck(false);
//		else
//			dto.setCheck(true);
//
//		listNotification.set(arg2, dto);
//		adapter.notifyDataSetChanged();
//	}
//
//	public void backBtn(View v) {
//		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//				R.anim.button_click));
//
//		if (mFromLogin) {
//			startActivity(new Intent(OptionMemreasActivity.this,
//					ViewMemreasActivity.class));
//		}
//
//		finish();
//	}
//
//	@Override
//	public void onBackPressed() {
//		if (mFromLogin) {
//			startActivity(new Intent(OptionMemreasActivity.this,
//					ViewMemreasActivity.class));
//		}
//
//		finish();
//	}
//
//	public void logoutBtn(View v) {
//		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//				R.anim.button_click));
//
//		mProgressBar.setVisibility(View.VISIBLE);
//
//		if (mApplication.isOnline()) {
//			new LogoutParser(this).execute();
//		}
//	}
//
//	public void clearBtn(View v) {
//		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//				R.anim.button_click));
//
//		if (listNotification != null && listNotification.size() > 0)
//			new ClearNotificationParser().execute("1");
//		else
//			Toast.makeText(OptionMemreasActivity.this, "No record here",
//					Toast.LENGTH_SHORT).show();
//	}
//
//	public void refreshNotification(View v) {
//		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//				R.anim.button_click));
//
//		new NotificationCall(this, mProgressBar).execute("");
//
//	}
//
//	public void updateBtn(View v) {
//		v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//				R.anim.button_click));
//
//		if (listNotification != null && listNotification.size() > 0)
//			new ClearNotificationParser().execute("2");
//		else
//			Toast.makeText(OptionMemreasActivity.this, "No record here",
//					Toast.LENGTH_SHORT).show();
//	}
//
//	// list adapter
//	class NotificationAdapter extends BaseAdapter {
//
//		private LayoutInflater mInflater;
//		private int resourceId;
//
//		public NotificationAdapter(Activity context, int resourceId,
//				List<NotificationGetterSetter> list) {
//
//			mInflater = (LayoutInflater) context
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			this.resourceId = resourceId;
//			listNotification = list;
//		}
//
//		@Override
//		public int getCount() {
//			return listNotification.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return listNotification.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getView(final int position, View convertView,
//				ViewGroup parent) {
//
//			final ViewHolder holder;
//
//			if (convertView == null) {
//
//				holder = new ViewHolder();
//				convertView = mInflater.inflate(resourceId, null);
//
//				holder.ivNotification = (ImageView) convertView
//						.findViewById(R.id.iv_notification);
//				holder.tvNotification = (TextView) convertView
//						.findViewById(R.id.tv_notification);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//
//			if (listNotification.get(position).isCheck())
//				holder.ivNotification.setImageResource(R.drawable.right_icon);
//			else
//				holder.ivNotification.setImageResource(R.drawable.cross_icon);
//
//			holder.tvNotification.setText(Html.fromHtml(listNotification
//					.get(position).getNotificationData()
//					.replaceAll("&lt;", "<").replaceAll("&gt;", ">")));
//
//			return convertView;
//		}
//
//		class ViewHolder {
//			ImageView ivNotification;
//			TextView tvNotification;
//		}
//	}
//
//	// clear notification parser
//	class ClearNotificationParser extends AsyncTask<String, Void, String> {
//
//		@Override
//		protected String doInBackground(String... arg0) {
//
//			if (arg0[0].equals("1")) {
//
//				String xmlData = XMLGenerator
//						.ClearNotificationXML(SessionManager.getInstance()
//								.getUserId());
//
//				Log.i("LoginParser XML DATA", xmlData);
//
//				SaxParser.parse(
//						Common.SERVER_URL + Common.CLEAR_NOTIFICATION + "&sid="
//								+ SessionManager.getInstance().getSessionId(),
//						xmlData, new NotificationHandler(), "xml");
//			} else {
//
//				String xmlData = XMLGenerator
//						.UpdateNotificationXML(listNotification);
//
//				Log.i("LoginParser XML DATA", xmlData);
//
//				SaxParser.parse(
//						Common.SERVER_URL + Common.UPDATE_NOTIFICATION
//								+ "&sid="
//								+ SessionManager.getInstance().getSessionId(),
//						xmlData, new NotificationHandler(), "xml");
//			}
//
//			return "";
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//
//			super.onPostExecute(result);
//
//			mProgressBar.setVisibility(8);
//			NotificationGetterSetter getterSetter = NotificationHandler.getterSetter;
//
//			if (getterSetter == null) {
//				// Toast.makeText(OptionMemreasActivity.this,
//				// "Sorry! Not getting proper response from server.",
//				// Toast.LENGTH_LONG).show();
//				Log.i(TAG, "Sorry! Not getting proper response from server.");
//			} else {
//
//				if (getterSetter.getStatus().equalsIgnoreCase("success")
//						|| getterSetter.getStatus().equalsIgnoreCase("Sucess")) {
//					listNotification.clear();
//					adapter.notifyDataSetChanged();
//					Toast.makeText(OptionMemreasActivity.this,
//							"" + getterSetter.getMessage(), Toast.LENGTH_LONG)
//							.show();
//				} else {
//					Toast.makeText(OptionMemreasActivity.this,
//							"" + getterSetter.getMessage(), Toast.LENGTH_LONG)
//							.show();
//				}
//			}
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//			super.onPreExecute();
//
//			NotificationHandler.getterSetter = null;
//			mProgressBar.setVisibility(0);
//		}
//	}
//
//	// class FindTagsTask extends AsyncTask<String, Void, String> {
//	// String mButtonText = "";
//	// String mResponse = "";
//	// String TAG_COUNT = "count";
//	// String TAG_SEARCH = "search";
//	// String TAG_TOTAL_PAGE = "totalPage";
//	//
//	// String mAddFriend = "add friend";
//	// String mAddToMe = "add to me";
//	//
//	// @Override
//	// protected String doInBackground(String... arg0) {
//	//
//	// String xmlData = new XMLGenerator(OptionMemreasActivity.this)
//	// .getTags(arg0[0]);
//	// if (arg0[0].startsWith(mTypeFriend)) {
//	// mButtonText = mAddFriend;
//	// } else if (arg0[0].startsWith(mTypeComments)) {
//	// mButtonText = "";
//	// } else if (arg0[0].startsWith(mTypeEvent)) {
//	// mButtonText = mAddToMe;
//	// }
//	// Log.i("LoginParser XML DATA", xmlData);
//	//
//	// mResponse = SaxParser.parse(Common.SERVER_URL + Common.FindTag,
//	// xmlData, "xml");
//	//
//	// return "";
//	// }
//	//
//	// @Override
//	// protected void onPostExecute(String result) {
//	//
//	// super.onPostExecute(result);
//	//
//	// mProgressBar.setVisibility(8);
//	//
//	// boolean isResultOK = false;
//	// if (!mResponse.equalsIgnoreCase("")) {
//	// isResultOK = true;
//	// try {
//	// System.out.println("Response: " + mResponse);
//	// JSONObject mJsonObject = new JSONObject(mResponse);
//	//
//	// String mCount = mJsonObject.getString(TAG_COUNT);
//	// String mTotalPage = mJsonObject.getString(TAG_TOTAL_PAGE);
//	// JSONArray mSearchResult = mJsonObject
//	// .getJSONArray(TAG_SEARCH);
//	// mFindTags.clear();
//	// mFindTags.setCount(mCount);
//	// mFindTags.setTotalPage(mTotalPage);
//	//
//	// if (mSearchResult != null) {
//	//
//	// for (int i = 0; i < mSearchResult.length(); i++) {
//	// JSONObject mObject = mSearchResult.getJSONObject(i);
//	//
//	// if (mButtonText.equalsIgnoreCase(mAddFriend)) {
//	// mFindTags
//	// .mAddTagResults(
//	//
//	// mGetJsonString(mObject,
//	// "username"),
//	// mGetJsonString(mObject,
//	// "profile_photo"),
//	// mGetJsonString(mObject,
//	// "friend_request_sent"));
//	//
//	// } else if (mButtonText.equalsIgnoreCase("")) {
//	// mFindTags.mAddTagResults(
//	//
//	// // comment = "#Faizan";
//	// // "commenter_photo" =
//	// //
//	// "http://memreasdev-wsu.elasticbeanstalk.com//memreas/img/profile-pic.jpg";
//	// // "event_id" =
//	// // "917cdc3a-55cb-409f-bdd2-e36fa72f411b";
//	// // "event_name" = "My Memreas";
//	// // "event_photo" =
//	// //
//	// "http://d1ckv7o9k6o3x9.cloudfront.net/ce3a833c-e075-4ca0-b890-71dfcdb7ba79/image/79x80/96FA5D82-CFBC-4A9B-82B4-656DDF383723.JPG";
//	// // name = "#Faizan";
//	//
//	// mGetJsonString(mObject, "name"),
//	// mGetJsonString(mObject, "event_name"),
//	// mGetJsonString(mObject, "event_id"),
//	// mGetJsonString(mObject, "event_photo"),
//	// mGetJsonString(mObject,
//	// "commenter_photo"),
//	// mGetJsonString(mObject, "comment"));
//	// } else if (mButtonText.equalsIgnoreCase(mAddToMe)) {
//	// mFindTags.mAddTagResults(
//	// mGetJsonString(mObject, "name"),
//	// mGetJsonString(mObject, "event_photo"),
//	// mGetJsonString(mObject,
//	// "friend_request_sent"));
//	// }
//	//
//	// }
//	//
//	// // tbl_Search.setAdapter(new UserSearchAdapter(
//	// // OptionMemreasActivity.this, mFindTags,
//	// // mButtonText));
//	// // tbl_Search.getAdapter().notifyDataSetChanged();
//	// }
//	//
//	// } catch (JSONException e1) {
//	// isResultOK = false;
//	// e1.printStackTrace();
//	// }
//	//
//	// }
//	// if (!isResultOK) {
//	// Toast.makeText(OptionMemreasActivity.this,
//	// "Sorry! Not getting proper response from server.",
//	// Toast.LENGTH_LONG).show();
//	// Log.i(TAG, "Sorry! Not getting proper response from server.");
//	// }
//	//
//	// }
//	//
//	// public String mGetJsonString(JSONObject mObject, String key) {
//	// try {
//	// if (mObject != null) {
//	// return mObject.getString(key);
//	// }
//	// } catch (Exception e) {
//	// // TODO: handle exception
//	// }
//	// return "";
//	// }
//	//
//	// @Override
//	// protected void onPreExecute() {
//	//
//	// super.onPreExecute();
//	// mProgressBar.setVisibility(0);
//	// }
//	// }
//
//	class UserSearchAdapter extends BaseAdapter {
//
//		private LayoutInflater mInflater;
//		private FindTag mFindTag;
//		private String mButtonText;
//		private ImageLoader mImageLoader;
//
//		public UserSearchAdapter(Activity context, FindTag mFindTag,
//				String mButtonText) {
//
//			mInflater = (LayoutInflater) context
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			this.mFindTag = mFindTag;
//			this.mButtonText = mButtonText;
//
//			mImageLoader = new ImageLoader(context.getApplicationContext());
//
//		}
//
//		@Override
//		public int getCount() {
//			return mFindTag.getmTagResults().size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return mFindTag.getmTagResults().get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getView(final int position, View convertView,
//				ViewGroup parent) {
//
//			final ViewHolder holder;
//
//			if (convertView == null) {
//
//				holder = new ViewHolder();
//				convertView = mInflater.inflate(R.layout.search_row, null);
//
//				holder.mImageViewProfileIcon = (ImageView) convertView
//						.findViewById(R.id.iv_usericon);
//				holder.mTextViewUserName = (TextView) convertView
//						.findViewById(R.id.tv_username);
//
//				holder.mButtonAdd = (Button) convertView
//						.findViewById(R.id.btn_adduser);
//
//				holder.mButtonAdd
//						.setOnClickListener(new View.OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								new AddFriendToEventParser(
//										(TagSearchResult) getItem(position))
//										.execute("");
//
//							}
//						});
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			TagSearchResult mSearchResult = mFindTag.getmTagResults().get(
//					position);
//
//			if (!mSearchResult.getUsername().startsWith(mTypeComments)) {
//
//				mImageLoader.DisplayImage(mSearchResult.getProfile_photo(),
//						holder.mImageViewProfileIcon,
//						R.drawable.profile_img_small);
//
//			} else {
//				mImageLoader.DisplayImage(mSearchResult.getEvent_photo(),
//						holder.mImageViewProfileIcon, R.drawable.gallery_img);
//			}
//
//			holder.mTextViewUserName.setText(mSearchResult.getUsername());
//			if (mButtonText.equalsIgnoreCase("")) {
//				holder.mButtonAdd.setVisibility(View.GONE);
//				holder.mButtonAdd.setEnabled(true);
//
//			} else {
//
//				if (mSearchResult.getFriend_request_sent()
//						.equalsIgnoreCase("1")) {
//
//					holder.mButtonAdd.setText("Friend Request sent.");
//					holder.mButtonAdd.setVisibility(View.VISIBLE);
//					holder.mButtonAdd.setEnabled(false);
//				} else {
//
//					holder.mButtonAdd.setText(mButtonText);
//					holder.mButtonAdd.setVisibility(View.VISIBLE);
//					holder.mButtonAdd.setEnabled(true);
//				}
//			}
//
//			return convertView;
//		}
//
//		class ViewHolder {
//			ImageView mImageViewProfileIcon;
//			TextView mTextViewUserName;
//			Button mButtonAdd;
//		}
//
//	}
//
//	public void showInvalidInputAlert() {
//		new AlertDialog.Builder(OptionMemreasActivity.this)
//				// .setTitle("memreas alert")
//				.setMessage(R.string.search_input_error)
//				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface arg0, int arg1) {
//						return;
//					}
//				}).create().show();
//	}
//
//	public void showAlertMessage(String mMessage) {
//		new AlertDialog.Builder(OptionMemreasActivity.this)
//				// .setTitle("memreas alert")
//				.setMessage(mMessage)
//				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface arg0, int arg1) {
//						return;
//					}
//				}).create().show();
//	}
//
//	class AddFriendToEventParser extends AsyncTask<String, Void, String> {
//
//		TagSearchResult mTagSearchResult;
//		String mMessage = "";
//
//		public AddFriendToEventParser(TagSearchResult mTagSearchResult) {
//
//			this.mTagSearchResult = mTagSearchResult;
//
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//			super.onPreExecute();
//			mProgressDialog = ProgressDialog.show(OptionMemreasActivity.this,
//					"", getString(R.string.feather_loading_title));
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//
//			String xmlData = "";
//			String mFriendName = "";
//			if (mTagSearchResult.getUsername().startsWith("@")) {
//				mFriendName = mTagSearchResult.getUsername().substring(1,
//						mTagSearchResult.getUsername().length());
//				xmlData = XMLGenerator.addFriend(SessionManager.getInstance()
//						.getUserId(), mFriendName, "memreas");
//			} else if (mTagSearchResult.getUsername().startsWith("!")) {
//				mFriendName = mTagSearchResult.getUsername().substring(1,
//						mTagSearchResult.getUsername().length());
//				xmlData = XMLGenerator.addFriendEvent(SessionManager
//						.getInstance().getUserId(), Common.EVENTID,
//						SessionManager.getInstance().getUserName(), "memreas",
//						SessionManager.getInstance().getUserProfilePicture());
//			} else {
//				return "";
//			}
//
//			Log.i("SelectFriendActivity", "Add Friend XML : " + xmlData);
//
//			SaxParser.parse(Common.SERVER_URL
//					+ Common.ADD_FRIENDS_TO_EVENT_ACTION + "&sid="
//					+ SessionManager.getInstance().getSessionId(), xmlData,
//					new CommonHandler(), "xml");
//
//			final CommonGetSet addFriendToEventParser = CommonHandler.commonList;
//
//			if ((addFriendToEventParser.getStatus().toString())
//					.equalsIgnoreCase("success")) {
//
//				// mMessage = addFriendToEventParser.getMessage();
//				Log.i("SelectFriendActivity", "Add Friends : "
//						+ addFriendToEventParser.getMessage());
//				if (mTagSearchResult.getUsername().startsWith("@")) {
//					mMessage = "Successfully add friend.";
//				} else {
//					mMessage = "Successfully added you to event.";
//				}
//			} else {
//				// mMessage = addFriendToEventParser.getMessage();
//
//				Log.i("SelectFriendActivity", "Add Friends : "
//						+ addFriendToEventParser.getMessage());
//			}
//			return "";
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//
//			super.onPostExecute(result);
//			mProgressDialog.dismiss();
//			showAlertMessage(mMessage);
//			final EditText myTextBox = (EditText) findViewById(R.id.et_search);
//			// if (mFindTask != null) {
//			// mFindTask.cancel(true);
//			// mFindTask = null;
//			// }
//			// mFindTask = new FindTagsTask();
//			// mFindTask.execute(myTextBox.getText().toString());
//
//		}
//	}
//
//	public void MemreasDetail(String name, String event_id,
//			String eventMediaType, String eventThumbUrl) {
//		EventBean eventBean = new EventBean();
//		// EventBean eventBean = new EventBean(name, event_id, eventMediaType,
//		// eventThumbUrl, false, false, true, "");
//
//		SessionManager.getInstance().setEventBean(eventBean);
//		SessionManager.getInstance().setEventId(eventBean.getEventId());
//
//		/*
//		 * TODO - Implement memreas detail page...
//		 */
//		Log.e("MISSING----->","NewMemreasDetailActivity");
////		startActivityForResult(
////				new Intent(this, NewMemreasDetailActivity.class), 0);
//	}
//
//	// Notification Parsser
//	class NotificationCall extends
//			AsyncTask<String, Void, List<NotificationItem>> {
//
//		private Activity activity;
//		private ProgressBar mProgressBar;
//
//		public NotificationCall(Activity activity, ProgressBar progressbar) {
//
//			this.activity = activity;
//			this.mProgressBar = progressbar;
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//			super.onPreExecute();
//
//			NotificationHandler.getterSetter = null;
//			mProgressBar.setVisibility(0);
//		}
//
//		@Override
//		protected List<NotificationItem> doInBackground(String... arg0) {
//
//			String xmlData = XMLGenerator.ListNotificationXML(SessionManager
//					.getInstance().getUserId());
//
//			Log.i("LoginParser XML DATA", xmlData);
//			NotificationHandler handler = new NotificationHandler();
//			SaxParser.parse(Common.SERVER_URL + Common.LIST_NOTIFICATION
//					+ "&sid=" + SessionManager.getInstance().getSessionId(),
//					xmlData, handler, "xml");
//
//			return handler.getNotifications();
//		}
//
//		@Override
//		protected void onPostExecute(List<NotificationItem> result) {
//
//			super.onPostExecute(result);
//
//			NotificationList.getInstance().getNotificationList().clear();
//			NotificationList.getInstance().getNotificationList().addAll(result);
//			mProgressBar.setVisibility(View.GONE);
//
//			NotificationGetterSetter getterSetter = NotificationHandler.getterSetter;
//
//			if (getterSetter == null) {
//				Toast.makeText(activity,
//						"Sorry! Not getting proper response from server.",
//						Toast.LENGTH_LONG).show();
//			} else {
//				if (getterSetter.getStatus().equalsIgnoreCase("success")) {
//					List<NotificationGetterSetter> list = getterSetter
//							.getNotificationList();
//					if (list == null) {
//						Log.i("NotificationParser",
//								"Sorry! notification list is null.");
//
//						// activity.startActivity(new Intent(activity,
//						// ViewMemreasActivity.class));
//						// activity.finish();
//					} else {
//
//						// Toast.makeText(activity, "Faizan recall WS",
//						// Toast.LENGTH_SHORT).show();
//
//						listNotification = list;
//						adapter.notifyDataSetChanged();
//
//						// Intent mIntent = new Intent(activity,
//						// OptionMemreasActivity.class);
//						// mIntent.putExtra("from_login", true);
//						// activity.startActivityForResult(mIntent, 0);
//						// activity.finish();
//					}
//				} else {
//					// activity.startActivity(new Intent(activity,
//					// ViewMemreasActivity.class));
//					// activity.finish();
//				}
//			}
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (mFindTask != null) {
//			mFindTask.cancel(true);
//			mFindTask = null;
//		}
//		super.onDestroy();
//	}
//
}
