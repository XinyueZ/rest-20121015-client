package com.rest.client.ds;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.chopping.rest.RestObject;

import io.realm.Realm;
import io.realm.RealmObject;

public class Client extends RestObject {

	@SerializedName("reqId")
	@JsonProperty("reqId")
	private String mReqId;
	@SerializedName("reqTime")
	@JsonProperty("reqTime")
	private long   mReqTime;
	@SerializedName("comment")
	@JsonProperty("comment")
	private String mComment;

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

	public String getComment() {
		return mComment;
	}

	public void setComment( String comment ) {
		mComment = comment;
	}

	@Override
	public RealmObject[] newInstances( Realm db, int status ) {
		ClientDB dbItem = new ClientDB();
		dbItem.setReqId( getReqId() );
		dbItem.setReqTime( getReqTime() );
		dbItem.setComment( getComment() );
		dbItem.setStatus( status );
		return new RealmObject[] { dbItem };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return ClientDB.class;
	}

	@Override
	public RestObject fromDB( RealmObject dbItem ) {
		ClientDB clientDB = (ClientDB) dbItem;
		Client   client   = new Client();
		client.setReqId( clientDB.getReqId() );
		client.setReqTime( clientDB.getReqTime() );
		client.setComment( clientDB.getComment() );
		return client;
	}
}
