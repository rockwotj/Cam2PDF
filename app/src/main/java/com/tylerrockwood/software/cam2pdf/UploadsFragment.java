package com.tylerrockwood.software.cam2pdf;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.mrengineer13.snackbar.SnackBar;


/**
 * Created by rockwotj on 2/10/2015.
 */
public class UploadsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private UploadAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private UploadsCallback mCallBacks;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragments_uploads, container, false);
        ListView uploadsList = (ListView) rootView.findViewById(android.R.id.list);
        mAdapter = new UploadAdapter(getActivity());
        uploadsList.setAdapter(mAdapter);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        uploadsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdapter.getItem(i).toString()));
                intent.setPackage("com.google.android.apps.docs");
                startActivity(intent);
            }
        });
        uploadsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAdapter.remove(i);
                mAdapter.notifyDataSetChanged();
                new SnackBar.Builder(getActivity().getApplicationContext(), rootView)
                        .withOnClickListener(new SnackBar.OnMessageClickListener() {
                            @Override
                            public void onMessageClick(Parcelable parcelable) {
                                mAdapter.undo();
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .withMessageId(R.string.removed)
                        .withActionMessageId(R.string.undo)
                        .withBackgroundColorId(R.color.primary)
//        .withDuration(duration)
                        .show();
                return true;
            }
        });
        onRefresh();
        return rootView;
    }


    @Override
    public void onRefresh() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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
