package com.rest.client.rest;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public   class RestHistory extends RealmObject{
	@PrimaryKey
	private String name;
	private String json;
	private String type;


	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getJson() {
		return json;
	}

	public void setJson( String json ) {
		this.json = json;
	}


	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}
}
