package com.tylerrockwood.software.cam2pdf;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by rockwotj on 1/19/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private List<String> mFilePaths;

    public ImageAdapter(Context context, List<String> filePaths) {
        mContext = context;
        mFilePaths = filePaths;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int i) {
        return mFilePaths.get(i);
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
            thumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnailView.setPadding(8, 8, 8, 8);
        } else {
            thumbnailView = (ThumbnailView) convertView;
        }
        thumbnailView.setImageResource(mThumbIds[position]);
        return thumbnailView;
    }

    public Bitmap getThumbnail(String path) throws Exception {
        ContentResolver cr = mContext.getContentResolver();
        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        ca.close();
        return null;

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
