package com.rest.client.ds;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.rest.client.rest.RestObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class ClientAddedResponse extends RestObject {

	@SerializedName("reqId")
	@JsonProperty("reqId")
	private String mReqId;
	@SerializedName("status")
	@JsonProperty("status")
	private int mStatus;

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

	@Override
	public RealmObject updateDB(int status) {
		Realm db = Realm.getDefaultInstance();
		RealmResults<ClientDB> dbItems = Realm.getDefaultInstance( )
											  .where( ClientDB.class )
											  .equalTo( "reqId", getReqId() )
											  .findAll();
		ClientDB clientDB = dbItems.first();
		db.beginTransaction();
		clientDB.setStatus( status );
		db.commitTransaction();
		return clientDB;
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return ClientDB.class;
	}
}
