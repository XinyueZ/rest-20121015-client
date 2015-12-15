package com.rest.client.rest;


import io.realm.RealmObject;

public   class RestPendingObject extends RealmObject{
	private String reqId;

	public String getReqId() {
		return reqId;
	}

	public void setReqId( String reqId ) {
		this.reqId = reqId;
	}
}
