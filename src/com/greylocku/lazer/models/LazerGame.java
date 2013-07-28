package com.greylocku.lazer.models;

import java.util.List;
import java.util.Random;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("LazerGame")
public class LazerGame extends ParseObject{
	private static int ID_MAX = 100000;
	private static int STATUS_READY = 1;
	private static int STATUS_STARTED = 2;
	private static int STATUS_OVER = 3;


	public static LazerGame create(){
		LazerGame generatedGame = new LazerGame();
		String inMemoryName = randomNumericalString();
		generatedGame.setName(inMemoryName);
		generatedGame.put("status", STATUS_READY);
		generatedGame.persistSynchronously();
		return generatedGame;
	}

	private static String randomNumericalString() {
	    Random randomGenerator = new Random();
	    return "" + randomGenerator.nextInt(ID_MAX);
	}

	public void start() {
        put("status", STATUS_STARTED);
		persistSynchronously();
	}
	
	public void end() {
        put("status", STATUS_OVER);
		persistSynchronously();
	}
	
	public boolean isStarted() {
		LazerGame game = LazerGame.find("objectId", getObjectId());
		return game.getInt("status") == STATUS_STARTED;
	}
	
	public String getName() {
		return getString("name");
	}

	public void setName(String value) {
		put("name", value);
	}

	public List<LazerUser> getPlayersSync() {
		ParseQuery<LazerUser> query = LazerUser.query().whereEqualTo("game", this);
		try {
			return query.find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void getPlayers(FindCallback<LazerUser> callback) {
		ParseQuery<LazerUser> query = LazerUser.query().whereEqualTo("game", this);
		query.findInBackground(callback);
	}

	public void persistSynchronously() {
		try {
			save();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
	}

	public static ParseQuery<LazerGame> query() {
		return ParseQuery.getQuery("LazerGame");
	}

	public static LazerGame find(String column, String name) {
		ParseQuery<LazerGame> query = query().whereEqualTo(column, name);
		List<LazerGame> results;
		try {
			results = query.find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
		return results.size() > 0 ? results.get(0) : null;
	}
	
	public void registerHit(final int color) {
		getPlayers(new FindCallback<LazerUser>() {

			@Override
			public void done(List<LazerUser> players, ParseException e) {
				for (LazerUser usr: players){
					if(usr.getColor() == color){
						int h = usr.getHealth() - 1;
						usr.setHealth(h);
                        usr.persistSynchronously();
						break;
					}
				}
			}
		});
	}
}
