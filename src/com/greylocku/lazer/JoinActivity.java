package com.greylocku.lazer;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class JoinActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		
		//Initialize parse
		// Add your initialization code here
		Parse.initialize(this, "ZAxTBMrNWKxLc1vQT6s1RJWRK2ytQoY4TGCNmdXV", "nmYDNx1IghjEjPyXgTJyfSUiOxIyeEEGjqbMX2U7"); 

		ParseACL defaultACL = new ParseACL();	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		
		//demo saving
		ParseObject gameScore = new ParseObject("GameScore");
		gameScore.put("score", 1337);
		gameScore.put("playerName", "Sean Plott");
		gameScore.put("cheatMode", false);
		gameScore.saveInBackground();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join, menu);
		return true;
	}

}
