package com.memreas.util;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SwipeDetectorUtil   implements View.OnTouchListener{

    static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    public final static int RIGHT_TO_LEFT=1;
    public final static int LEFT_TO_RIGHT=2;
    public final static int TOP_TO_BOTTOM=3;
    public final static int BOTTOM_TO_TOP=4;
    private View v;

    private onSwipeEvent swipeEventListener;


    public SwipeDetectorUtil(Activity activity,View v){
        try{
            swipeEventListener=(onSwipeEvent)activity;
        }
        catch(ClassCastException e)
        {
            Log.e("ClassCastException", activity.toString() + " must implement SwipeDetector.onSwipeEvent");
        } 
        this.v=v;
    }
    public SwipeDetectorUtil(Fragment fragment,View v){
        try{
            swipeEventListener=(onSwipeEvent)fragment;
        }
        catch(ClassCastException e)
        {
            Log.e("ClassCastException", fragment.toString() + " must implement SwipeDetector.onSwipeEvent");
        } 
        this.v=v;
    }


    public void onRightToLeftSwipe(){   
        swipeEventListener.SwipeEventDetected(v,RIGHT_TO_LEFT);
    }

    public void onLeftToRightSwipe(){   
        swipeEventListener.SwipeEventDetected(v,LEFT_TO_RIGHT);
    }

    public void onTopToBottomSwipe(){   
        swipeEventListener.SwipeEventDetected(v,TOP_TO_BOTTOM);
    }

    public void onBottomToTopSwipe(){
        swipeEventListener.SwipeEventDetected(v,BOTTOM_TO_TOP);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN: {
            downX = event.getX();
            downY = event.getY();
            return true;
        }
        case MotionEvent.ACTION_UP: {
            upX = event.getX();
            upY = event.getY();

            float deltaX = downX - upX;
            float deltaY = downY - upY;

            //HORIZONTAL SCROLL
            if(Math.abs(deltaX) > Math.abs(deltaY))
            {
                if(Math.abs(deltaX) > MIN_DISTANCE){
                    // left or right
                    if(deltaX < 0) 
                    {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if(deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true; 
                    }
                }
                else {
                    //not long enough swipe...
                    return false; 
                }
            }
            //VERTICAL SCROLL
            else 
            {
                if(Math.abs(deltaY) > MIN_DISTANCE){
                    // top or down
                    if(deltaY < 0) 
                    { this.onTopToBottomSwipe();
                    return true; 
                    }
                    if(deltaY > 0)
                    { this.onBottomToTopSwipe(); 
                    return true;
                    }
                }
                else {
                    //not long enough swipe...
                    return false;
                }
            }

            return true;
        }
        }
        return false;
    }
    public interface onSwipeEvent
    {
        public void SwipeEventDetected(View v, int SwipeType);
    }

}