package com.tylerrockwood.software.cam2pdf;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final int EDIT_PHOTO = 101;
    private List<String> mPhotos;
    private ImageAdapter mAdapter;
    private List<Bitmap> mThumbnails;
    private int mCurrentEditedIndex;

    public ImagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        mAdapter = new ImageAdapter(getActivity(), mPhotos, mThumbnails);
        gridView.setAdapter(mAdapter);
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_EDIT);
        String filepath = mAdapter.getItem(index);
        Log.d("C2P", "Starting intent to: " + filepath);
        mCurrentEditedIndex = index;
        intent.setDataAndType(Uri.parse("file://" + filepath), "image/*");
        startActivityForResult(intent, EDIT_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("C2P", resultCode + "?" + requestCode);
        if (requestCode == EDIT_PHOTO && resultCode == Activity.RESULT_OK) {
            File newest = getNewestFileInDirectory();
            mAdapter.updateIndex(mCurrentEditedIndex, newest);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File getNewestFileInDirectory() {
        File newestFile = null;

        File dir = ImageUtils.getAlbumStorageDir(MainActivity.ALBUM_NAME);

        for (File file : dir.listFiles()) {
            if (newestFile == null || file.lastModified() > newestFile.lastModified()) {
                newestFile = file;
            }
        }
        Log.d("C2P", newestFile.getPath());
        return newestFile;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
        return false;
    }

    public void updateView() {
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            ThumbnailsCallback c = (ThumbnailsCallback) activity;
            this.mPhotos = c.getPhotoPaths();
            this.mThumbnails = c.getThumbnails();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ImagesFragment.ThumbnailsCallback");
        }
    }

    public interface ThumbnailsCallback {
        public List<String> getPhotoPaths();

        public List<Bitmap> getThumbnails();
    }
}
