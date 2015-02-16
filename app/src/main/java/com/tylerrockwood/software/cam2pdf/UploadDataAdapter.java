package com.tylerrockwood.software.cam2pdf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Greg on 2/15/2015.
 */
public class UploadDataAdapter {
    // Becomes the filename of the database
    private static final String DATABASE_NAME = "cam2pdf.db";
    // Only one table in this database
    private static final String TABLE_NAME = "recents";
    // We increment this every time we change the database schema which will
    // kick off an automatic upgrade
    private static final int DATABASE_VERSION = 1;

    private SQLiteOpenHelper mOpenHelper;
    private SQLiteDatabase mDatabase;

    static final String KEY_ID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_PATH = "path";
    static final String KEY_SIZE = "size";
    static final String KEY_PARENT = "parent";
    static final String KEY_DATE = "date";
    static final String KEY_EMAIL = "email";


    public UploadDataAdapter(Context context) {
        mOpenHelper = new UploadDbHelper(context);

    }

    public void open() {
        mDatabase = mOpenHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public long addUpload(Upload upload) {
        ContentValues row = getContentValues(upload);
        long id = mDatabase.insert(TABLE_NAME, null, row);
        upload.setId(id);
        return id;
    }

    private ContentValues getContentValues(Upload upload) {
        ContentValues row = new ContentValues();
        row.put(KEY_NAME, upload.getName());
        row.put(KEY_PATH, upload.getPath());
        row.put(KEY_SIZE, upload.getSize());
        row.put(KEY_PARENT, upload.getParentFolder());
        row.put(KEY_DATE, upload.getCreationDate());
        row.put(KEY_EMAIL, upload.getUploadersEmail());
        return row;
    }

    public void deleteUpload(Upload upload) {
        mDatabase.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{Long.toString(upload.getId())});
    }


    public Upload getUploadFromCursor(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME));
        String path = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PATH));
        String size = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SIZE));
        String parent = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PARENT));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL));
        Long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID));
        Upload upload = new Upload(id, name, path, size, parent, date, email);
        return upload;
    }


    public void setAllUploads(ArrayList<Upload> uploads, String email) {
        String[] columns = null;
        String whereClause = (email == null) ? null : KEY_EMAIL + "=" + "'" + email + "'";
        Cursor cursor = mDatabase.query(TABLE_NAME, columns, whereClause, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        uploads.clear();
        do {
            uploads.add(getUploadFromCursor(cursor));
        } while (cursor.moveToNext());
        Collections.sort(uploads);
    }

    private static class UploadDbHelper extends SQLiteOpenHelper {

        private static final String CREATE_STATEMENT;

        static {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE " + TABLE_NAME + "(");
            sb.append(KEY_ID + " Integer primary key autoincrement, ");
            sb.append(KEY_NAME + " text, ");
            sb.append(KEY_PATH + " text, ");
            sb.append(KEY_SIZE + " text, ");
            sb.append(KEY_PARENT + " text, ");
            sb.append(KEY_DATE + " text, ");
            sb.append(KEY_EMAIL + " text");
            sb.append(");");
            CREATE_STATEMENT = sb.toString();
        }

        private static final String DROP_STATEMENT = "DROP TABLE IF EXISTS "
                + TABLE_NAME;


        public UploadDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STATEMENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_STATEMENT);
            db.execSQL(CREATE_STATEMENT);
        }

    }


}
