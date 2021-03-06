package com.greylocku.lazer.models;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("LazerUser")
public class LazerUser extends ParseObject{

	public static LazerUser create(String name, LazerGame game, Boolean isowner, int color){
		LazerUser generatedUser = new LazerUser();
		generatedUser.setName(name);
		generatedUser.setHealth(3);
		generatedUser.setOwner(isowner);
		generatedUser.put("game", game);
		generatedUser.setColor(color);
		generatedUser.persistSynchronously();
		return generatedUser;
	}
	/*
	 * Note: LazerUser cannot have a constructor 
	 * that makes the object "dirty" (ie saves something to DB)
	 */
	public String getName() {
		return getString("name");
	}

	public void setName(String value) {
		put("name", value);
	}

	public int getHealth() {
		return getInt("health");
	}

	public void setHealth(int value) {
		put("health", value);
	}

	public int getColor() {
		return getInt("color");
	}

	public void setColor(int color) {
		put("color", color);
	}
		
	public Boolean isOwner() {
        LazerUser usr = LazerUser.find("objectId", getObjectId());
		return usr.getBoolean("owner");
	}

	public void setOwner(Boolean value) {
		put("owner", value);
	}


	public void persistSynchronously() {
		try {
			save();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
	}

	public static ParseQuery<LazerUser> query() {
		return ParseQuery.getQuery("LazerUser");
	}
	
	public static LazerUser find(String column, String name) {
		ParseQuery<LazerUser> query = query().whereEqualTo(column, name);
		List<LazerUser> results;
		try {
			results = query.find();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.toString());
		}
		return results.size() > 0 ? results.get(0) : null;
	}
	
}
