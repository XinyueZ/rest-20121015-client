package com.rest.client.ds;


import com.chopping.rest.RestObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class EditClientRequest extends RestObject {

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
		RealmResults<ClientDB> dbItems = db.where( ClientDB.class )
										   .equalTo(
												   "reqId",
												   getReqId()
										   )
										   .findAll();
		ClientDB dbItem = dbItems.first();
		dbItem.setReqId( getReqId() );
		if(status == RestObject.UPDATE_SYNCED) {
			dbItem.setReqTime( getReqTime() );
			dbItem.setComment( getComment() );
		}
		dbItem.setStatus( status );

		EditClientRequestDB editedItemDB  = new EditClientRequestDB();
		editedItemDB.setReqId( getReqId() );
		editedItemDB.setReqTime( getReqTime() );
		editedItemDB.setComment( getComment() );
		editedItemDB.setStatus( status );
		return new RealmObject[] { editedItemDB , dbItem};
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return EditClientRequestDB.class;
	}

	@Override
	public RestObject fromDB( RealmObject dbItem ) {
		EditClientRequestDB          clientDB = (EditClientRequestDB) dbItem;
		EditClientRequest client   = new EditClientRequest();
		client.setReqId( clientDB.getReqId() );
		client.setReqTime( clientDB.getReqTime() );
		client.setComment( clientDB.getComment() );
		return client;
	}

	public EditClientRequest fromClient(Client client) {
		setReqId( client.getReqId() );
		setReqTime( client.getReqTime() );
		setComment( client.getComment() );
		return this;
	}
}
