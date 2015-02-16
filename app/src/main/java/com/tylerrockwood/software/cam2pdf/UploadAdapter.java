package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rockwotj on 2/11/2015.
 */
public class UploadAdapter extends BaseAdapter {

    private final ArrayList<Upload> mUploads;
    private Context mContext;
    protected UploadDataAdapter mUploadDataAdapter;
    private Upload mLastRemoved;


    public UploadAdapter(Context context) {
        mContext = context;
        mUploadDataAdapter = new UploadDataAdapter(mContext);
        mUploads = new ArrayList<>();
    }

    public void update(String email) {
        mUploadDataAdapter.open();
        mUploadDataAdapter.setAllUploads(mUploads, email);
        mUploadDataAdapter.close();

    }

    @Override
    public int getCount() {
        return mUploads.size();
    }

    public void remove(int position) {
        mLastRemoved = mUploads.remove(position);//mUploads.get(position);
        mUploadDataAdapter.open();
        mUploadDataAdapter.deleteUpload(mLastRemoved);
        mUploadDataAdapter.close();

    }

    @Override
    public Upload getItem(int i) {
        return mUploads.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mUploads.get(i).getId();
    }

    @Override
    public View getView(int i, View view, final ViewGroup parent) {
        View v;
        if (view == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.view_upload, parent, false);
        } else {
            v = view;
        }
        final Upload upload = mUploads.get(i);
        TextView filename = (TextView) v.findViewById(R.id.filename);
        TextView date = (TextView) v.findViewById(R.id.date);
        final View infoButton = v.findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, infoButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.upload_info_popup, popup.getMenu());
                popup.getMenu().getItem(0).setTitle(mContext.getResources().getString(R.string.folder_colon) + upload.getPath());
                popup.getMenu().getItem(1).setTitle(mContext.getResources().getString(R.string.size) + upload.getSize());
                popup.show();
            }
        });
        filename.setText(upload.getName());
        date.setText(upload.getCreationDate());
        return v;
    }


    public void undo() {
        mUploads.add(mLastRemoved);
        mUploadDataAdapter.open();
        mUploadDataAdapter.addUpload(mLastRemoved);
        mUploadDataAdapter.close();
        mLastRemoved = null;
        Collections.sort(mUploads);
    }
}
