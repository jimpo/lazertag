package com.greylocku.lazer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;

import android.os.Bundle;
import android.os.Handler;
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
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait);

		game_ = getGame();
		mHandler = new Handler();

		TextView game_input = (TextView)findViewById(R.id.game_id_field);
		game_input.setText(game_.getName());

		final ListView youList = (ListView)findViewById(R.id.you_list);
		final ListView playersList = (ListView)findViewById(R.id.others_list);
		
		player_ = LazerUser.find("objectId", getPlayerID());
		
		final PlayerArrayAdapter youAdapter = new PlayerArrayAdapter(this);
		youAdapter.add(player_);
		final PlayerArrayAdapter othersAdapter = new PlayerArrayAdapter(this);
		youList.setAdapter(youAdapter);
		playersList.setAdapter(othersAdapter);

		
		View startButton = findViewById(R.id.start_button);
		startButton.setVisibility(player_.isOwner() ? View.VISIBLE : View.GONE);
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				updateLists(othersAdapter);
			}
		}, 0, 5000);
	}
	
	private void updateLists(final PlayerArrayAdapter playersList) {
		mHandler.post(new Runnable() {

		@Override
		public void run() {
			List<LazerUser> players = game_.getPlayers();
			String playerID = getPlayerID();
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getObjectId().equals(playerID)) {
					players.remove(i);
					break;
				}
			}
		playersList.clear();
		playersList.addAll(players);
		}
		});
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
