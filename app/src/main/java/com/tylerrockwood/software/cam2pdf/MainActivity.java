package com.tylerrockwood.software.cam2pdf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.tylerrockwood.software.cam2pdf.backgroundTasks.SaveImageTask;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements CameraFragment.PictureCallback, ImagesFragment.ThumbnailsCallback, ViewPager.OnPageChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static final String ALBUM_NAME = "Cam2PDF";

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_CREATOR = 31415;
    private static final int DOCUMENT_MARGIN = 25;

    private List<String> mPhotoPaths;
    private List<Bitmap> mThumbnails;
    private SaveImageTask mSaveImageTask;
    private ActionBar mActionBar;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ImageUtils.clearStorageDir(ALBUM_NAME);
        ImageUtils.noMediaScan(ALBUM_NAME);
        mPhotoPaths = new ArrayList<>();
        mThumbnails = new ArrayList<>();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle(null);
            mActionBar.setIcon(R.drawable.ic_logo);
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = this.mSectionsPagerAdapter.sendActionToCurrentFragment(item);
        return result || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean canTakePicture() {
        return mSaveImageTask == null || mSaveImageTask.getStatus() == AsyncTask.Status.FINISHED;
    }

    @Override
    public void onPictureTaken(Bitmap image) {
        ImageButton shutter = (ImageButton) findViewById(R.id.camera_button);
        String fileName = getString(R.string.image_name, mPhotoPaths.size()) + ".jpg";
        // Create an Async task to save the image
        mSaveImageTask = new SaveImageTask(image, fileName, ALBUM_NAME);
        mThumbnails.add(ThumbnailUtils.extractThumbnail(image, 256, 256));
        mSaveImageTask.execute();
        mPhotoPaths.add(ImageUtils.getAlbumStorageDir(ALBUM_NAME) + "/" + fileName);
        Log.d("CDP", "Created Thumb");
        Toast texas = Toast.makeText(this, R.string.captured_image, Toast.LENGTH_SHORT);
        texas.setGravity(Gravity.CENTER, 0, 0);
        texas.show();
        shutter.setClickable(true);
    }

    @Override
    public List<String> getPhotoPaths() {
        return mPhotoPaths;
    }

    @Override
    public List<Bitmap> getThumbnails() {
        return mThumbnails;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            mActionBar.show();
            mSectionsPagerAdapter.updateCurrentFragment();
        } else if (position == 0) {
            mActionBar.hide();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if (mPhotoPaths.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.confirm_exit);
            builder.setIcon(R.drawable.ic_alert);
            builder.setMessage(R.string.exit_message);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageUtils.clearStorageDir(ALBUM_NAME);
        mPhotoPaths.clear();
        mThumbnails.clear();
        Log.d("C2P", "Deleted temp image files");
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void saveToDrive() {
        final List<String> photos = mPhotoPaths;
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                try {
                    String filename = "exported.pdf";
                    // Get output Directory
                    // Create the PDF and set some metadata
                    Document document = new Document(PageSize.A4, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN);
                    Resources resources = getResources();
                    document.addTitle(filename);
                    document.addAuthor(resources.getString(R.string.app_name));
                    document.addSubject(resources.getString(R.string.file_subject));
                    // Open the file that we will write the pdf to.
                    OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();
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
                    // Set Mime type
                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setMimeType("application/pdf").setTitle(filename).build();
                    // Create an intent for the file chooser, and start it.
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(driveContentsResult.getDriveContents())
                            .build(mGoogleApiClient);
                    try {
                        startIntentSenderForResult(
                                intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i("C2P", "Failed to launch file chooser.");
                    }
                } catch (Exception e) {
                    Log.e("C2P", "unable to upvert", e);
                    return;
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private CameraFragment mCurrentCameraFragment;
        private ImagesFragment mCurrentImagesFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void updateCurrentFragment() {
            if (mCurrentImagesFragment != null) {
                mCurrentImagesFragment.updateView();
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    mCurrentCameraFragment = new CameraFragment();
                    return mCurrentCameraFragment;
                default:
                    mCurrentImagesFragment = new ImagesFragment();
                    return mCurrentImagesFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

        public boolean sendActionToCurrentFragment(MenuItem item) {
            if (mCurrentImagesFragment != null) {
                return mCurrentImagesFragment.onOptionsItemSelected(item);
            }
            return false;
        }
    }


}
