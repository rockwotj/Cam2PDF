package com.tylerrockwood.software.cam2pdf;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.tylerrockwood.software.cam2pdf.backgroundTasks.SaveImageTask;
import com.tylerrockwood.software.cam2pdf.backgroundTasks.UpvertTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements CameraFragment.PictureCallback, ImagesFragment.ImagesCallback, ViewPager.OnPageChangeListener, UploadsFragment.UploadsCallback {


    private static final String PREF_ACCOUNT_NAME = "PREFS";
    private static final int REQUEST_ACCOUNT_PICKER = 101;
    public static final int REQUEST_AUTHORIZATION = 102;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 103;
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


    private List<String> mPhotoPaths;
    private List<Bitmap> mThumbnails;
    private SaveImageTask mSaveImageTask;
    private UpvertTask mUpvertTask;
    private ActionBar mActionBar;
    private GoogleAccountCredential mCredential;
    private Drive mService;
    private Menu mMenu;
    private AlertDialog mDriveDialog;


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
        mViewPager.setCurrentItem(1);
        mViewPager.setOnPageChangeListener(this);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.hide();
            mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        }
        // Google Accounts
        mCredential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE));
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        // Drive client
        mService = new Drive
                .Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), mCredential)
                .setApplicationName(getString(R.string.app_name))
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();

        if (!hasMenuKey) {
            // Do whatever you need to do, this device has a navigation bar
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main_old, menu);
        }
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pick_account) {
            chooseAccount();
            return true;
        }
        boolean result = this.mSectionsPagerAdapter.sendActionToCurrentFragment(item);
        return result || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                if (mMenu != null) {
                    mMenu.performIdentifierAction(R.id.menu_overflow, 0);
                }
        }
        return super.onKeyUp(keycode, e);
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
        if (position != 1) {
            boolean onImagesFragment = position == 2;
            MenuItem item = mMenu.findItem(R.id.action_pick);
            item.setVisible(onImagesFragment);
            item = mMenu.findItem(R.id.action_delete);
            item.setVisible(onImagesFragment);
            mActionBar.setDisplayShowHomeEnabled(onImagesFragment);
            if (onImagesFragment) {
                mActionBar.setTitle(getString(R.string.captured_images));
                mSectionsPagerAdapter.updateImagesFragment();
            } else {
                mActionBar.setTitle(getString(R.string.recent_uploads));
                mSectionsPagerAdapter.updateUploadsFragment();
            }
            mActionBar.show();
        } else {
            mActionBar.hide();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if (mPhotoPaths.size() > 0) {
            AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(this);
            builder.setTitle(R.string.confirm_exit);
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
        if (mUpvertTask != null && mUpvertTask.getStatus() == AsyncTask.Status.RUNNING) {
            mUpvertTask.cancel(true);
        }
        Log.d("C2P", "Deleted temp image files");
    }


    public void saveToDrive() {
        if (mDriveDialog == null) {
            View v = getLayoutInflater().inflate(R.layout.dialog_upvert, null);
            final EditText fileInput = (EditText) v.findViewById(R.id.filenameInput);
            final Spinner folderInput = (Spinner) v.findViewById(R.id.folderSpinner);
            fileInput.setText("exported.pdf");
            final DriveFolderAdapter adapter = new DriveFolderAdapter(this, mService);
            folderInput.setAdapter(adapter);
            mDriveDialog = new AlertDialogWrapper.Builder(this)
                    .setTitle(getString(R.string.upvert_dialog_title))
                    .setView(v)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] params = mPhotoPaths.toArray(new String[mPhotoPaths.size()]);
                            String filename = fileInput.getText().toString();
                            String folderPath = folderInput.getSelectedItem().toString();
                            File folder = adapter.getFileFromTitle(folderPath);
                            mUpvertTask = new UpvertTask(MainActivity.this, mService, filename, folder, folderPath);
                            mUpvertTask.execute(params);
                        }
                    }).create();
        }
        mDriveDialog.show();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d("C2P", "Returning from intent in Main Activity");
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    saveToDrive();
                } else {
                    chooseAccount();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        mSectionsPagerAdapter.updateUploadsFragment();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     */
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (mCredential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        } else {
            mSectionsPagerAdapter.updateUploadsFragment();
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog =
                        GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, MainActivity.this,
                                REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private void chooseAccount() {
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGooglePlayServicesAvailable()) {
            haveGooglePlayServices();
        }
    }

    @Override
    public String getEmail() {
        String email = mCredential.getSelectedAccountName();
        Log.d("C2P", "User email: " + email);
        return email;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private CameraFragment mCurrentCameraFragment;
        private ImagesFragment mCurrentImagesFragment;
        private UploadsFragment mCurrentUploadsFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void updateImagesFragment() {
            if (mCurrentImagesFragment != null) {
                mCurrentImagesFragment.updateView();
            }
        }

        public void updateUploadsFragment() {
            if (mCurrentUploadsFragment != null)
                mCurrentUploadsFragment.onRefresh();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    mCurrentUploadsFragment = new UploadsFragment();
                    return mCurrentUploadsFragment;
                case 1:
                    mCurrentCameraFragment = new CameraFragment();
                    return mCurrentCameraFragment;
                default:
                    mCurrentImagesFragment = new ImagesFragment();
                    return mCurrentImagesFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

        public boolean sendActionToCurrentFragment(MenuItem item) {
            return mCurrentImagesFragment != null && mCurrentImagesFragment.onOptionsItemSelected(item);
        }

    }


}
