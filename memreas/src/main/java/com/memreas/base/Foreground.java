package com.memreas.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class Foreground implements Application.ActivityLifecycleCallbacks {

    private static Foreground instance;
    private boolean foreground;
    private boolean background;

    public static void init(Application app) {
        if (instance == null) {
            instance = new Foreground();
            instance.foreground = true;
            app.registerActivityLifecycleCallbacks(instance);
        }
    }

    public static Foreground getInstance() {
        return instance;
    }

    private Foreground() {
    }

    public boolean isForeground(){
        return foreground;
    }

    public boolean isBackground(){
        return !foreground;
    }

    public boolean setForeground(boolean isForeground){
        return foreground = isForeground;
    }

    public void hasReturnedFromBackground(boolean mbackground) {
        foreground = mbackground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        foreground = true;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        foreground = true;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //set foreground in Activity
    }

    @Override
    public void onActivityPaused(Activity activity) {
        foreground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        foreground = false;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    // TODO: implement the lifecycle callback methods!

}
