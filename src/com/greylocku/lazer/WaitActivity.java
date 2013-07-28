package com.greylocku.lazer;

import com.greylocku.lazer.models.LazerGame;
import com.greylocku.lazer.models.LazerUser;
import com.parse.ParseException;
import com.parse.ParseQuery;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class WaitActivity extends Activity {
	public static final String GAME_ID_FIELD = "com.greylocku.lazertag.GAME_ID_FIELD";
	public static final String PLAYER_ID_FIELD = "com.greylocku.lazertag.PLAYER_ID_FIELD";

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
        /*
		LazerUser[] players;
		ParseQuery<LazerUser> query = LazerUser.query().whereEqualTo("name", "Jim");
		try {
			players = query.find().toArray(new LazerUser[0]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
        */
		PlayerArrayAdapter adapter = new PlayerArrayAdapter(this, players);
		players_list.setAdapter(adapter);
	}

	private LazerGame getGame() {
		Intent intent = getIntent();
		String gameID = intent.getStringExtra(GAME_ID_FIELD);
		return LazerGame.find("objectId", gameID);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wait, menu);
		return true;
	}

}
