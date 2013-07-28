package com.greylocku.lazer.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("LazerGame")
public class LazerGame extends ParseObject{
	public String getName() {
		return getString("name");
	}
	  
	public void setName(String value) {
		put("name", value);
	}
	
}
