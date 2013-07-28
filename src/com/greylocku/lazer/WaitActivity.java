package com.greylocku.lazer;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class WaitActivity extends Activity {
	public static final String GAME_ID_FIELD = "com.greylocku.lazertag.GAME_NAME_FIELD";
	
	private LazerGame game_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait);
		
		game_ = getGame();
		
		TextView game_input = (TextView)findViewById(R.id.game_id_field);
		game_input.setText(game_.getName());
		
		ListView players_list = (ListView)findViewById(R.id.others_list);
		LazerUser[] players = game_.getPlayers().toArray(new LazerUser[0]);
		PlayerArrayAdapter adapter = new PlayerArrayAdapter(this, players);
		players_list.setAdapter(adapter);
	}
	
	private LazerGame getGame() {
		Intent intent = getIntent();
		String name = intent.getStringExtra(GAME_ID_FIELD);
		return LazerGame.find(name);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wait, menu);
		return true;
	}

}
