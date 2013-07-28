package com.greylocku.lazer.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundEffects {
	public static void play(Context context, int RclipId) {
    	SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
    	final int soundID = soundPool.load(context, RclipId, 1);
    	soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                int status) {
            	soundPool.play(soundID, 1f, 1f, 1, 0, 1f);
            }
          });
	}
}
