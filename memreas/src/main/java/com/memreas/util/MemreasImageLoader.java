
package com.memreas.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.memreas.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MemreasImageLoader extends ImageLoader {

	private static ImageLoaderConfiguration config;
	private static DisplayImageOptions defaultDisplayImageOptions;
	private static DisplayImageOptions defaultDisplayImageOptionsStorage;
	private static BitmapFactory.Options defaultBitmapFactoryOptions;
	private static int failImage = R.drawable.loading_image_fail;
	private static int onLoadingImage = R.drawable.loading_image_gallery;

	public static ImageLoader getInstance(Context c) {
		if (config == null) {
			config = new ImageLoaderConfiguration.Builder(c)
					.denyCacheImageMultipleSizesInMemory()
					.threadPriority(Thread.NORM_PRIORITY - 2)
					.denyCacheImageMultipleSizesInMemory()
					.diskCacheFileNameGenerator(new Md5FileNameGenerator())
					.diskCacheSize(50 * 1024 * 1024) // 50 MiB
					.tasksProcessingOrder(QueueProcessingType.LIFO)
					// .writeDebugLogs() // Remove for release app
					.build();

			config = new ImageLoaderConfiguration.Builder(c).build();
			ImageLoader.getInstance().init(config);
			//L.disableLogging();
		}
		//reset fail image on get
		failImage = R.drawable.loading_image_fail;
		onLoadingImage = R.drawable.loading_image_gallery;

		return ImageLoader.getInstance();
	}

	public static BitmapFactory.Options getDefaultBitmapFactoryOptions() {
		if (defaultBitmapFactoryOptions == null) {
			defaultBitmapFactoryOptions = new BitmapFactory.Options();
			defaultBitmapFactoryOptions.outWidth = 96;
			defaultBitmapFactoryOptions.outHeight = 96;
			defaultBitmapFactoryOptions.inJustDecodeBounds = true;
		}
		return defaultBitmapFactoryOptions;
	}

	public static DisplayImageOptions getDefaultDisplayImageOptions() {
		if (defaultDisplayImageOptions == null) {

			defaultDisplayImageOptions = new DisplayImageOptions.Builder()
					.imageScaleType(ImageScaleType.EXACTLY)
					.showImageOnLoading(onLoadingImage)
					.showImageForEmptyUri(failImage)
					.showImageOnFail(failImage)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}
		return defaultDisplayImageOptions;
	}

	public static DisplayImageOptions getDefaultDisplayImageOptionsStorage() {
		if (defaultDisplayImageOptionsStorage == null) {
			defaultDisplayImageOptionsStorage = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.decodingOptions(getDefaultBitmapFactoryOptions())
					.imageScaleType(ImageScaleType.EXACTLY)
					.showImageForEmptyUri(failImage).showImageOnFail(failImage)
					.build();
		}
		return defaultDisplayImageOptionsStorage;
	}

	public static void setOnLoadingImage(int altOnLoadingImage) {
		onLoadingImage = altOnLoadingImage;
	}

	public static void setFailImage(int altFailImage) {
		failImage = altFailImage;
	}


}
