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

import java.util.List;


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
            camera.setDisplayOrientation(90);
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

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
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
                Camera.Size size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
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


