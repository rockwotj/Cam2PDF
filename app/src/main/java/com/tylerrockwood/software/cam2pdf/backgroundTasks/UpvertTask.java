package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.tylerrockwood.software.cam2pdf.ImageUtils;
import com.tylerrockwood.software.cam2pdf.MainActivity;
import com.tylerrockwood.software.cam2pdf.R;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by rockwotj on 2/8/2015.
 */
public class UpvertTask extends AsyncTask<String, Void, Exception> {

    private static final int DOCUMENT_MARGIN = 25;
    private static final int NOTIFICATION_ID = 1;
    private final Drive mService;
    private final String mFilename;
    private final File mFolder;
    private final Context mContext;
    private Notification.Builder mBuilder;
    private NotificationManager mNotifyMgr;

    public UpvertTask(Context context, Drive service, String filename, File folder) {
        this.mContext = context;
        this.mService = service;
        this.mFilename = filename;
        this.mFolder = folder;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Do stuff with a notification
        mBuilder = new Notification.Builder(mContext)
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                        android.R.drawable.stat_sys_upload_done))
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setProgress(1, 0, true)
                .setContentTitle(mContext.getString(R.string.notification_start))
                .setContentText(mContext.getString(R.string.app_name));
        mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    protected Exception doInBackground(String... photos) {
        try {
            // Get output Directory
            // Create the PDF and set some metadata
            Document document = new Document(PageSize.A4, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN);
            Resources resources = mContext.getResources();
            document.addTitle(mFilename);
            document.addAuthor(resources.getString(R.string.app_name));
            document.addSubject(resources.getString(R.string.file_subject));
            // Open the file that we will write the pdf to.
            java.io.File fileContent = new java.io.File(ImageUtils.getAlbumStorageDir(MainActivity.ALBUM_NAME) + mFilename);
            OutputStream outputStream = new FileOutputStream(fileContent);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            // Get the document's size
            Rectangle pageSize = document.getPageSize();
            float pageWidth = pageSize.getWidth() - (document.leftMargin() + document.rightMargin());
            float pageHeight = pageSize.getHeight();
            //Loop through images and add them to the document
            for (String path : photos) {
                Image image = Image.getInstance(path);
                image.scaleToFit(pageWidth, pageHeight);
                document.add(image);
                document.newPage();
            }
            // Cleanup
            document.close();
            outputStream.close();
            // Upload time!
            FileContent mediaContent = new FileContent("application/pdf", fileContent);
            File body = new File();
            if (mFolder != null)
                body.setParents(Arrays.asList(new ParentReference().setId(mFolder.getId())));
            body.setTitle(mFilename);
            body.setDescription("Created by Cam2PDF");
            body.setMimeType("application/pdf");
            try {

                File file = mService.files().insert(body, mediaContent).execute();
                Log.d("C2P", "File Id: " + file.getId());
            } catch (UserRecoverableAuthIOException e) {
                return e;
            }
        } catch (Exception e) {
            Log.d("C2P", "ERROR", e);
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Exception exception) {
        if (exception != null && exception instanceof UserRecoverableAuthIOException) {
            Intent intent = ((UserRecoverableAuthIOException) exception).getIntent();
            // Not a super big fan of this... but what else can we do?
            ((Activity) mContext).startActivityForResult(intent, MainActivity.REQUEST_AUTHORIZATION);
        } else if (exception != null) {
            mBuilder.setContentTitle(mContext.getString(R.string.notification_error));
        } else {
            mBuilder.setContentTitle(mContext.getString(R.string.notification_complete));
        }
        mBuilder.setProgress(0, 0, false)
                .setSmallIcon(R.drawable.ic_notification_logo);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
