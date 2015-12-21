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

	public void setReqId( String reqId ) {
		mReqId = reqId;
	}

	public void setReqTime( long reqTime ) {
		mReqTime = reqTime;
	}

	public String getReqId() {
		return mReqId;
	}

	public long getReqTime() {
		return mReqTime;
	}


	@Override
	public RealmObject updateDB(int status) {
		Realm db = Realm.getDefaultInstance();
		db.beginTransaction();
		RequestForResponseDB dbItem = new RequestForResponseDB();
		dbItem.setReqId( getReqId() );
		dbItem.setReqTime( getReqTime() );
		dbItem.setStatus(status);
		db.copyToRealmOrUpdate( dbItem );
		db.commitTransaction();
		return dbItem;
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return RequestForResponseDB.class;
	}
}
