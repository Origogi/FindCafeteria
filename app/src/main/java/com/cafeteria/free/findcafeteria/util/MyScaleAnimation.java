package com.cafeteria.free.findcafeteria.util;

import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;

public class MyScaleAnimation {

    public static ScaleAnimation instance;

    static {
        instance = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        instance.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        instance.setInterpolator(bounceInterpolator);
    }
}
