package com.cafeteria.free.findcafeteria.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.cafeteria.free.findcafeteria.util.Logger;

public class CustomViewPager extends ViewPager {

    private GestureDetector mGesture;

    public CustomViewPager(Context context) {
        super(context);
        init();
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mGesture = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //startDetailActivity(cardViewDtos.get(position));
                Logger.d("touch");
                return true;
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mGesture.onTouchEvent(ev)) {
            super.onInterceptTouchEvent(ev);
            return true;
        }
        return false;
    }

}

