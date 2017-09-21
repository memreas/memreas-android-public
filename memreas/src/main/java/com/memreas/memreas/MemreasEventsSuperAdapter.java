package com.memreas.memreas;

import java.util.LinkedList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.memreas.util.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressWarnings("rawtypes")
public abstract class MemreasEventsSuperAdapter extends BaseAdapter { 

	protected Activity context;
	protected int resource;
	protected LayoutInflater mInflater;
	protected DisplayImageOptions optionsGallery;
	protected DisplayImageOptions optionsStorage;
	protected AnimateFirstDisplayListener animateFirstListener;
	protected ImageLoader memreasImageLoader;
	protected LinkedList mMemreasList;

	@Override
	public int getCount() {
		return mMemreasList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMemreasList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public LinkedList getmMemreasList() {
		return mMemreasList;
	}

}
