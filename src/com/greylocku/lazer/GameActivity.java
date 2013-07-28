package com.greylocku.lazer;

import android.os.Bundle;
import android.util.Log;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zmichaelov
 * Date: 7/28/13
 * Time: 4:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameActivity extends ColorBlobDetectionActivity {

    private Map<String, double[]> colors;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.makeColors();
    }


    public void makeColors(){
        Map<String, double[]> colors = new HashMap<String, double[]>();
        colors.put("White", new double[]{0, 0, 255});
        colors.put("Black", new double[]{0, 255, 0});
        colors.put("Red", new double[]{0, 255, 255});
        colors.put("RedShirt", new double[]{7, 146, 239});
        colors.put("Blue", new double[]{240, 255, 255});
        colors.put("BlueShirt", new double[]{105, 78, 180});
        colors.put("Cyan", new double[]{180, 255, 255});
        colors.put("Yellow", new double[]{60, 255, 255});
        colors.put("Green", new double[]{120 , 255, 255});
        colors.put("TreeGreen", new double[]{50 , 171, 77});
    }

    @Override
    public void finishUp(Scalar mBlobColorRgba, MatOfDouble mean){
        this.activateHit(mean.toArray());
    }

    public void activateHit(double[] meanHSV) {

        double[] weight = {4, .1, .2};

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

}
