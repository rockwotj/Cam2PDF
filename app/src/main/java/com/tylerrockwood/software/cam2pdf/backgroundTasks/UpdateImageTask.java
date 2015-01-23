package com.tylerrockwood.software.cam2pdf.backgroundTasks;

import android.os.AsyncTask;

import com.tylerrockwood.software.cam2pdf.ImagesFragment;

/**
 * Created by gregorycallegari on 1/22/15.
 */
public class UpdateImageTask extends AsyncTask<Void,Void,Void> {

    private final ImagesFragment mImageFrag;

    public UpdateImageTask(ImagesFragment frag){
        mImageFrag = frag;
    }


    @Override
    protected Void doInBackground(Void... params) {
        mImageFrag.imageEdited();

        //but its "V"oid
        return null;
    }
}
