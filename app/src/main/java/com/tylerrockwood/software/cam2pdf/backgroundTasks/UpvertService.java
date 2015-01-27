package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

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

    /**
     * Starts this service to perform the action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startService(Context context, List<String> images, String driveFolder) {
        Intent intent = new Intent(context, UpvertService.class);
        intent.putExtra(EXTRA_IMAGES, images.toArray(new String[0]));
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
                final String folder = intent.getStringExtra(EXTRA_FOLDER);
                convertToPdf(images);
                uploadToDrive(folder);
        }
    }

    private void convertToPdf(String[] images) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void uploadToDrive(String driveFolder) {

    }
}
