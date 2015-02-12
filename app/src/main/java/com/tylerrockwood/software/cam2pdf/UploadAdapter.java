package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
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
        // TODO: actually pull values from a database
        mUploads = Arrays.asList(new Upload(0, "exported.pdf", "/", "956KB", "root", "01/01/2015"));
    }

    @Override
    public int getCount() {
        return mUploads.size();
    }

    public void remove(Object item) {
        mUploads.remove(item);
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        if (view == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.view_upload, viewGroup, false);
        } else {
            v = view;
        }
        final Upload upload = mUploads.get(i);
        TextView filename = (TextView) v.findViewById(R.id.filename);
        TextView date = (TextView) v.findViewById(R.id.date);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(upload.toString()));
                intent.setPackage("com.google.android.apps.docs");
                mContext.startActivity(intent);
            }
        });
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


}
