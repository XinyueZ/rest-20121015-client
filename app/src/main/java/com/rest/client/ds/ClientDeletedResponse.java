package com.rest.client.ds;


import com.chopping.rest.RestObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class ClientDeletedResponse extends RestObject {

	@SerializedName("reqId")
	@JsonProperty("reqId")
	private String mReqId;
	@SerializedName("status")
	@JsonProperty("status")
	private int    mStatus;

	@Override
	public String getReqId() {
		return mReqId;
	}

	public void setReqId( String reqId ) {
		mReqId = reqId;
	}

	@Override
	public long getReqTime() {
		return 0;
	}

	public int getStatus() {
		return mStatus;
	}

	@Override
	public RealmObject[] newInstances( Realm db, int status ) {
		RealmResults<ClientDB> dbItems = db.where( ClientDB.class )
										   .equalTo(
												   "reqId",
												   getReqId()
										   )
										   .findAll();
		ClientDB clientDB = dbItems.first();
		clientDB.setStatus( status );
		return new RealmObject[] { clientDB };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return ClientDB.class;
	}


	@Override
	public RestObject fromDB( RealmObject dbItem ) {
		ClientDB clientDB = (ClientDB) dbItem;
		ClientDeletedResponse   delClient   = new ClientDeletedResponse();
		delClient.setReqId( clientDB.getReqId() );
		return delClient;
	}
}
