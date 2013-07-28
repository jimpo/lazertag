package com.greylocku.lazer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.greylocku.lazer.util.SoundEffects;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created with IntelliJ IDEA.
 * User: zmichaelov
 * Date: 7/28/13
 * Time: 4:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameActivity extends ColorBlobDetectionActivity {
	public static final String GAME_ID_FIELD = "com.greylocku.lazertag.GAME_ID_FIELD";
	public static final String PLAYER_ID_FIELD = "com.greylocku.lazertag.PLAYER_ID_FIELD";

    private List<Integer> colors;
    private LazerGame mGame;
    private LazerUser mPlayer;
    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTask;
    private int timeoutOut;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame = getGame();
        mPlayer = getPlayer();
        mHandler = new Handler();
        this.makeColors();
        timeoutOut = 0;
		mTimer = new Timer();
		mTask = new TimerTask() {
			public void run() {
				updateStats();
			}
		};
	}
	
	@Override
	public void onResume() {
		mTimer.scheduleAtFixedRate(mTask, 0, 2000);
		super.onResume();
	}
	
	public void onPause() {
		mTimer.cancel();
		super.onPause();
	}
	
	private boolean canShoot() {
		return timeoutOut <= 0;
	}
    
    private void updateStatsInUIThread() {
    	mHandler.post(new Runnable() {
			@Override
			public void run() {
				
			}
    	});
    }
    
    private void updateStats() {
    	mPlayer.fetchInBackground(new GetCallback<LazerUser>() {
            @Override
            public void done(LazerUser newPlayer, ParseException e) {
                LazerUser oldPlayer = mPlayer;
                mPlayer = newPlayer;
                if (!canShoot()) {
                    timeoutOut--;
                    return;
                }
                if (mPlayer.getHealth() < oldPlayer.getHealth()) {
                    getShot();
                }
                if (mPlayer.getHealth() <= 0) {
                    endGame();
                    return;
                }
            }
        });
    }
    
    private void endGame() {
    	finish();
    }
    
    private void getShot() {
    	timeoutOut = 6;
    	//SoundEffects.play(this, R.raw.hit);
    }
    
	private LazerGame getGame() {
		Intent intent = getIntent();
		String gameID = intent.getStringExtra(GAME_ID_FIELD);
		return LazerGame.find("objectId", gameID);
    }

	private LazerUser getPlayer() {
		Intent intent = getIntent();
		String playerID = intent.getStringExtra(PLAYER_ID_FIELD);
		return LazerUser.find("objectId", playerID);
    }
	
    public void makeColors(){
        colors = new ArrayList<Integer>();
    	for (LazerUser player : mGame.getPlayersSync()) {
    		colors.add(player.getColor());
    	}
    }

    @Override
    public void finishUp(Scalar mBlobColorRgba, MatOfDouble mean){
    	Log.d("FINISHUP", "SHOOTING");
    	if (canShoot()) {
    		Log.d("FINISHUP", "SHOT");
    		this.activateHit(mBlobColorRgba.val);
    	}
    }

    public void activateHit(double[] rgba) {
        SoundEffects.play(this, R.raw.shot);
        Integer bestColor = null;
        float minDiff = 9999999;
        for (Integer color : colors) {
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            float[] shirt = new float[3];
            rgb2lab(r, g, b, shirt);

            float[] target = new float[3];
            rgb2lab((int)rgba[0], (int)rgba[1], (int)rgba[2], target);

            ;

//            Log.i(TAG, "HSV" + color + ": " + colorDistance(shirt, target));
//            double sum1 = Math.abs(hsv[0] - meanHSV[0]);
//            double minDist = Math.min(sum1, 360 - sum1);
//            double sum = Math.pow(minDist,2)*weight[0];
//            for (int i = 1; i < meanHSV.length; i++) {
//
//                sum += Math.pow(meanHSV[i] - hsv[i], 2) * weight[i];
//            }
//            double dist = Math.sqrt(sum);
            float cd = colorDistance(shirt, target);
            Log.i(TAG, "Color Distance" + color + ": " + cd);
            if (cd < 18 && cd < minDiff) {
                minDiff = cd;
                bestColor = color;
            }
            if (bestColor != null) {
                Log.i(TAG, "Hit" + color + ": " + cd);
                SoundEffects.play(this, R.raw.hit);
                mGame.registerHit(bestColor);
            }
        }

    }

    /**
     * Compute the distance E (CIE 1994) between two colors in LAB color space.
     * Reference: http://www.brucelindbloom.com/index.html?ColorDifferenceCalc.html
     */
    private static float colorDistance(final float[] lab1, final float[] lab2) {
        if (false) {
            // Compute distance using CIE94 formula.
            // NOTE: this formula sometime fails because of negative
            //       value in the first Math.sqrt(...) expression.
            final double dL = (double)lab1[0] - lab2[0];
            final double da = (double)lab1[1] - lab2[1];
            final double db = (double)lab1[2] - lab2[2];
            final double C1 = Math.hypot(lab1[1], lab1[2]);
            final double C2 = Math.hypot(lab2[1], lab2[2]);
            final double dC = C1 - C2;
            final double dH = Math.sqrt(da*da + db*db - dC*dC);
            final double sL = dL / 2;
            final double sC = dC / (1 + 0.048*C1);
            final double sH = dH / (1 + 0.014*C1);
            return (float)Math.sqrt(sL*sL + sC*sC + sH*sH);
        } else {
            // Compute distance using delta E formula.
            double sum = 0;
            for (int i=Math.min(lab1.length, lab2.length); --i>=0;) {
                final double delta = lab1[i] - lab2[i];
                sum += delta*delta;
            }
            return (float)Math.sqrt(sum);
        }
    }

    public void rgb2lab(int R, int G, int B, float[] lab) {
        //http://www.brucelindbloom.com

        float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
        float Ls, as, bs;
        float eps = 216.f/24389.f;
        float k = 24389.f/27.f;

        float Xr = 0.964221f;  // reference white D50
        float Yr = 1.0f;
        float Zr = 0.825211f;

        // RGB to XYZ
        r = R/255.f; //R 0..1
        g = G/255.f; //G 0..1
        b = B/255.f; //B 0..1

        // assuming sRGB (D65)
        if (r <= 0.04045)
            r = r/12;
        else
            r = (float) Math.pow((r+0.055)/1.055,2.4);

        if (g <= 0.04045)
            g = g/12;
        else
            g = (float) Math.pow((g+0.055)/1.055,2.4);

        if (b <= 0.04045)
            b = b/12;
        else
            b = (float) Math.pow((b+0.055)/1.055,2.4);


        X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
        Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
        Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;

        // XYZ to Lab
        xr = X/Xr;
        yr = Y/Yr;
        zr = Z/Zr;

        if ( xr > eps )
            fx =  (float) Math.pow(xr, 1/3.);
        else
            fx = (float) ((k * xr + 16.) / 116.);

        if ( yr > eps )
            fy =  (float) Math.pow(yr, 1/3.);
        else
            fy = (float) ((k * yr + 16.) / 116.);

        if ( zr > eps )
            fz =  (float) Math.pow(zr, 1/3.);
        else
            fz = (float) ((k * zr + 16.) / 116);

        Ls = ( 116 * fy ) - 16;
        as = 500*(fx-fy);
        bs = 200*(fy-fz);

        lab[0] = (float) (2.55*Ls + .5);
        lab[1] = (float) (as + .5);
        lab[2] = (float) (bs + .5);
    }



}
