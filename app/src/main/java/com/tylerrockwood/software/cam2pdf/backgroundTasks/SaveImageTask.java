package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by rockwotj on 1/21/2015.
 */
public class SaveImageTask extends AsyncTask<Void, Void, Boolean> {

    private final Bitmap mImage;
    private final String mFilename;

    public SaveImageTask(Bitmap image, String filename) {
        this.mImage = image;
        this.mFilename = filename;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {


        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFilename);
            mImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /* For example, here's a method that creates a directory for a new photo album in the public pictures directory: */
    private File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("C2Pc", "Directory not created");
        }

        return file;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
