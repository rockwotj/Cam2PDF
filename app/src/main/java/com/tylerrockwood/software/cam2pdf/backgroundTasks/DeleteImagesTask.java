package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by rockwotj on 1/21/2015.
 */
public class DeleteImagesTask extends AsyncTask<Void, Void, Void> {

    private final List<String> mImagePaths;

    public DeleteImagesTask(List<String> mImagePaths) {
        this.mImagePaths = mImagePaths;
    }


    @Override
    protected Void doInBackground(Void... strings) {
        for (String path : mImagePaths) {
            //Delete files
        }
        return null;
    }
}
