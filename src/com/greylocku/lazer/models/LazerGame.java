package com.greylocku.lazer.models;

import java.util.List;
import java.util.Random;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

@ParseClassName("LazerGame")
public class LazerGame extends ParseObject{
	private static String GAME_NAME = "lazertag";
	private static int ID_MAX = 100000;
	private static String RELATIONAL_FIELD = "players";

	
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
	
	public void addPlayer(LazerUser user) {
		getRelation(RELATIONAL_FIELD).add(user);
	}
	
	public List<LazerUser> getPlayers() {
		try {
			ParseRelation<LazerUser> players = getRelation(RELATIONAL_FIELD);
			return players.getQuery().find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
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
	
	public static ParseQuery<LazerGame> query() {
		return ParseQuery.getQuery("LazerGame");
	}
	
	public static LazerGame findByGameName(String name){
		List<LazerGame> results;
		try {
			results = query().whereEqualTo("name", name).find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
		if (results.size() == 0){
			return null;
		}
		else {
			return results.get(0);	
		}
	}
	
	public static LazerGame find(String id){
		try {
			return query().get(id);
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
	}
}
