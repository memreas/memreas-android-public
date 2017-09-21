
package com.memreas.search;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.memreas.R;
import com.memreas.sax.handler.FindTagsParser;

public class SearchPopup {
	private Activity mActivity;
	private PopupWindow mPopupWindow;
	private FindTagsParser mFindTagTask;
	private static ListAdapter mAdapter;
	private ListView listView;
	private EditText edtSearch;
	private View tvNoresult;
	private View progressBar;
	
	private static String text = "";

	public static void clearSession() {
		mAdapter = null;
		text = "";
	}

	public SearchPopup(Activity activity) {
		mActivity = activity;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	public void show(View view) {
		/*
		 * View is called from BaseActivity...
		 */
		View popupView = LayoutInflater.from(mActivity).inflate(
				R.layout.search_popupwindow_layout, null);
		View arrow = popupView.findViewById(R.id.ic_arrow);
		arrow.measure(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		/*
		 * Setup listView adapter here...
		 */
		listView = (ListView) popupView
				.findViewById(R.id.list_notification);
		if (mAdapter != null) {
			listView.setAdapter(mAdapter);
		}
		
		/*
		 * Setup text vars
		 */
		edtSearch = (EditText) popupView
				.findViewById(R.id.et_search);
		edtSearch.setText(text);

		tvNoresult = popupView.findViewById(R.id.tv_no_result);
		progressBar = popupView.findViewById(R.id.progress_bar);

		int[] location = new int[2];
		view.getLocationOnScreen(location);

		/*
		 * Setup View for screen size...
		 */
		int width = mActivity.getWindowManager().getDefaultDisplay().getWidth()
				- location[0] * 2;
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) arrow
				.getLayoutParams();
		params.leftMargin = view.getWidth() / 2 - arrow.getMeasuredWidth() / 2;

		mPopupWindow = new PopupWindow(popupView, width, mActivity
				.getWindowManager().getDefaultDisplay().getHeight() / 2);
		mPopupWindow.setAnimationStyle(R.style.SearchAnimationPopup);

		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		mPopupWindow.setOnDismissListener(onDismissListener);
		mPopupWindow.showAsDropDown(view, 0, 0);

		edtSearch.addTextChangedListener(textWatcher);
		edtSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);

	}

	PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {

		@Override
		public void onDismiss() {
			if (mFindTagTask != null) {
				mFindTagTask.cancel(true);
				mFindTagTask = null;
			}
			mAdapter = listView.getAdapter();
		}
	};
	
	
	TextWatcher textWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
			text = s.toString().trim();
			if (!text.equalsIgnoreCase("")) {
				if (text.toString().startsWith("@")
						|| text.toString().startsWith("!")
						|| text.toString().startsWith("#")) {
					if (s != null && s.toString().length() > 1) {
						
						if (mFindTagTask != null) {
							mFindTagTask.cancel(true);
							mFindTagTask = null;
						}
						progressBar.setVisibility(View.VISIBLE);
						tvNoresult.setVisibility(View.GONE);
						listView.setVisibility(View.GONE);

						mFindTagTask = new FindTagsParser(mActivity
								.getApplicationContext(),
								new FindTagsParser.FindTagCallBack() {

									@Override
									public void callBack(String result,
											List<Object> results) {
										progressBar
												.setVisibility(View.GONE);
										listView.setVisibility(View.VISIBLE);
										try {
											if (result != null) {
												if (result.equals("@")) {
													listView.setAdapter(new UserTagAdapter(
															mActivity,
															results));
												} else if (result
														.equals("!")) {
													listView.setAdapter(new EventTagAdapter(
															mActivity,
															results));
												} else if (result
														.equals("#")) {
													listView.setAdapter(new CommentTagAdapter(
															mActivity,
															results));
												}
												if (results.size() == 0) {
													tvNoresult
															.setVisibility(View.VISIBLE);
												}
											} else {
												listView.setAdapter(null);
												tvNoresult
														.setVisibility(View.VISIBLE);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
						mFindTagTask.execute(text);
					}
				} else {
					edtSearch.setText("");
					Toast.makeText(mActivity, R.string.search_input_error,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				if (mFindTagTask != null) {
					mFindTagTask.cancel(true);
					mFindTagTask = null;
				}
				mAdapter = null;
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}
	};
}
