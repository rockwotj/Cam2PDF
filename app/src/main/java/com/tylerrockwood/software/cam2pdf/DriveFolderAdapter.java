package com.tylerrockwood.software.cam2pdf;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rockwotj on 2/8/2015.
 */
public class DriveFolderAdapter extends ArrayAdapter {

    private final Drive mDrive;

    private final Map<String, File> mFolderMap;

    public DriveFolderAdapter(Context context, Drive service) {
        super(context, android.R.layout.simple_spinner_item, new ArrayList());
        add("/");
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDrive = service;
        mFolderMap = new HashMap<>();
        mFolderMap.put("/", null);
        new GetFoldersTask("root", "/").execute();
    }

    public File getFileFromTitle(String folderString) {
        return mFolderMap.get(folderString);
    }

    private class GetFoldersTask extends AsyncTask<Void, Void, Map<String, File>> {

        private final String id;
        private final String path;

        public GetFoldersTask(String id, String path) {
            this.id = id;
            this.path = path;
        }

        @Override
        protected Map<String, File> doInBackground(Void... voids) {
            try {
                return getFolderChildren(id, path);
            } catch (IOException e) {
                Log.d("C2P", "Error in GetFoldersTask", e);
            }
            return null;
        }

        public Map<String, File> getFolderChildren(String id, String path) throws IOException {
            Map<String, File> folders = new HashMap<>();
            Drive.Files.List files = mDrive.files().list();
            files.setQ("mimeType = 'application/vnd.google-apps.folder' and '" + id + "' in parents");
            FileList list = files.execute();
            for (File f : list.getItems()) {
                String newPath = path + f.getTitle() + "/";
                folders.put(newPath, f);
                new GetFoldersTask(f.getId(), newPath).execute();
            }
            return folders;
        }

        @Override
        protected void onPostExecute(Map<String, File> folders) {
            if (folders != null && folders.size() > 0) {
                ArrayList<String> list = new ArrayList<>(folders.keySet());
                Log.d("C2P", "Folders found: " + list.size());
                Collections.sort(list, new Comparator<String>() {
                    @Override
                    public int compare(String s, String s2) {
                        int slashCompare = countSlashes(s) - countSlashes(s2);
                        return slashCompare == 0 ? s.length() - s2.length() : slashCompare;
                    }

                    public int countSlashes(String s) {
                        int count = 0;
                        for (int i = 0; i < s.length(); i++) {
                            if (s.charAt(i) == '/')
                                count++;
                        }
                        return count;
                    }
                });
                mFolderMap.putAll(folders);
                DriveFolderAdapter.this.addAll(list);
                DriveFolderAdapter.this.notifyDataSetChanged();
            }
        }
    }
}
