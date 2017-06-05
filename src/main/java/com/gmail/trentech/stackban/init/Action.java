package com.gmail.trentech.stackban.init;

public enum Action {
	PLACE("place"),
	BREAK("break"),
	USE("use"),
	CRAFT("craft"),
	MODIFY("modify"),
	PICKUP("pickup"),
	HOLD("hold"),
	DROP("drop");

	private String name;

	private Action(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
