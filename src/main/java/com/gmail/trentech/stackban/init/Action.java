package com.gmail.trentech.stackban.utils;

public enum Action {
	PLACE("place", "%PLAYER% attempted to place banned item: %ITEM%"),
	BREAK("break", "%PLAYER% attempted to break banned item: %ITEM%"),
	USE("use", "%PLAYER% attempted to use banned item: %ITEM%"),
	CRAFT("craft", "%PLAYER% attempted to craft banned item: %ITEM%"),
	MODIFY("modify", "%PLAYER% attempted to modify banned item: %ITEM%"),
	PICKUP("pickup", "%PLAYER% attempted to pickup banned item: %ITEM%"),
	HOLD("hold", "%PLAYER% attempted to hold banned item: %ITEM%"),
	DROP("drop", "%PLAYER% attempted to drop banned item: %ITEM%");

	private String name;
	private String message;
	
	private Action(String name, String message) {
		this.name = name;
		this.message = message;
	}

	public String getName() {
		return name;
	}
	
	public String getMessage() {
		return message;
	}
}
