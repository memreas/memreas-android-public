
package com.memreas.util;

import java.lang.ref.SoftReference;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;

import com.memreas.base.BaseActivity;
import com.memreas.base.Common;

public class ImageUtils {
	
	private static ImageUtils instance  = null;
	private Bitmap defaultBitmap;

	private ImageUtils() {
	}
	
	public static ImageUtils getInstance() {
		if (instance == null) {
			instance = new ImageUtils();
		}
		return instance;
	}

	@SuppressWarnings("deprecation")
	public BitmapFactory.Options getBitmapOption() {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;

		return options;

	}

	public String getImageNameFromPath(String path) {
		String imagePath[] = path.split("/");
		String imageName = imagePath[imagePath.length - 1];
		return imageName;
	}

	public String getRealPathFromURI(final SoftReference<BaseActivity> mReference, Uri contentUri) {
		return getDataFromURI(mReference, contentUri, "path");
	}
	public String getMimeTypeFromURI(final SoftReference<BaseActivity> mReference, Uri contentUri) {
		return getDataFromURI(mReference, contentUri, "mime_type");
	}
	public String getDataFromURI(final SoftReference<BaseActivity> mReference, Uri contentUri, String type) {
		Activity activity = mReference.get();
		try {
			String data = "";
			
			Cursor cursor;
			if (contentUri.toString().contains("video")) {
				String[] proj = { MediaStore.Video.Media._ID,
						MediaStore.Video.Media.DATA,
						MediaStore.MediaColumns.MIME_TYPE };
				cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
				// int index = cursor
				// .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
				// cursor.moveToFirst();
				// data = cursor.getString(index);
				if (type.equalsIgnoreCase("path")) {
					int index = cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
					cursor.moveToFirst();
					data = cursor.getString(index);
					// data = cursor.getString(cursor
					// .getColumnIndex(MediaStore.Video.Media.DATA));
				} else if (type.equalsIgnoreCase("mime_type")) {
					int index = cursor
							.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
					cursor.moveToFirst();
					data = cursor.getString(index);
					// data = cursor.getString(cursor
					// .getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
				}
			} else {
				String[] proj = { MediaStore.Images.Media._ID,
						MediaStore.Images.Media.DATA,
						MediaStore.MediaColumns.MIME_TYPE };
				cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
				if (type.equalsIgnoreCase("path")) {
					int index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					data = cursor.getString(index);
				} else if (type.equalsIgnoreCase("mime_type")) {
					int index = cursor
							.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
					cursor.moveToFirst();
					data = cursor.getString(index);
				}
			}
			cursor.close();
			return data;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Bitmap decodeSampledBitmapFromPath(String path) {
		int reqWidth = Common.GALLARY_IMG_WIDTH;
		int reqHeight = Common.GALLARY_IMG_HEIGHT;
	
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
	
		return resizeBitmaps(BitmapFactory.decodeFile(path, options));
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
		
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
		
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		
		return inSampleSize;
	}

	public Bitmap resizeBitmaps(Bitmap bitmap) {

		Bitmap newBitmap = null;

		try {
			newBitmap = Bitmap.createScaledBitmap(bitmap,
					Common.GALLARY_IMG_WIDTH, Common.GALLARY_IMG_HEIGHT, true);
		} catch (Exception e) {
			e.printStackTrace();
			newBitmap = defaultBitmap;
		}

		return newBitmap;
	}
	
	public Bitmap getRoundedCornerImage(Bitmap bitmap) {

		if (bitmap == null)
			bitmap = defaultBitmap;

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 10;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	
}
