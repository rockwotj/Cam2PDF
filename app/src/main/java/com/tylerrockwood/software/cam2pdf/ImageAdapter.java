package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The adapter for the grid view of images within the {@link com.tylerrockwood.software.cam2pdf.ImagesFragment}
 * Created by rockwotj on 1/19/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private static final int THUMBNAIL_SIZE = 256;
    private final Context mContext;
    private final List<Bitmap> mThumbnails;
    private final List<String> mFilePaths;
    private final List<Integer> mSelected;

    public ImageAdapter(Context context, List<String> filePaths, List<Bitmap> thumbnails) {
        mContext = context;
        mFilePaths = filePaths;
        mThumbnails = thumbnails;
        mSelected = new ArrayList<>();
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
        // Async task to delete the photo?
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ThumbnailView thumbnailView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            thumbnailView = new ThumbnailView(mContext);
        } else {
            thumbnailView = (ThumbnailView) convertView;
        }
        thumbnailView.setChecked(mSelected.contains(position));
        thumbnailView.setThumbnailBitmap(mThumbnails.get(position));
        return thumbnailView;
    }

    public void updateIndex(int index, File newest) {
        String path = newest.getPath();
        Log.d("C2P", path);
        mFilePaths.set(index, newest.getPath());
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        mThumbnails.set(index, thumbnail);
        notifyDataSetChanged();
    }


    public void setChecked(int index, boolean enabled) {
        if (enabled) {
            this.mSelected.add(index);
        } else {
            this.mSelected.remove(Integer.valueOf(index));
        }
    }

    public void uncheckAll() {
        mSelected.clear();
    }

}
