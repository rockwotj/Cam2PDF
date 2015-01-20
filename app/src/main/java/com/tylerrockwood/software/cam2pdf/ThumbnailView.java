package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by rockwotj on 1/19/2015.
 */
public class ThumbnailView extends ImageView {

    public ThumbnailView(Context context) {
        super(context);
    }

    public ThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbnailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
