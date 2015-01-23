package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.tylerrockwood.software.cam2pdf.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by rockwotj on 1/21/2015.
 */
public class SaveImageTask extends AsyncTask<Void, Void, Boolean> {

    private static final int JPEG_QUALITY = 70;
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
        File myDir = ImageUtils.getAlbumStorageDir(mAlbumName);
        // Get the file to save it in
        File file = new File(myDir, mFilename);
        // If a file is already there, delete it
        if (file.exists()) file.delete();
        // Now save the file as a PNG (no loss compression)
        try {
            FileOutputStream out = new FileOutputStream(file);
            mImage.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        Log.d("C2P", "Finished saving JPG file");
        mImage.recycle();
        return Boolean.TRUE;
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
