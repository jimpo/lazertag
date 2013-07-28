package com.greylocku.lazer;

import android.graphics.Color;
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

    private Map<String, Integer> colors;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.makeColors();
    }


    public void makeColors(){
        Map<String, Integer> colors = new HashMap<String, Integer>();
        colors.put("White", -262);
        colors.put("Black", -8488597);
        colors.put("RedShirt", -21606);
        colors.put("BlueShirt", -10191974);
        colors.put("TreeGreen", -12884933);
    }

    @Override
    public void finishUp(Scalar mBlobColorRgba, MatOfDouble mean){
        this.activateHit(mean.toArray());
    }

    public void activateHit(double[] meanHSV) {

        double[] weight = {4, .1, .2};

        for (String color : colors.keySet()) {
            float[] hsv = new float[3];
            Color.colorToHSV(colors.get(color), hsv);
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
