package com.rest.client.ds;


import com.chopping.rest.RestObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class ClientEditRequest extends RestObject {

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
	protected RealmObject[] newInstances( Realm db, int status ) {
		RealmResults<ClientDB> dbItems = db.where( ClientDB.class )
										   .equalTo(
												   "reqId",
												   getReqId()
										   )
										   .findAll();
		ClientDB dbItem = dbItems.first();
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
	public RestObject newFromDB( RealmObject dbItem ) {
		ClientDB          clientDB          = (ClientDB) dbItem;
		ClientEditRequest clientEditRequest = new ClientEditRequest();
		clientEditRequest.setReqId( clientDB.getReqId() );
		clientEditRequest.setReqTime( clientDB.getReqTime() );
		clientEditRequest.setComment( clientDB.getComment() );
		return clientEditRequest;
	}

	public ClientEditRequest assignFromClient( Client client ) {
		setReqId( client.getReqId() );
		setReqTime( client.getReqTime() );
		setComment( client.getComment() );
		return this;
	}
}
