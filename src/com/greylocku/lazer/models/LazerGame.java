package com.greylocku.lazer.models;

import java.util.List;
import java.util.Random;

import android.net.ParseException;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

@ParseClassName("LazerGame")
public class LazerGame extends ParseObject{
	private static String GAME_NAME = "lazertag";
	private static int ID_LENGTH = 5;
	private static String RELATIONAL_FIELD = "players";
	
	private ParseRelation<LazerUser> playerRelation = getRelation(RELATIONAL_FIELD);

	
	public static LazerGame create(){
		LazerGame generatedGame = new LazerGame();
		String inMemoryName = GAME_NAME + "_" + randomNumericalString(ID_LENGTH);
		generatedGame.setName(inMemoryName);
		generatedGame.persistSynchronously();
		return generatedGame;
	}
	
	private static String randomNumericalString(int x) {
	    Random randomGenerator = new Random();
	    String randString = "";
	    for (int idx = 1; idx <= x; ++idx){
	      int randomInt = randomGenerator.nextInt(100);
	      randString = randString + randomInt;
	    }
	    return randString;
	}
	
	public String getName() {
		return getString("name");
	}
	  
	public void setName(String value) {
		put("name", value);
	}
	
	public void addPlayer(LazerUser user) {
		playerRelation.add(user);
	}
	
	public List<LazerUser> getPlayers() {
		try {
			return playerRelation.getQuery().find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Goodluck Biatch");
		}
	}
	
	public void persistSynchronously() {
		try {
			save();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Goodluck Biatch");
		}
	}
	
	public static LazerGame findByGameName(String name){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("LazerGame");
		List<ParseObject> results;
		query.whereEqualTo("name", name);
		try {
			results = query.find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Goodluck Biatch");
		}
		if (results.size() == 0){
			return null;
		}
		else {
			return (LazerGame) results.get(0);	
		}
	}
	
	
}
