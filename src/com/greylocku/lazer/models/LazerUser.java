package com.greylocku.lazer.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("LazerUser")
public class LazerUser extends ParseObject{
	
	public static LazerUser create(String name, LazerGame game){
		LazerUser generatedUser = new LazerUser();
		generatedUser.setName(name);
		generatedUser.setHealth("3");
		generatedUser.put("game", game);
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
	
	public String getHealth() {
		return getString("health");
	}
	  
	public void setHealth(String value) {
		put("health", value);
	}
	
	public String getColor() {
		return getString("color");
	}
	  
	public void setColor(String value) {
		put("color", value);
	}
	
	public void persistSynchronously() {
		try {
			save();
		} catch (com.parse.ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Goodluck Biatch");
		}
	}
}
