package com.rest.client.rest;


import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public   class RestPendingObject extends RealmObject {
	private String reqId;
	private long   reqTime;
	private int status;

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

	public int getStatus() {
		return status;
	}

	public void setStatus( int status ) {
		this.status = status;
	}
}

