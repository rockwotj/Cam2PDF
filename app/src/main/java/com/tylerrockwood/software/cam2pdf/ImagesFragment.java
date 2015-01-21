package com.tylerrockwood.software.cam2pdf;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {


    private ImageAdapter mAdapter;

    public ImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        mAdapter = new ImageAdapter(getActivity());
        gridView.setAdapter(mAdapter);
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
        return false;
    }
}
