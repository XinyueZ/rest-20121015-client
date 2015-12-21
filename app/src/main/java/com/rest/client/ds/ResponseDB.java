package com.rest.client.ds;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ResponseDB extends RealmObject {
	@PrimaryKey
	private String              reqId;
	private int                 responseStatus;
	private int                 status;
	private RealmList<ClientDB> result;

	public String getReqId() {
		return reqId;
	}

	public void setReqId( String reqId ) {
		this.reqId = reqId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus( int status ) {
		this.status = status;
	}

	public RealmList<ClientDB> getResult() {
		return result;
	}

	public void setResult( RealmList<ClientDB> result ) {
		this.result = result;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus( int responseStatus ) {
		this.responseStatus = responseStatus;
	}
}
