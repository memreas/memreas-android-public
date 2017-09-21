package com.memreas.memreas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.memreas.R;
import com.memreas.util.AnimateFirstDisplayListener;
import com.memreas.util.MemreasImageLoader;
import com.memreas.util.MemreasProgressDialog;

import java.util.LinkedList;
import java.util.Random;

public class MemreasEventsMeAdapter extends MemreasEventsSuperAdapter {

	private static MemreasEventsMeAdapter instance;
	private MemreasProgressDialog mProgressDialog;
	public static boolean asyncTaskComplete = false;

	protected void setMemreasMeAdapterView(ViewMemreasActivity context,
			int resource) {
		super.setContext(context);
		super.resource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		animateFirstListener = new AnimateFirstDisplayListener();
		animateFirstListener.setFailImage(R.drawable.gallery_img);
		memreasImageLoader = MemreasImageLoader.getInstance();
		optionsGallery = MemreasImageLoader.getDefaultDisplayImageOptions();
		optionsStorage = MemreasImageLoader
				.getDefaultDisplayImageOptionsStorage();
		mProgressDialog = MemreasProgressDialog.getInstance(context);
	}

	protected MemreasEventsMeAdapter() {
		mMemreasList = new LinkedList<MemreasEventMeBean>();
	}

	public static MemreasEventsMeAdapter getInstance() {
		// Assumes adatpter is set
		if (instance == null) {
			instance = new MemreasEventsMeAdapter();
		}
		return instance;
	}

	public static void reset() {
		asyncTaskComplete = false;
		instance=null;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			/*
			 * Initialize Holder
			 */
			holder = new ViewHolder();
			convertView = mInflater.inflate(this.resource, parent, false);

			holder.memreasEventMeBean = null;
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.memreas_event_thumb);
			holder.imageview.setOnClickListener(memreasEventGalleryListener);
			holder.txtMemreasName = (TextView) convertView
					.findViewById(R.id.memreas_event_name);
			
			// holder.networkHeaderLinearLayout.setTag(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * Set viewable grid data based on position
		 */
		holder.memreasEventMeBean = (MemreasEventMeBean) mMemreasList.get(position);
		holder.position = position;
		String url = "";
		try {
			int random_index;
			if ((holder.memreasEventMeBean.getMediaList() != null) && (holder.memreasEventMeBean
					.getMediaList().size() > 0)) {
				random_index = new Random().nextInt(holder.memreasEventMeBean
						.getMediaList().size());
				url = holder.memreasEventMeBean.getMediaList().get(random_index).media_98x78.getString(0);
			} else {
				if (holder.memreasEventMeBean.getMediaList().get(0).media_type.equalsIgnoreCase(
						"image")) {
					url = holder.memreasEventMeBean.getMediaList().get(0).media_url;
				}
			}
		} catch (Exception e) {
			//use fail image...
		}
		memreasImageLoader.displayImage(url, holder.imageview, optionsGallery,
				animateFirstListener);

		holder.txtMemreasName.setText("!"+holder.memreasEventMeBean.getName());
		
		holder.imageview.setTag(Integer.valueOf(position));
		holder.position = position;
		convertView.setTag(holder);
		return convertView;
	}

	@SuppressWarnings("unchecked")
	public void add(MemreasEventMeBean memreasEventMeBean) {
		mMemreasList.add(memreasEventMeBean);
		this.notifyDataSetChanged();
	}

	protected class ViewHolder {
		public ImageView imageview;
		public TextView txtMemreasName;
		public MemreasEventMeBean memreasEventMeBean;
		public int position;
	}

	protected OnClickListener memreasEventGalleryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Integer position = (Integer) v.getTag();
			((ViewMemreasActivity) context).onItemClickViewEvent(position, null, MemreasEventBean.EventType.ME);
		}
	};
	
}
