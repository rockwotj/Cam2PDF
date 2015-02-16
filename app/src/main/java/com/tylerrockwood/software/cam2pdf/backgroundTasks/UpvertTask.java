package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
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
import com.tylerrockwood.software.cam2pdf.Upload;
import com.tylerrockwood.software.cam2pdf.UploadDataAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by rockwotj on 2/8/2015.
 */
public class UpvertTask extends AsyncTask<String, Void, Exception> {

    private static final int DOCUMENT_MARGIN = 25;
    private static final int NOTIFICATION_ID = 1;
    private final Drive mService;
    private final String mFilename;
    private final File mFolder;
    private String mFolderPath;
    private final Context mContext;
    private Notification.Builder mBuilder;
    private NotificationManager mNotifyMgr;


    public UpvertTask(Context context, Drive service, String filename, File folder, String folderPath) {
        this.mContext = context;
        this.mService = service;
        this.mFilename = filename;
        this.mFolder = folder;
        this.mFolderPath = folderPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Do stuff with a notification
        mBuilder = new Notification.Builder(mContext)
                .setAutoCancel(false)
                .setOngoing(true)
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
            body.setDescription(resources.getString(R.string.file_subject));
            body.setMimeType("application/pdf");
            try {
                // You might want to grab the email Address for the database...
                // That way if they switch accounts the data is per each account.
                // mService.about().get().execute().getUser().getEmailAddress();
                Drive.Files.Insert insert = mService.files().insert(body, mediaContent);
                MediaHttpUploader uploader = insert.getMediaHttpUploader();
                uploader.setDirectUploadEnabled(false);
                uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);
                uploader.setProgressListener(new FileProgressListener());
                File file = insert.execute();
                Log.d("C2P", "File Id: " + file.getId());
                /* Database Code */
                DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                Date date = new Date();
                //file.getFileSize().toString()
                String parentFolder = mFolder != null ? mFolder.getId() : "root";
                Long size = file.getFileSize();
                String fileSizeString = humanReadableByteCount(size);
                Upload upload = new Upload(-1, mFilename, mFolderPath, fileSizeString, parentFolder, format.format(date), mService.about().get().execute().getUser().getEmailAddress());
                UploadDataAdapter mUploadDataAdapter = new UploadDataAdapter(mContext);
                mUploadDataAdapter.open();
                mUploadDataAdapter.addUpload(upload);
                mUploadDataAdapter.close();
            } catch (UserRecoverableAuthIOException e) {
                return e;
            }
        } catch (Exception e) {
            Log.d("C2P", "ERROR", e);
            return e;
        }
        return null;
    }


    /**
     * Taken from
     * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     *
     * @param bytes
     * @return number of bytes in human readable format
     */
    private static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
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
            // Set clickable intent to Drive App
            String parentFolder = mFolder != null ? mFolder.getId() : "root";
            Intent notifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/open?id=" + parentFolder + "&authuser=0"));
            notifyIntent.setPackage("com.google.android.apps.docs");
            mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 1, notifyIntent, PendingIntent.FLAG_ONE_SHOT));
            mBuilder.setAutoCancel(true);
        }
        mBuilder.setProgress(0, 0, false)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_notification_logo);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    protected void onCancelled() {
        mBuilder.setContentTitle(mContext.getString(R.string.notification_error_cancelled))
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_notification_logo);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
        super.onCancelled();
    }

    private class FileProgressListener implements MediaHttpUploaderProgressListener {
        @Override
        public void progressChanged(MediaHttpUploader uploader) throws IOException {
            if (uploader == null) return;
            switch (uploader.getUploadState()) {
                case INITIATION_STARTED:
                    //System.out.println("Initiation has started!");
                    break;
                case INITIATION_COMPLETE:
                    //System.out.println("Initiation is complete!");
                    break;
                case MEDIA_IN_PROGRESS:
                    int percent = (int) (uploader.getProgress() * 100);
                    mBuilder.setProgress(100, percent, false);
                    mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
                    break;
                case MEDIA_COMPLETE:
                    //System.out.println("Upload is complete!");
            }
        }
    }
}
