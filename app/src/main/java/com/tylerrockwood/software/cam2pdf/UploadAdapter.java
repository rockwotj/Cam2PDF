package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rockwotj on 2/11/2015.
 */
public class UploadAdapter extends BaseAdapter {

    private final List<Upload> mUploads;
    private Context mContext;

    public UploadAdapter(Context context) {
        mContext = context;
        mUploads = Arrays.asList(new Upload(0, "exported.pdf", "root", "01/01/2015"));
    }

    @Override
    public int getCount() {
        return mUploads.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return mUploads.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        if (view == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.view_upload, viewGroup, false);
        } else {
            v = view;
        }
        TextView filename = (TextView) v.findViewById(R.id.filename);
        TextView date = (TextView) v.findViewById(R.id.date);
        Upload upload = mUploads.get(i);
        filename.setText(upload.getName());
        date.setText(upload.getCreationDate());
        return v;
    }

    public void remove(Object item) {
    }
}
