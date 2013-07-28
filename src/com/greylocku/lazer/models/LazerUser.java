package com.greylocku.lazer.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("LazerUser")
public class LazerUser extends ParseObject{
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
	
}
