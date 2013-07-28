package com.greylocku.lazer;

import android.content.pm.PackageManager;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import java.util.Arrays;

public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    public static final String  TAG              = "DAJIM";
    // fields for the game
    public static final int RADIUS = 10;

    //    MatOfInt[] mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
//    int mHistSizeNum = 25;
//    MatOfInt mHistSize = new MatOfInt(mHistSizeNum);
//    float[] mBuff = new float[mHistSizeNum];
    private boolean playerFired = false;

    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    protected Size                 SPECTRUM_SIZE;
    private int callTouchEvent = 0;

//    private Scalar               CONTOUR_COLOR;

    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ColorBlobDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.color_blob_detection_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
//        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }
    // this will be fired by a different event
    // instead of onTouch, it will be a menu item on the screen
    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();
        int x = cols/2;
        int y = rows/2;

        Point a = new Point();
        Point b = new Point();
        int M = RADIUS*2;
        //MxM rectangle, centered at the
        a.x = x - M/2;
        a.y = y - M/2;
        b.x = x + M/2;
        b.y = y + M/2;

        Rect centerRect = new Rect(a, b);

        Mat targetRegionRgba = mRgba.submat(centerRect);

        Mat targetRegionHsv = new Mat();
        // convert RGBA to HSV
        Imgproc.cvtColor(targetRegionRgba, targetRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(targetRegionHsv, mean, stddev);

        Log.i(TAG, "Mean: " + Arrays.toString(mean.toArray()));
        Log.i(TAG, "Std Dev: " + Arrays.toString(stddev.toArray()));
        double[] std = stddev.toArray();
        // convert HSV to RGBA representation
        mBlobColorRgba = converScalarHsv2Rgba(new Scalar(mean.toArray()));
        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

//        if (std[0] > 70) {
////        if (stddev.get(0, 0)[0] > 70 && stddev.get(1, 0)[0] > 15) {
//            if (callTouchEvent > 0) {
//                callTouchEvent--;
//            } else {
//                callTouchEvent = 10;
//            }
//            return false;
//        } else {
//            callTouchEvent = 0;
//        }
        this.finishUp(mBlobColorRgba, mean);

        mDetector.setHsvColor(mBlobColorHsv);
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        targetRegionRgba.release();
        targetRegionHsv.release();
        return false; // don't need subsequent touch events
    }

    public void finishUp(Scalar mBlobColorRgba, MatOfDouble mean) {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        this.drawReticle();
//        if (callTouchEvent > 0) {
//            this.onTouch(null, null);
//        }
        // update color labels
        Mat colorLabel = mRgba.submat(4, 68, 4, 68);
        colorLabel.setTo(mBlobColorRgba);
        return mRgba;
    }

    private void drawReticle() {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        Point center = new Point(cols/2, rows/2);
        Core.circle(mRgba, center, RADIUS, new Scalar(255,255,255,255), 2);
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);

        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB, 4);
        return new Scalar(pointMatRgba.get(0, 0));
    }

}