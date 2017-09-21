
package com.memreas.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.memreas.R;
import com.memreas.base.MemreasApplication;
import com.memreas.notifications.NotificationItem.Type;
import com.memreas.queue.QueueAdapter;
import com.memreas.sax.handler.LogoutParser;

public class NotificationPopup {
	private Activity mActivity;
	private ProgressBar progressBar;
	private PopupWindow mPopupWindow;
	private NotificationAdapter mAdapter;
	private static ListNotificationsAsyncTask mNotificationCall;
	private ListView listView;
	private SwipeDismissListViewTouchListener swipeDismissListViewTouchListener;

	public NotificationPopup(Activity activity) {
		mActivity = activity;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	public void show(View view) {
		/*
		 * Clear the list of notifications and initialize arrays...
		 */
		NotificationList.getInstance().getNotificationList().clear();

		/*
		 * Create the view
		 */
		View popupView = LayoutInflater.from(mActivity).inflate(
				R.layout.notification_popupwindow_layout, null);
		View arrow = popupView.findViewById(R.id.ic_arrow);
		arrow.measure(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		/*
		 * Populate the list
		 */
		listView = (ListView) popupView.findViewById(R.id.list_notification);
		progressBar = (ProgressBar) popupView.findViewById(R.id.progress_bar);
		mAdapter = new NotificationAdapter(mActivity);
		listView.setAdapter(mAdapter);
		popupView.findViewById(R.id.ic_refresh).setOnClickListener(
				popupRefreshOnClickListener);
		swipeDismissListViewTouchListener = initSwipeListener();
		listView.setOnTouchListener(swipeDismissListViewTouchListener);

		/*
		 * Handle the logout button...
		 */
		View btnLogout = popupView.findViewById(R.id.btn_logout);
		btnLogout.measure(0, 0);
		// ViewGroup.LayoutParams layoutParams = btnLogout.getLayoutParams();
		// layoutParams.height = (btnLogout.getMeasuredWidth() * 54) / 176;
		btnLogout.setOnClickListener(logoutOnClickListener);

		/*
		 * Screen sizing related
		 */
		int[] location = new int[2];
		view.getLocationOnScreen(location);

		int width = 2 * (location[0] + view.getWidth())
				- mActivity.getWindowManager().getDefaultDisplay().getWidth();
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) arrow
				.getLayoutParams();
		params.leftMargin = width - view.getWidth() / 2
				- arrow.getMeasuredWidth() / 2;

		mPopupWindow = new PopupWindow(popupView, width, mActivity
				.getWindowManager().getDefaultDisplay().getHeight() / 2);
		mPopupWindow.setAnimationStyle(R.style.NotificationAnimationPopup);

		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		mPopupWindow.setOnDismissListener(popupOnDismissListener);
		mPopupWindow.showAsDropDown(view, view.getWidth() - width, 0);
		if (mNotificationCall != null) {
			mNotificationCall.cancel(true);
			mNotificationCall = null;
		}

		/*
		 * Call list notifications async
		 */
		mNotificationCall = new ListNotificationsAsyncTask(mAdapter,
				progressBar, true);
		mNotificationCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	} // end show(View v)

	private void logoutConfirm() {
		new AlertDialog.Builder(mActivity)
				.setTitle("memreas alert")
				.setMessage(
						"logging out will cancel existing transfers, please confirm")
				.setPositiveButton(R.string.confirm_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								logout();
							}
						})
				.setNegativeButton(R.string.confirm_no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
	}

	private void logout() {
		// for (LinkedBlockingQueue<TransferModel> model :
		// QueueAdapter.getInstance().getmTransferModels().keySet()) {
		// if (QueueAdapter.getInstance().getDownloadTasks().get(download) !=
		// null)
		// QueueAdapter.getInstance().getDownloadTasks().get(download).cancel(true);
		// }
		//
		// for (UploadMediaQueue upload :
		// QueueAdapter.getInstance().getUploadTasks().keySet()) {
		// if (QueueAdapter.getInstance().getUploadTasks().get(upload) != null)
		// QueueAdapter.getInstance().getUploadTasks().get(upload).cancel(true);
		// }
		// QueueService.getInstance(mActivity).getmTransferModels().clear();
		new LogoutParser(mActivity).execute();

	}

	/*
	 * Listeners
	 */
	OnClickListener logoutOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			/*
			 * TODO - Fix once QueueAdapter is set...
			 */
			if (QueueAdapter.getInstance().getSelectedTransferModelQueue()
					.size() > 0) {
				logoutConfirm();
			} else {
				logout();
				MemreasApplication.trimCache(mActivity);
			}

		}
	};

	OnDismissListener popupOnDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss() {
			mAdapter = null;
			// new UpdateNotificationListAsyncTask(mActivity)
			// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	};

	public SwipeDismissListViewTouchListener initSwipeListener() {
		return new SwipeDismissListViewTouchListener(this.listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							// mDeclineItems
							// .add(new Pair<NotificationItem, String>(
							// mAdapter.getItem(position), ""));
							/*
							 * Call UpdateNotifcationAsyncTask
							 */
							NotificationItem item = mAdapter.getItem(position);
							item.setType(Type.IGNORE);
							new UpdateNotificationAsyncTask(mActivity,
									mAdapter, item, true)
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							mAdapter.remove(mAdapter.getItem(position));
						}
						mAdapter.notifyDataSetChanged();
						try {
							mActivity
									.getWindow()
									.setSoftInputMode(
											WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
						} catch (Exception e) {
						}
					}
				});
	}

	OnClickListener popupRefreshOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mNotificationCall != null) {
				mNotificationCall.cancel(true);
				mNotificationCall = null;
			}
			mNotificationCall = new ListNotificationsAsyncTask(mAdapter,
					progressBar, true);
			mNotificationCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	};
}
