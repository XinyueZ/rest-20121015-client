package com.rest.client.ds;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.rest.client.rest.RestObject;

import io.realm.Realm;
import io.realm.RealmObject;

public class RequestForResponse extends RestObject {

	@SerializedName("reqId")
	@JsonProperty("reqId")
	private String mReqId;
	@SerializedName("reqTime")
	@JsonProperty("reqTime")
	private long   mReqTime;

	public String getReqId() {
		return mReqId;
	}

	public void setReqId( String reqId ) {
		mReqId = reqId;
	}

	public long getReqTime() {
		return mReqTime;
	}

	public void setReqTime( long reqTime ) {
		mReqTime = reqTime;
	}

	@Override
	public RealmObject[] newInstances( Realm db, int status  ) {
		RequestForResponseDB dbItem = new RequestForResponseDB();
		dbItem.setReqId( getReqId() );
		dbItem.setReqTime( getReqTime() );
		dbItem.setStatus( status );
		return new RealmObject[] { dbItem };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return RequestForResponseDB.class;
	}
}
