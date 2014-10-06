package com.sourcefuse.clickinandroid.model.bean;



public class Contacts {
	private String name;
	private String pic;

	public Contacts(String name, String pic) {
		this.name = name;
		this.pic = pic;
	}

	public String getName() {
		return this.name;
	}

	public String getPic() {
		return this.pic;
	}

	
}