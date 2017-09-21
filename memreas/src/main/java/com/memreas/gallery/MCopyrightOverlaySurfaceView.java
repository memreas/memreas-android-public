package com.memreas.gallery;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;

public class MCopyrightOverlaySurfaceView extends SurfaceView {
    protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final float scale = getResources().getDisplayMetrics().density;
    protected MCameraActivity mCameraActivity;
    protected static MCopyrightOverlaySurfaceView instance;
    protected static Canvas canvas;
    protected String mRight;

    protected MCopyrightOverlaySurfaceView(MCameraActivity activity) {
        super(activity);
        mCameraActivity = activity;
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setZOrderMediaOverlay(true);
    }

    public static MCopyrightOverlaySurfaceView getInstance(MCameraActivity activity) {
        if (instance == null) {
            instance = new MCopyrightOverlaySurfaceView(activity);
        }
        return instance;
    }

    //protected Canvas generateCopyrightOverlay(String mRight, boolean newCopyright) {
    //    if ((instance == null) || (newCopyright)) {
    protected Canvas generateCopyrightOverlay(Canvas canvas) {
        if (instance == null) {
            //Canvas canvas = this.getHolder().lockCanvas();
            //this.mRight = mRight;
            paint.setColor(Color.BLUE);
            paint.setTextSize((int) (5 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.LTGRAY);

            //
            // Build copyright string...
            //
            //mRight = "md5:" + copyrightMD5 + " sha256:" + copyrightSHA256;
            mRight = "Hello World";

            float rotate = 0.0f;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                rotate = 90.0f;
            }
            canvas.rotate(rotate, 0, 25);
            canvas.drawText(mRight, 0, 25, paint);
            canvas.save();
        }
        return canvas;
    }

}
