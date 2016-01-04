package com.rest.client.ds;


import com.chopping.rest.RestObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

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
		ClientDB dbItem;
		switch( status ) {
			case DELETE_SYNCED:
				RealmResults<ClientDB> dbItems = db.where( ClientDB.class )
												   .equalTo(
														   "reqId",
														   getReqId()
												   )
												   .findAll();
				dbItem = dbItems.first();
				break;
			default:
				dbItem = new ClientDB();
				dbItem.setReqId( getReqId() );
				dbItem.setReqTime( getReqTime() );
				dbItem.setComment( getComment() );
				dbItem.setStatus( status );
				break;
		}
		return new RealmObject[] { dbItem };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return ClientDB.class;
	}

	@Override
	public RestObject newFromDB( RealmObject dbItem ) {
		ClientDB clientDB = (ClientDB) dbItem;
		Client   client   = new Client();
		client.setReqId( clientDB.getReqId() );
		client.setReqTime( clientDB.getReqTime() );
		client.setComment( clientDB.getComment() );
		return client;
	}
}
