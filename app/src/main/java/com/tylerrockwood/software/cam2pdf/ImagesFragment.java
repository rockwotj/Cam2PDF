package com.tylerrockwood.software.cam2pdf;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends Fragment {


    public ImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        //http://stackoverflow.com/questions/4916159/android-get-thumbnail-of-image-on-sd-card-given-uri-of-original-image
        gridView.setAdapter(new ImageAdapter(getActivity()));
        return rootView;
    }


}
