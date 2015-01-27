package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.tylerrockwood.software.cam2pdf.ImageUtils;
import com.tylerrockwood.software.cam2pdf.MainActivity;
import com.tylerrockwood.software.cam2pdf.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class UpvertService extends IntentService {
    private static final String EXTRA_IMAGES = "com.tylerrockwood.software.cam2pdf.backgroundTasks.extra.IMAGE_LIST";
    private static final String EXTRA_FOLDER = "com.tylerrockwood.software.cam2pdf.backgroundTasks.extra.DRIVE_FOLDER";
    private static final String EXTRA_NAME = "com.tylerrockwood.software.cam2pdf.backgroundTasks.extra.PDF_NAME";

    private static final int NOTIFICATION_ID = 001;
    private static final int DOCUMENT_MARGIN = 25;

    /**
     * Starts this service to perform the action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startService(Context context, List<String> images, String fileName, String driveFolder) {
        Intent intent = new Intent(context, UpvertService.class);
        intent.putExtra(EXTRA_IMAGES, images.toArray(new String[0]));
        intent.putExtra(EXTRA_NAME, fileName);
        intent.putExtra(EXTRA_FOLDER, driveFolder);
        context.startService(intent);
    }

    public UpvertService() {
        super("UpvertService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String[] images = intent.getStringArrayExtra(EXTRA_IMAGES);
            final String filename = intent.getStringExtra(EXTRA_NAME);
            final String folder = intent.getStringExtra(EXTRA_FOLDER);
            try {
                convertToPdf(images, filename);
            } catch (Exception e) {
                Log.d("C2P", "ERROR: cannot export PDF", e);
                // TODO: DISPLAY ERROR
            }
            uploadToDrive(folder);
            // TODO: EXTRACT & MAKE PROGRESS BAR
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
            mBuilder.setContentTitle("My notification");
            mBuilder.setContentText("Hello World!");
            // Sets an ID for the notification
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private File convertToPdf(String[] images, String filename) throws Exception {
        // Get output Directory
        File myDir = ImageUtils.getAlbumStorageDir(MainActivity.ALBUM_NAME);
        // Create the PDF and set some metadata
        Document document = new Document(PageSize.A4, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN);
        Resources resources = getResources();
        document.addTitle(filename);
        document.addAuthor(resources.getString(R.string.app_name));
        document.addSubject(resources.getString(R.string.file_subject));
        // Open the file that we will write the pdf to.
        File pdf = new File(myDir, filename);
        OutputStream outputStream = new FileOutputStream(pdf);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        // Get the document's size
        Rectangle pageSize = document.getPageSize();
        float pageWidth = pageSize.getWidth() - (document.leftMargin() + document.rightMargin());
        float pageHeight = pageSize.getHeight();
        //Loop through images and add them to the directory
        for (String path : images) {
            Image image = Image.getInstance(path);
            image.scaleToFit(pageWidth, pageHeight);
            document.add(image);
            document.newPage();
        }
        // Cleanup
        document.close();
        outputStream.close();
        return pdf;
    }


    private void uploadToDrive(String driveFolder) {
        // TODO
    }
}
