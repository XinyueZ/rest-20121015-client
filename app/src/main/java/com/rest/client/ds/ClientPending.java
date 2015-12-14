package com.rest.client.ds;


import io.realm.RealmObject;

public   class ClientPending extends RealmObject {

	private String reqId;
	private long   reqTime;
	private String comment;



	public ClientPending() {
	}

	public ClientPending( String reqId, long reqTime, String comment ) {
		this.reqId = reqId;
		this.reqTime = reqTime;
		this.comment = comment;
	}

	public String getReqId() {
		return reqId;
	}

	public long getReqTime() {
		return reqTime;
	}

	public String getComment() {
		return comment;
	}


	public void setReqId( String reqId ) {
		this.reqId = reqId;
	}

	public void setReqTime( long reqTime ) {
		this.reqTime = reqTime;
	}

	public void setComment( String comment ) {
		this.comment = comment;
	}
}
