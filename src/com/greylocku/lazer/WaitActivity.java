package com.greylocku.lazer;

import java.util.List;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;
import com.parse.ParseException;
import com.parse.ParseQuery;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class WaitActivity extends Activity {
	public static final String GAME_ID_FIELD = "com.greylocku.lazertag.GAME_ID_FIELD";
	public static final String PLAYER_ID_FIELD = "com.greylocku.lazertag.PLAYER_ID_FIELD";

	private LazerGame game_;
	private LazerUser player_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait);

		game_ = getGame();

		TextView game_input = (TextView)findViewById(R.id.game_id_field);
		game_input.setText(game_.getName());

		List<LazerUser> players = game_.getPlayers();
		String playerID = getPlayerID();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getObjectId().equals(playerID)) {
				player_ = players.remove(i);
				break;
			}
		}
		
		ListView youList = (ListView)findViewById(R.id.you_list);
		ListView playersList = (ListView)findViewById(R.id.others_list);
		PlayerArrayAdapter youAdapter = new PlayerArrayAdapter(this, new LazerUser[] { player_ });
		PlayerArrayAdapter othersAdapter = new PlayerArrayAdapter(this, players.toArray(new LazerUser[0]));
		youList.setAdapter(youAdapter);
		playersList.setAdapter(othersAdapter);
	}

	private LazerGame getGame() {
		Intent intent = getIntent();
		String gameID = intent.getStringExtra(GAME_ID_FIELD);
		return LazerGame.find("objectId", gameID);
    }
	
	private String getPlayerID() {
		Intent intent = getIntent();
		return intent.getStringExtra(PLAYER_ID_FIELD);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wait, menu);
		return true;
	}

}
