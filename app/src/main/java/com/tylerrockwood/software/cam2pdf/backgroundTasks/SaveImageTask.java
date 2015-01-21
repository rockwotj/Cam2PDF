package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by rockwotj on 1/21/2015.
 */
public class SaveImageTask extends AsyncTask<Void, Void, Boolean> {

    private final Bitmap mImage;
    private final String mFilename;
    private final String mAlbumName;

    public SaveImageTask(Bitmap image, String filename, String albumName) {
        this.mImage = image;
        this.mFilename = filename;
        this.mAlbumName = albumName;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (!isExternalStorageWritable()) return Boolean.FALSE;
        // Get the dir to save it in
        File myDir = getAlbumStorageDir(mAlbumName);
        // Get the file to save it in
        File file = new File(myDir, mFilename);
        // If a file is already there, delete it
        if (file.exists()) file.delete();
        // Now save the file as a PNG (no loss compression)
        try {
            FileOutputStream out = new FileOutputStream(file);
            mImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
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

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
