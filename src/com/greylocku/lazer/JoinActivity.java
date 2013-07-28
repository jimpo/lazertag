package com.greylocku.lazer;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;


import com.greylocku.lazer.models.*;

import android.view.View;
import android.graphics.PorterDuff;

public class JoinActivity extends Activity {
	public static final String NEW_GAME_FIELD = "com.greylocku.lazertag.NEW_GAME_FIELD";
	private static final int TAKE_PICTURE_ACTION = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		View game_input = findViewById(R.id.game_id_input);
		game_input.setVisibility(isNewGame() ? View.GONE : View.VISIBLE);
	}

	private boolean isNewGame() {
		Intent intent = getIntent();
		return intent.getBooleanExtra(NEW_GAME_FIELD, false);
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join, menu);
		return true;
	}

	public void joinGame(View view) {
		LazerGame game;
		if (isNewGame()) {

		}
		else {

		}
		Intent intent = new Intent(this, WaitActivity.class);
		startActivity(intent);
	}

	public void takePicture(View view) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(takePictureIntent, TAKE_PICTURE_ACTION);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE_ACTION:
			handlePictureTaken(data);
			break;
		}
	}

	private void handlePictureTaken(Intent data) {
		View button = findViewById(R.id.picture_button);
		button.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
	}
 }
