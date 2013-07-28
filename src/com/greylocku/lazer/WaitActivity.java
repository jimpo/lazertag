package com.greylocku.lazer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
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

		final ListView youList = (ListView)findViewById(R.id.you_list);
		final ListView playersList = (ListView)findViewById(R.id.others_list);
		
		View startButton = findViewById(R.id.start_button);
		startButton.setVisibility(player_.isOwner() ? View.VISIBLE : View.GONE);
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				updateLists(youList, playersList);
			}
		}, 0, 5000);
	}
	
	private void updateLists(ListView youList, ListView playersList) {
		List<LazerUser> players = game_.getPlayers();
		String playerID = getPlayerID();
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getObjectId().equals(playerID)) {
				player_ = players.remove(i);
				break;
			}
		}
		
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
