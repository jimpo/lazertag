package com.greylocku.lazer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;

/**
 * Created with IntelliJ IDEA.
 * User: zmichaelov
 * Date: 7/28/13
 * Time: 4:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class ColorPickerActivity extends ColorBlobDetectionActivity {

    @Override
    public void finishUp(Scalar mBlobColorRgba, MatOfDouble mean) {
        int daColor = Color.argb((int) mBlobColorRgba.val[3],
                (int) mBlobColorRgba.val[0],
                (int) mBlobColorRgba.val[1],
                (int) mBlobColorRgba.val[2]);

        Log.i(TAG, "dacolor: (" + daColor);

        // RETURN ALL DA SHIT
        Intent resultData = new Intent();
        resultData.putExtra("colorRGBA", daColor);
        resultData.putExtra("colorHSA", mean.toArray());
        setResult(Activity.RESULT_OK, resultData);
        finish();
    }
}
