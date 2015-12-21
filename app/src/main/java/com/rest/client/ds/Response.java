package com.rest.client.ds;


import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.rest.client.rest.RestObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public final class Response extends RestObject {
	@SerializedName("reqId")
	private String mReqId;

	@SerializedName("status")
	private int mStatus;


	@SerializedName("result")
	private List<Client> mResult;


	public int getStatus() {
		return mStatus;
	}

	public List<Client> getResult() {
		return mResult;
	}

	@Override
	public String getReqId() {
		return mReqId;
	}


	@Override
	public long getReqTime() {
		return 0;
	}

	@Override
	public RealmObject updateDB(int status) {
		Realm db = Realm.getDefaultInstance();

		RealmList<ClientDB> dbRealmList = new RealmList<>();
		List<Client>        clientList  = getResult();
		for( Client client : clientList ) {
			dbRealmList
				  .add( (ClientDB) client.updateDB( status));
		}

		db.beginTransaction();
		ResponseDB dbItem = new ResponseDB();
		dbItem.setReqId( getReqId() );
		dbItem.setResponseStatus( getStatus() );
		dbItem.setStatus( status );
		dbItem.setResult( dbRealmList );
		db.copyToRealmOrUpdate( dbItem );
		db.commitTransaction();
		return dbItem;
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return ResponseDB.class;
	}
}
