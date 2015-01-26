package com.tylerrockwood.software.cam2pdf;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tylerrockwood.software.cam2pdf.backgroundTasks.SaveImageTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements CameraFragment.PictureCallback, ImagesFragment.ThumbnailsCallback, ViewPager.OnPageChangeListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageUtils.clearAlbumStorageDir(ALBUM_NAME);
        mPhotoPaths = new ArrayList<>();
        mThumbnails = new ArrayList<>();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(null);
            ab.setIcon(R.drawable.ic_logo);
            ab.setDisplayShowHomeEnabled(true);
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
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.delete);
            builder.setMessage(R.string.delete_message);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(mPhotoPaths.size() > 0) {
                        ImageUtils.clearAlbumStorageDir(ALBUM_NAME);
                        mPhotoPaths.clear();
                        mThumbnails.clear();
                        mSectionsPagerAdapter.updateCurrentFragment();
                        Log.d("C2P", "Deleted temp image files");
                    }
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean canTakePicture() {
        return mSaveImageTask == null || mSaveImageTask.getStatus() == AsyncTask.Status.FINISHED;
    }

    @Override
    public void onPictureTaken(Bitmap image) {
        ImageButton shutter = (ImageButton)findViewById(R.id.camera_button);
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
            mSectionsPagerAdapter.updateCurrentFragment();
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
        ImageUtils.clearAlbumStorageDir(ALBUM_NAME);
        mPhotoPaths.clear();
        mThumbnails.clear();
        Log.d("C2P", "Deleted temp image files");
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
    }


}
