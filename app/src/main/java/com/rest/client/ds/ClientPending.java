package com.rest.client.ds;


import io.realm.RealmObject;

public   class ClientPending extends RealmObject {
	private String reqId;
	private long   reqTime;
	private String comment;


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

	public String getComment() {
		return comment;
	}

	public void setComment( String comment ) {
		this.comment = comment;
	}


}
