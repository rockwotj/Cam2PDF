package com.tylerrockwood.software.cam2pdf;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import static java.lang.Thread.sleep;


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
            Log.d("C2P", "onSurfaceDestroyed");
            if (camera != null) {
                camera.stopPreview();
            }
        }
    };
    private RelativeLayout rootView;

    public interface PictureCallback {
        public void onPictureTaken(Bitmap image);

        public boolean canTakePicture();
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
        Log.d("C2P", "onCreateView");
        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_camera, container, false);
        this.cameraView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
        this.cameraPreview = cameraView.getHolder();
        cameraPreview.addCallback(surfaceCallback);
        rootView.findViewById(R.id.camera_button).setOnClickListener(this);
        return rootView;
    }

    private void stopPreviewAndFreeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            rootView.removeView(cameraView);
            cameraView = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPreviewAndFreeCamera();
    }

    @Override
    public void onResume() {
        Log.d("C2P", "onResume");
        super.onResume();
        try {
            if (camera == null) {
                camera = Camera.open();
            }
            camera.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.d("C2P", "Camera not opened!");
        }
        if (this.cameraView == null) {
            this.cameraView = new SurfaceView(getActivity());
            this.rootView.addView(cameraView, 0);
            this.cameraPreview = cameraView.getHolder();
            cameraPreview.addCallback(surfaceCallback);
        }
        startPreview();
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            Log.d("C2P", "Starting Preview");
            camera.startPreview();
            inPreview = true;
        }
    }

    @Override
    public void onClick(View view) {
        ImageButton shutter = (ImageButton)getActivity().findViewById(R.id.camera_button);
        shutter.setClickable(false);
        if (onPictureTakenListener.canTakePicture()) {
            camera.takePicture(null, null, this);
        } else {
            // Should probably make some Toast...
            try{sleep(100);}catch(InterruptedException e){}
            shutter.setClickable(true);
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        ImageButton shutter = (ImageButton)getActivity().findViewById(R.id.camera_button);
        shutter.setClickable(true);
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        // Background Task?
        onPictureTakenListener.onPictureTaken(rotateBitmap(bmp));
        startPreview();
    }

    public static Bitmap rotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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


