package com.tylerrockwood.software.cam2pdf;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
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

    public static void clearAlbumStorageDir(String albumName) {
        File file = ImageUtils.getAlbumStorageDir(albumName);
        String[] myFiles;
        myFiles = file.list();
        for (int i = 0; i < myFiles.length; i++) {
            File myFile = new File(file, myFiles[i]);
            myFile.delete();
        }
    }


}
