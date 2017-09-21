package com.memreas.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.comment.AddTextCommentActivity;
import com.memreas.memreas.MemreasEventBean.CommentShortDetails;
import com.memreas.notifications.NotificationItem.Type;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

public class NotificationAdapter extends ArrayAdapter<NotificationItem> {

	private Context context;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateGalleryListener;
	protected AnimateFirstDisplayListener animateProfileFirstListener;
	protected ImageLoader memreasImageLoader;
	protected LinkedList<CommentShortDetails> mCommentsList;
	protected ViewHolder holder;

	public NotificationAdapter(Context context) {
		super(context, R.layout.notification_item, NotificationList
				.getInstance().getNotificationList());
		this.context = context;
		animateGalleryListener = new AnimateFirstDisplayListener();
		animateGalleryListener.setFailImage(R.drawable.gallery_img);
		animateProfileFirstListener = new AnimateFirstDisplayListener();
		animateProfileFirstListener.setFailImage(R.drawable.profile_img);
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		/*
		 * Initialize the holder and convertView
		 */
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.notification_popupwindow_item_ref, parent, false);
			View v = convertView.findViewById(R.id.notificationProfileView);

			holder = new ViewHolder();
			holder.headerTextView = (TextView) convertView
					.findViewById(R.id.headerTextView);
			holder.memreas_event_profile = (ImageView) v
					.findViewById(R.id.memreas_event_profile);
			holder.memreas_event_profile_name = (TextView) v
					.findViewById(R.id.memreas_event_profile_name);
			holder.ic_notification_type = (ImageView) convertView
					.findViewById(R.id.ic_notification_type);
			holder.tv_notification_time = (TextView) convertView
					.findViewById(R.id.tv_notification_time);
			holder.tv_notification_message = (TextView) convertView
					.findViewById(R.id.tv_notification_message);
			holder.replyText = (EditText) convertView
					.findViewById(R.id.replyText);
			holder.replyText.addTextChangedListener(replyTextWatcher);
			holder.replyText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			holder.ic_media_framelayout = (FrameLayout) convertView
					.findViewById(R.id.ic_media_framelayout);
			holder.ic_media_thumb = (ImageView) convertView
					.findViewById(R.id.ic_media_thumb);
			holder.btnAccept = (Button) convertView
					.findViewById(R.id.btnAccept);
			holder.btnDecline = (Button) convertView
					.findViewById(R.id.btnDecline);
			holder.btnIgnore = (Button) convertView
					.findViewById(R.id.btnIgnore);
			holder.btnAccept
					.setOnClickListener(updateNotificationOnClickListener);
			holder.btnDecline
					.setOnClickListener(updateNotificationOnClickListener);
			holder.btnIgnore
					.setOnClickListener(updateNotificationOnClickListener);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * Handle adapter specific items here...
		 */
		NotificationItem item = getItem(position);
		// Log.d(getClass().getName(), item.getMessage());
		holder.tv_notification_message.setText(Html.fromHtml(item.getMessage()
				.replaceAll("&lt;", "<").replaceAll("&gt;", ">")));

		/*
		 * Load Profile Pic
		 */
		String url = (item.getProfilePicture79x80() == null || item
				.getProfilePicture79x80().length() == 0) ? item
				.getProfilePicture() : item.getProfilePicture79x80();
		memreasImageLoader.displayImage(url, holder.memreas_event_profile,
				optionsGallery, animateProfileFirstListener);
		holder.memreas_event_profile_name.setText('@' + item.getProfileName());
		if (holder.ic_media_thumb.getVisibility() == View.VISIBLE) {
			memreasImageLoader.displayImage(item.getMediaUrl(),
					holder.ic_media_thumb, optionsGallery,
					animateProfileFirstListener);
		}

		/*
		 * Set time sent...
		 */
		holder.tv_notification_time.setText(item.getTimeString());

		/*
		 * Reset btnAccept if reply was shown...
		 */
		holder.btnAccept.setText("accept");

