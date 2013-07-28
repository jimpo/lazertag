package com.greylocku.lazer.models;

import java.util.List;
import java.util.Random;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("LazerGame")
public class LazerGame extends ParseObject{
	private static String GAME_NAME = "lazertag";
	private static int ID_MAX = 100000;
	
	public static LazerGame create(){
		LazerGame generatedGame = new LazerGame();
		String inMemoryName = GAME_NAME + "_" + randomNumericalString();
		generatedGame.setName(inMemoryName);
		generatedGame.persistSynchronously();
		return generatedGame;
	}
	
	private static String randomNumericalString() {
	    Random randomGenerator = new Random();
	    return "" + randomGenerator.nextInt(ID_MAX);
	}
	
	public String getName() {
		return getString("name");
	}
	  
	public void setName(String value) {
		put("name", value);
	}
	
	public List<LazerUser> getPlayers() {
		ParseQuery<LazerUser> query = ParseQuery.getQuery("LazerUser");
		query.whereEqualTo("game", this);
		try {
			return query.find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void persistSynchronously() {
		try {
			save();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
	}
	
	public static LazerGame find(String column, String name){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("LazerGame");
		List<ParseObject> results;
		query.whereEqualTo(column, name);
		try {
			results = query.find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
		if (results.size() == 0){
			return null;
		}
		else {
			return (LazerGame) results.get(0);	
		}
	}
	
	
}
