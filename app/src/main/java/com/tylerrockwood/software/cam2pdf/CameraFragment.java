package com.tylerrockwood.software.cam2pdf;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class CameraFragment extends Fragment implements Camera.PictureCallback, View.OnClickListener {

    private Camera camera;
    private SurfaceView cameraView;
    private SurfaceHolder cameraPreview;
    private boolean inPreview;
    private boolean cameraConfigured;
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            initPreview(width, height);
            startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            //no-op
        }
    };

    public interface PictureCallback {
        public void onPictureTaken(Bitmap image);
    }

    private PictureCallback onPictureTakenListener;


    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inPreview = false;
        cameraConfigured = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        this.cameraView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
        this.cameraPreview = cameraView.getHolder();
        cameraPreview.addCallback(surfaceCallback);
        rootView.findViewById(R.id.camera_button).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (inPreview) {
            camera.stopPreview();
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        inPreview = false;

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(0);
        } catch (Exception e) {

        }
        startPreview();
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    @Override
    public void onClick(View view) {
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        // Background Task?
        onPictureTakenListener.onPictureTaken(bmp);
        startPreview();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onPictureTakenListener = (PictureCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Camera.PictureCallback");
        }
    }


    private void initPreview(int width, int height) {
        if (camera != null && cameraPreview.getSurface() != null) {
            try {
                camera.setPreviewDisplay(cameraPreview);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height,
                        parameters);
                camera.setDisplayOrientation(90);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPictureTakenListener = null;
    }


}


