package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by rockwotj on 1/19/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Bitmap> mThumbnails;
    private final List<String> mFilePaths;
    //private final List<Boolean> mSelected;

    public ImageAdapter(Context context, List<String> filePaths, List<Bitmap> thumbnails) {
        mContext = context;
        mFilePaths = filePaths;
        mThumbnails = thumbnails;
        //mSelected = new ArrayList<Boolean>();
        //for(int i = 0; i < 10; i++){
        //    mSelected.add(i,false);
        //}
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
/*        View rootView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            rootView = LayoutInflater.from(mContext).inflate(R.layout.view_thumbnail, null);
        } else {
            rootView = convertView;
        }
        ImageView image = (ImageView)rootView.findViewById(R.id.thumbNail);
        ImageView check = (ImageView)rootView.findViewById(R.id.checked);
        try {
            image.setImageBitmap(mThumbnails.get(position));
        } catch (Exception e) {
            image.setImageResource(R.drawable.ic_default_thumbnail);
        }
        return rootView;*/
        //mSelected.add(false);
        ThumbnailView thumbnailView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes

            thumbnailView = new ThumbnailView(mContext);
            //thumbnailView.setLayoutParams(new GridView.LayoutParams(85, 85));
            thumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnailView.setPadding(8, 8, 8, 8);
            //if(mSelected.get(position))
             //   thumbnailView.setBackground( new ColorDrawable(mContext.getResources().getColor(android.R.color.holo_red_dark)));
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
        notifyDataSetChanged();
    }


/*    public void setEnabled(int index){
        mSelected.set(index, true);
    }

    public boolean isEnabled(int index){
        return mSelected.get(index);
    }

    public void setDisabled(int index){
        mSelected.set(index, false);
    }*/
}
