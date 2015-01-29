package com.tylerrockwood.software.cam2pdf;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Some utility methods for storing images.
 * Created by rockwotj on 1/21/2015.
 */
public class ImageUtils {
    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("C2P", "Directory not created");
        }
        return file;
    }

    public static void noMediaScan(String albumName) {
        File myDir = ImageUtils.getAlbumStorageDir(albumName);
        // Get the file to save it in
        File file = new File(myDir, ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("C2P", ".nomedia not created", e);
            }
        }
    }

    public static void clearStorageDir(String albumName) {
        File file = ImageUtils.getAlbumStorageDir(albumName);
        String[] myFiles;
        myFiles = file.list();
        // Only Delete jpgs?
        for (int i = 0; i < myFiles.length; i++) {
            File myFile = new File(file, myFiles[i]);
            myFile.delete();
        }
    }


}
