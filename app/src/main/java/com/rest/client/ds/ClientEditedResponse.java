package com.rest.client.ds;


import com.chopping.rest.RestObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class ClientEditedResponse extends RestObject {

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
		RealmResults<ClientDB> dbItems2 = db.where( ClientDB.class )
											.equalTo(
													"reqId",
													getReqId()
											)
											.findAll();
		ClientDB dbItem = dbItems2.first();
		dbItem.setStatus( status );
		return new RealmObject[] { dbItem };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return ClientDB.class;
	}


}
