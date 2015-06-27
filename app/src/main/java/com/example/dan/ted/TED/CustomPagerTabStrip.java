package com.example.dan.ted.TED;

import android.content.Context;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Dan on 4/5/2015.
 */
public class CustomPagerTabStrip extends PagerTabStrip {

    public CustomPagerTabStrip(Context context){
        super(context);
    }

    public CustomPagerTabStrip (Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
