
package com.memreas.util;

import android.util.Log;

import com.memreas.aws.AmazonClientManager;
import com.memreas.base.BaseActivity;
import com.memreas.base.Common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private static final String TAG = MExceptionHandler.class.getName();
    private AmazonClientManager awsManager;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public MExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        // Fetch S3 Handle
        this.awsManager = AmazonClientManager.getInstance();
    }

    public void uncaughtException(Thread t, Throwable e) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.dd.hh.mm.ss.aaa.zzz.MMMM");
        String formattedDate;
        try {
            formattedDate = simpleDateFormat.format(new Date());
        } catch (Exception fce) {
            formattedDate = new Date().toString();
        }

        File outputDir=null;
        File fatalExceptionFile=null;
        try {
            outputDir = BaseActivity.mApplication.getApplicationContext().getCacheDir(); // context being the Activity pointer
            fatalExceptionFile = File.createTempFile( formattedDate + ".stacktrace", ".log", outputDir);
        } catch (Exception fce) {
            Log.e(TAG, "File.createTempFile()"+fce.getMessage());
        }

        String timestamp = simpleDateFormat.format(new java.util.Date());
        String key = timestamp + "." + fatalExceptionFile.getName();

        try {
            awsManager.getTransferManager().upload(Common.BUCKET_NAME_CRASH_LOGS, key, fatalExceptionFile);
        } catch (Exception awse) {
            Log.e(TAG, "awsManager.getTransferManager().upload(Common.BUCKET_NAME_CRASH_LOGS, key, stream, null)->exception::" + awse.getMessage());
        }
        defaultUEH.uncaughtException(t, e);
    }

}
