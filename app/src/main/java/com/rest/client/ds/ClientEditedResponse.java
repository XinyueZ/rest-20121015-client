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
		RealmResults<EditClientRequestDB> dbItems = db.where( EditClientRequestDB.class )
										   .equalTo(
												   "reqId",
												   getReqId()
										   )
										   .findAll();
		EditClientRequestDB editedRequestItem = dbItems.first();
		editedRequestItem.setStatus( status );

		RealmResults<ClientDB> dbItems2 = db.where( ClientDB.class )
										   .equalTo(
												   "reqId",
												   getReqId()
										   )
										   .findAll();
		ClientDB dbItem = dbItems2.first();
		dbItem.setReqId( editedRequestItem.getReqId() );
		dbItem.setReqTime( editedRequestItem.getReqTime() );
		dbItem.setComment( editedRequestItem.getComment() );
		dbItem.setStatus( status );
		return new RealmObject[] { editedRequestItem, dbItem };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return EditClientRequestDB.class;
	}


}
