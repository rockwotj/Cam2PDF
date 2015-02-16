package com.tylerrockwood.software.cam2pdf;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The fragment for viewing/editing/deleting images that have been taken by the camera.
 */
public class ImagesFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    private static final int EDIT_PHOTO = 101;
    private static final int PICK_PHOTO = 100;
    private List<String> mPhotos;
    private ImageAdapter mAdapter;
    private List<Bitmap> mThumbnails;
    private int mCurrentEditedIndex;
    private ActionMode mActionMode = null;
    private List<Integer> mCheckedItems;
    private ImagesCallback mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCheckedItems = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        rootView.findViewById(R.id.upvert_button).setOnClickListener(this);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        mAdapter = new ImageAdapter(getActivity(), mPhotos, mThumbnails);
        gridView.setAdapter(mAdapter);
        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
            builder.setTitle(R.string.delete);
            builder.setMessage(R.string.delete_message);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mPhotos.size() > 0) {
                        ImageUtils.clearStorageDir(MainActivity.ALBUM_NAME);
                        mPhotos.clear();
                        mThumbnails.clear();
                        updateView();
                        Log.d("C2P", "Deleted temp image files");
                    }
                }
            });
            builder.show();
            return true;
        } else if (id == R.id.action_pick) {
            Log.d("C2P", "Should start intent for picker");
            startPickIntent();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        if (mActionMode != null) {
            boolean isSelecting = !mCheckedItems.contains(index);
            if (isSelecting) {
                mCheckedItems.add(index);
            } else {
                mCheckedItems.remove(Integer.valueOf(index));
            }
            mAdapter.setChecked(index, isSelecting);
            if (mCheckedItems.size() == 0) {
                mActionMode.finish();
                return;
            }
            String s = mCheckedItems.size() != 1 ? getString(R.string.delete_selected_format, mCheckedItems.size()) : getString(R.string.delete_selected_one);
            mActionMode.setTitle(s);
        } else {
            final int i = index;
            AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
            builder.setTitle(R.string.delete_selected);
            builder.setMessage(R.string.delete_selected_message);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(R.string.delete_image, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAdapter.deleteItem(i);
                }
            });
            builder.setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startEditIntent(i);
                }
            });
            builder.show();
        }
        updateView();
    }

    private void startEditIntent(int index) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_EDIT);
        String filepath = mAdapter.getItem(index);
        Log.d("C2P", "Starting intent to: " + filepath);
        mCurrentEditedIndex = index;
        intent.setDataAndType(Uri.parse("file://" + filepath), "image/*");
        startActivityForResult(intent, EDIT_PHOTO);
    }

    private void startPickIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("C2P", "Returning from intent in Images Fragment");
        if (requestCode == EDIT_PHOTO && resultCode == Activity.RESULT_OK) {
            File newest = getNewestFileInDirectory();
            Log.d("C2P", "New file: " + newest.toString());
            mAdapter.updateIndex(mCurrentEditedIndex, newest);
        } else if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            mAdapter.addItem(new File(filePath));
        } else {
            Log.d("C2P", "RESULT_CANCELLED");
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        if (mActionMode != null) {
            return true;
        }
        mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(new ImagesFragmentActionModeCallback());
        mActionMode.setTitle(R.string.delete_selected);
        onItemClick(adapterView, view, index, l);
        return true;
    }


    public void updateView() {
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (ImagesCallback) activity;
            this.mPhotos = mCallbacks.getPhotoPaths();
            this.mThumbnails = mCallbacks.getThumbnails();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ImagesFragment.ThumbnailsCallback");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.upvert_button) {
            mCallbacks.saveToDrive();
        }
    }

    public interface ImagesCallback {
        public List<String> getPhotoPaths();

        public List<Bitmap> getThumbnails();

        public void saveToDrive();
    }

    private class ImagesFragmentActionModeCallback implements ActionMode.Callback {


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete_selected) {
                Collections.sort(mCheckedItems);
                Collections.reverse(mCheckedItems);
                for (Integer i : mCheckedItems) {
                    mAdapter.deleteItem(i);
                }
                updateView();
                mActionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.uncheckAll();
            mAdapter.notifyDataSetChanged();
            mCheckedItems.clear();
            mActionMode = null;

        }
    }
}
