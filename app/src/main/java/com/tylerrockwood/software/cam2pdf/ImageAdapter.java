package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by rockwotj on 1/19/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ThumbnailView thumbnailView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            thumbnailView = new ThumbnailView(mContext);
            //thumbnailView.setLayoutParams(new GridView.LayoutParams(85, 85));
            thumbnailView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            thumbnailView.setPadding(8, 8, 8, 8);
        } else {
            thumbnailView = (ThumbnailView) convertView;
        }

        thumbnailView.setImageResource(mThumbIds[position]);
        return thumbnailView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };
}
