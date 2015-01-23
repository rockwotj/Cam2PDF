package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

/**
 * Created by rockwotj on 1/19/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Bitmap> mThumbnails;
    private List<String> mFilePaths;

    public ImageAdapter(Context context, List<String> filePaths, List<Bitmap> thumbnails) {
        mContext = context;
        mFilePaths = filePaths;
        mThumbnails = thumbnails;
    }

    @Override
    public int getCount() {
        return mFilePaths.size();
    }

    @Override
    public String getItem(int i) {
        return mFilePaths.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void deleteItem(int i) {
        mFilePaths.remove(i);
        mThumbnails.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ThumbnailView thumbnailView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes

            thumbnailView = new ThumbnailView(mContext);
            //thumbnailView.setLayoutParams(new GridView.LayoutParams(85, 85));
            thumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnailView.setPadding(8, 8, 8, 8);
        } else {
            thumbnailView = (ThumbnailView) convertView;
        }
        try {
            thumbnailView.setImageBitmap(mThumbnails.get(position));
        } catch (Exception e) {
            thumbnailView.setImageResource(R.drawable.ic_default_thumbnail);
        }
        return thumbnailView;
    }

    public void updateIndex(int index, File newest) {
        String path = newest.getPath();
        Log.d("C2P", path);
        mFilePaths.set(index, newest.getPath());
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),256,256);
        mThumbnails.set(index,thumbnail);

    }
}
