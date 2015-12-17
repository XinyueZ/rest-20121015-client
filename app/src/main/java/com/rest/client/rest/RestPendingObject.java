package com.rest.client.rest;


import io.realm.RealmObject;

public   class RestPendingObject extends RealmObject {
	private String reqId;
	private long   reqTime;

	public String getReqId() {
		return reqId;
	}

	public void setReqId( String reqId ) {
		this.reqId = reqId;
	}

	public long getReqTime() {
		return reqTime;
	}

	public void setReqTime( long reqTime ) {
		this.reqTime = reqTime;
	}
}
