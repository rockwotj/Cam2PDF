package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.os.AsyncTask;

/**
 * Created by rockwotj on 1/21/2015.
 */
public class DeleteImagesTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... imagePaths) {
        for (String path : imagePaths) {
            //Delete files
        }
        return null;
    }
}
