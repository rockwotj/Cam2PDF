package com.tylerrockwood.software.cam2pdf;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by rockwotj on 2/10/2015.
 */
public class UploadsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private UploadAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private View mEmptyView;
    private UploadsCallback mCallBacks;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_uploads, container, false);
        ListView uploadsList = (ListView) rootView.findViewById(android.R.id.list);
        mAdapter = new UploadAdapter(getActivity());
        uploadsList.setAdapter(mAdapter);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        //mUploadDataAdapter = new UploadDataAdapter(getActivity());
        //mUploadDataAdapter.open();

        mAdapter.update(mCallBacks.getEmail());
        return rootView;
    }


    @Override
    public void onRefresh() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Sleep to show animation for a while
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.d("C2P", "Background thread no sleep", e);
                }
                mAdapter.update(mCallBacks.getEmail());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
                mSwipeLayout.setRefreshing(false);
            }
        }.execute();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBacks = (UploadsCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UploadsFragment.UploadsCallback");
        }
    }

    //make interface, on attach, on detach set interface to null
    public interface UploadsCallback {
        public String getEmail();
    }
}
