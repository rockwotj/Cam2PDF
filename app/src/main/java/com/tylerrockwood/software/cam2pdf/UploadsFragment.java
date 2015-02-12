package com.tylerrockwood.software.cam2pdf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


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
        return rootView;
    }
}
