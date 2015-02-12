package com.tylerrockwood.software.cam2pdf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by rockwotj on 2/10/2015.
 */
public class UploadsFragment extends Fragment {

    private UploadAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_uploads, container, false);
        ListView uploadsList = (ListView) rootView.findViewById(R.id.uploads_list);
        mAdapter = new UploadAdapter(getActivity());
        uploadsList.setAdapter(mAdapter);
        TextView emptyView = new TextView(getActivity());
        emptyView.setText("You haven't uploaded anything yet!");
        emptyView.setPadding(0, 20, 0, 0);
        emptyView.setTextSize(18);
        uploadsList.setEmptyView(emptyView);
        return rootView;
    }

}
