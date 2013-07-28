package com.greylocku.lazer;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class JoinActivity extends Activity {
	public static final String NEW_GAME_FIELD = "com.greylocku.lazertag.NEW_GAME_FIELD";
	private static final int TAKE_PICTURE_ACTION = 0;
	
	private int mColor;

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
			game = LazerGame.create();
		}
		else {
			EditText input = (EditText)findViewById(R.id.game_id_input);
			game = LazerGame.find("name", input.getText().toString());
		}
		EditText nameInput = (EditText)findViewById(R.id.player_input);
		LazerUser user = LazerUser.create(nameInput.getText().toString(), game, isNewGame(), mColor);

		Intent intent = new Intent(this, WaitActivity.class);
		intent.putExtra(WaitActivity.GAME_ID_FIELD, game.getObjectId());
		intent.putExtra(WaitActivity.PLAYER_ID_FIELD, user.getObjectId());
		startActivity(intent);
	}

	public void showMessage(String message) {
		Toast.makeText(this,  message,  Toast.LENGTH_SHORT).show();
	}

	public void takePicture(View view) {
	    Intent takePictureIntent = new Intent(this, ColorPickerActivity.class);
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
		mColor = data.getExtras().getInt("colorRGBA");
		View button = findViewById(R.id.picture_button);
		button.setBackgroundColor(mColor);
	}
 }
