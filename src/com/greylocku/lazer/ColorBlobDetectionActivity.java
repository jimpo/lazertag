package com.greylocku.lazer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

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
import java.util.HashMap;
import java.util.Map;

public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";
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
    private Size                 SPECTRUM_SIZE;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.color_blob_detection_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        Log.i(TAG, "Has Flash: " + getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH));
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


        if (std[0] > 70) {
//        if (stddev.get(0, 0)[0] > 70 && stddev.get(1, 0)[0] > 15) {
            if (callTouchEvent > 0) {
                callTouchEvent--;
            } else {
                callTouchEvent = 10;
            }
            return false;
        } else {
            callTouchEvent = 0;
        }

        this.activateHit(mean.toArray());
        // convert HSV to RBGA representation
        mBlobColorRgba = converScalarHsv2Rgba(new Scalar(mean.toArray()));

        int daColor = Color.argb((int)mBlobColorRgba.val[3],
        						 (int)mBlobColorRgba.val[0],
        						 (int)mBlobColorRgba.val[1],
        						 (int)mBlobColorRgba.val[2]);

        Log.i(TAG, "dacolor: (" + daColor);

        // RETURN ALL DA SHIT
        Intent resultData = new Intent();
        resultData.putExtra("colorRGBA", daColor);
        resultData.putExtra("colorHSA", mean.toArray());
        setResult(Activity.RESULT_OK, resultData);
        finish();


        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        playerFired = true;

        targetRegionRgba.release();
        targetRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    public void activateHit(double[] meanHSV) {
        Map<String, double[]> colors = new HashMap<String, double[]>();
        colors.put("White", new double[]{0, 0, 255});
        colors.put("Black", new double[]{0, 255, 0});
        colors.put("Red", new double[]{0, 255, 255});
        colors.put("RedShirt", new double[]{7, 146, 239});
        colors.put("Blue", new double[]{240, 255, 255});
        colors.put("Cyan", new double[]{180, 255, 255});
        colors.put("Yellow", new double[]{60, 255, 255});
        colors.put("Green", new double[]{120 , 255, 255});
        colors.put("TreeGreen", new double[]{50 , 171, 77});

        double[] weight = {1.5, .1, .2};

        for (String color : colors.keySet()) {
            double[] hsv = colors.get(color);
            double sum1 = Math.abs(hsv[0] - meanHSV[0]);
            double minDist = Math.min(sum1, 360 - sum1);
            double sum = Math.pow(minDist,2)*weight[0];
            for (int i = 1; i < meanHSV.length; i++) {

                sum += Math.pow(meanHSV[i] - hsv[i], 2) * weight[i];
            }
            double dist = Math.sqrt(sum);
            Log.i(TAG, "Color Distance" + color + ": " + dist);
        }

    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        this.drawReticle();
        if (callTouchEvent > 0) {
            this.onTouch(null, null);
        }
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

        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);
        return new Scalar(pointMatRgba.get(0, 0));
    }

}