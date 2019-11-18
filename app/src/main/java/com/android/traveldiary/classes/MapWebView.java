package com.android.traveldiary.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MapWebView extends WebView {

    public MapWebView(Context context) {
        super(context);
    }

    public MapWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }
}