		/*
		 * ADD_FRIEND
		 */
		if (item.getNotificationType().equals("1")
				|| item.getNotificationType().equals("ADD_FRIEND")) {
			String friend_request_sent = context.getResources().getString(
					R.string.notification_friend_request);
			holder.headerTextView.setText(friend_request_sent);
			holder.tv_notification_message.setText(item.getMessage());

			holder.ic_notification_type
					.setImageResource(R.drawable.notification_friend_icon);
			holder.replyText.setVisibility(View.VISIBLE);
			holder.btnAccept.setVisibility(View.VISIBLE);
			holder.btnDecline.setVisibility(View.VISIBLE);
			holder.btnIgnore.setVisibility(View.VISIBLE);
			holder.ic_media_framelayout.setVisibility(View.GONE);
			holder.ic_media_thumb.setVisibility(View.GONE);

			holder.btnAccept
					.setOnClickListener(updateNotificationOnClickListener);
			holder.btnDecline
					.setOnClickListener(updateNotificationOnClickListener);
			holder.btnIgnore
					.setOnClickListener(updateNotificationOnClickListener);

		} else if (item.getNotificationType().equals("6")
			|| item.getNotificationType().equals("ADD_FRIEND_RESPONSE")) {
			/*
			 * ADD_FRIEND_RESPONSE
			 */
			String friend_request_response = context.getResources().getString(
					R.string.notification_friend_response);
			holder.headerTextView.setText(friend_request_response);
			holder.tv_notification_message.setText(item.getMessage());

			holder.ic_notification_type
					.setImageResource(R.drawable.notification_friend_icon);
			holder.replyText.setVisibility(View.GONE);
			holder.ic_media_framelayout.setVisibility(View.GONE);
			holder.ic_media_thumb.setVisibility(View.GONE);
			holder.btnAccept.setVisibility(View.GONE);
			holder.btnDecline.setVisibility(View.GONE);
			holder.btnIgnore.setVisibility(View.GONE);

		} else if (item.getNotificationType().equals("2")
			|| item.getNotificationType().equals("ADD_FRIEND_TO_EVENT")) {
			/*
			 * ADD_FRIEND_TO_EVENT
			 */
			//String memreas_event_request = context.getResources().getString(
			//		R.string.notification_memreas_event_request);
			String memreas_event_request = context.getResources().getString(
					R.string.notification_memreas_event_request);

			holder.headerTextView.setText(memreas_event_request);
			holder.ic_notification_type
					.setImageResource(R.drawable.notification_event_icon);
			holder.tv_notification_message.setText(item.getMessage());
			holder.replyText.setVisibility(View.VISIBLE);
			holder.replyText.addTextChangedListener(replyTextWatcher);
			holder.replyText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			holder.ic_media_framelayout.setVisibility(View.VISIBLE);
			holder.ic_media_thumb.setVisibility(View.VISIBLE);
			memreasImageLoader.displayImage(item.getMediaUrl(),
					holder.ic_media_thumb, optionsGallery,
					animateGalleryListener);
			holder.btnAccept.setVisibility(View.VISIBLE);
			holder.btnDecline.setVisibility(View.VISIBLE);
			holder.btnIgnore.setVisibility(View.VISIBLE);
		} else if (item.getNotificationType().equals("7")
			|| item.getNotificationType().equals("ADD_FRIEND_TO_EVENT_RESPONSE")) {
			/*
			 * ADD_FRIEND_TO_EVENT_RESPONSE
			 */
			holder.ic_notification_type
					.setImageResource(R.drawable.notification_event_icon);
			holder.btnAccept.setVisibility(View.GONE);
			holder.btnDecline.setVisibility(View.GONE);
			holder.btnIgnore.setVisibility(View.GONE);
			holder.replyText.setVisibility(View.GONE);
			holder.ic_media_framelayout.setVisibility(View.VISIBLE);
			holder.ic_media_thumb.setVisibility(View.VISIBLE);

		} else if (item.getNotificationType().equals("3")
			|| item.getNotificationType().equals("ADD_COMMENT")) {
			/*
			 * ADD_COMMENT
			 */
			holder.ic_notification_type
					.setImageResource(R.drawable.notification_comment_icon);
			holder.ic_media_framelayout.setVisibility(View.VISIBLE);
			holder.ic_media_thumb.setVisibility(View.VISIBLE);
			memreasImageLoader.displayImage(item.getMediaUrl(),
					holder.ic_media_thumb, optionsGallery,
					animateProfileFirstListener);
			holder.replyText.setVisibility(View.GONE);
			holder.btnAccept.setVisibility(View.VISIBLE);
			holder.btnDecline.setVisibility(View.GONE);
			holder.btnIgnore.setVisibility(View.GONE);

			holder.btnAccept.setText("add comment");
			holder.btnAccept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							AddTextCommentActivity.class);
					intent.putExtra("event_id", getItem(position).getEventId());
					intent.putExtra("media_id", getItem(position).getMediaId());
					context.startActivity(intent);
				}
			});
		} else if (item.getNotificationType().equals("4")
			|| item.getNotificationType().equals("ADD_MEDIA")) {
			/*
			 * ADD_MEDIA
			 */
			String memreas_event_request = context.getResources().getString(
					R.string.notification_memreas_event_media_added);
			holder.headerTextView.setText(memreas_event_request);
			holder.ic_notification_type
					.setImageResource(R.drawable.notification_media_icon);

			holder.tv_notification_message.setText(item.getMessage());
			holder.replyText.setVisibility(View.VISIBLE);
			holder.replyText.addTextChangedListener(replyTextWatcher);
			holder.replyText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			holder.ic_media_framelayout.setVisibility(View.VISIBLE);
			holder.ic_media_thumb.setVisibility(View.VISIBLE);
			memreasImageLoader.displayImage(item.getMediaUrl(),
					holder.ic_media_thumb, optionsGallery,
					animateGalleryListener);
			holder.btnAccept.setVisibility(View.VISIBLE);
			holder.btnAccept.setText("reply");
			holder.btnDecline.setVisibility(View.GONE);
			holder.btnIgnore.setVisibility(View.GONE);
			holder.ic_media_thumb
					.setOnClickListener(openMemreasEventOnClickListener);

		} else if (item.getNotificationType().equals("5")
			|| item.getNotificationType().equals("ADD_EVENT")) {
			/*
			 * ADD_EVENT
			 */
			holder.ic_notification_type
					.setImageResource(R.drawable.notification_event_icon);
			holder.btnAccept.setVisibility(View.GONE);
			holder.btnDecline.setVisibility(View.GONE);
			holder.btnIgnore.setVisibility(View.GONE);
			holder.ic_media_thumb.setVisibility(View.VISIBLE);
		}

		holder.position = position;
		holder.item = item;
		holder.btnAccept.setTag(holder);
		holder.btnDecline.setTag(holder);
		holder.btnIgnore.setTag(holder);
		convertView.setTag(holder);
		return convertView;
	}

	OnClickListener updateNotificationOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			ViewHolder holder = (ViewHolder) v.getTag();
			Button button = (Button) v;
			if (button.getId() == R.id.btnAccept) {
				holder.item.setType(Type.ACCEPT);
			} else if (button.getId() == R.id.btnDecline) {
				holder.item.setType(Type.DECLINE);
			} else if (button.getId() == R.id.btnIgnore) {
				holder.item.setType(Type.IGNORE);
			}

			/*
			 * Call UpdateNotifcationAsyncTask
			 */
			new UpdateNotificationAsyncTask(context, NotificationAdapter.this, holder.item, false)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			//
			// remove item regardless of result - item will show on next call if fail
			//
			NotificationList.getInstance().removeNotification(holder.item);
			notifyDataSetChanged();
		}
	};

	OnClickListener openMemreasEventOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	TextWatcher replyTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			String reply = TextUtils.isEmpty(holder.replyText.getText()) ? ""
					: holder.replyText.getText().toString();
			holder.item.setMessage(reply);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}
	};

	private class ViewHolder {
		public TextView headerTextView;
		public ImageView memreas_event_profile;
		public TextView memreas_event_profile_name;
		public ImageView ic_notification_type;
		public FrameLayout ic_media_framelayout;
		public ImageView ic_media_thumb;
		public TextView tv_notification_time;
		public TextView tv_notification_message;
		public EditText replyText;
		public Button btnAccept;
		public Button btnDecline;
		public Button btnIgnore;
		public NotificationItem item;
		int position;
	}
}
