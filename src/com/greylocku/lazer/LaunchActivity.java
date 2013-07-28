package com.greylocku.lazer;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
        initializeParse();
	}

    public void newGame(View view) {
        joinGame(view, true);
    }

    public void joinGame(View view) {
        joinGame(view, false);
    }

    public void joinGame(View view, boolean newGame) {
        Intent intent = new Intent(this, JoinActivity.class);
        intent.putExtra(JoinActivity.NEW_GAME_FIELD, newGame);
        startActivity(intent);
    }

    public void initializeParse() {
        //Initialize parse
        // Add your initialization code here
        ParseObject.registerSubclass(LazerUser.class);
        ParseObject.registerSubclass(LazerGame.class);
        Parse.initialize(this, "ZAxTBMrNWKxLc1vQT6s1RJWRK2ytQoY4TGCNmdXV", "nmYDNx1IghjEjPyXgTJyfSUiOxIyeEEGjqbMX2U7");

        ParseACL defaultACL = new ParseACL();
        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
