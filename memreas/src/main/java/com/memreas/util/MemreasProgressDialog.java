package com.memreas.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

public class MemreasProgressDialog extends ProgressDialog {

    private static MemreasProgressDialog instance;
    private Context context;
    private String msg;

    protected MemreasProgressDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public static MemreasProgressDialog getInstance(Context context) {
        if (instance == null) {
            instance = new MemreasProgressDialog(context);
            instance.context = context;
        } else if (instance.context != context) {
            instance.dismiss();
            instance = new MemreasProgressDialog(context);
            instance.context = context;
        }
        return instance;
    }

    public void setAndShow(final String msg) {
        instance.setMessage(msg);
        if (!instance.isShowing()) {
            instance.show();
        }
    }

    public void showWithDelay(final String msg, int delay) {
        try {
            instance.setMessage(msg);
            instance.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        if (instance != null) {
                            instance.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
