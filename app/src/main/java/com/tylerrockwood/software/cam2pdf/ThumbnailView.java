package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by rockwotj on 1/19/2015.
 */
public class ThumbnailView extends FrameLayout {


    private ImageView mThumbnail;
    private ImageView mCheckmark;

    public ThumbnailView(Context context) {
        super(context);
        initialize(context);
    }

    public ThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ThumbnailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        setLayoutParams(new AbsListView.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mThumbnail = new ImageThumbnail(context);
        mThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mCheckmark = new ImageThumbnail(context);
        mCheckmark.setBackgroundColor(context.getResources().getColor(R.color.checked_background));
        mCheckmark.setImageResource(R.drawable.ic_check);
        mCheckmark.setScaleType(ImageView.ScaleType.CENTER);
        this.addView(mThumbnail);
        this.addView(mCheckmark);
    }

    public void setThumbnailBitmap(Bitmap src) {
        try {
            mThumbnail.setImageBitmap(src);
        } catch (Exception e) {
            mThumbnail.setImageResource(R.drawable.ic_default_thumbnail);
        }
    }

    public void setChecked(boolean selected) {
        mCheckmark.setVisibility(selected ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }


    private static class ImageThumbnail extends ImageView {
        public ImageThumbnail(Context context) {
            super(context);
            setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }

    }
}
