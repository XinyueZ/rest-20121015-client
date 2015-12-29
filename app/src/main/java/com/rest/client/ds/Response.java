package com.rest.client.ds;


import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.chopping.rest.RestObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

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
	public Class<? extends RealmObject> DBType() {
		return ResponseDB.class;
	}

	@Override
	public RealmObject[] newInstances( Realm db, int status ) {
		RealmList<ClientDB> dbRealmList = new RealmList<>();
		List<Client>        clientList  = getResult();
		for( Client client : clientList ) {
			RealmObject[] objects = client.newInstances(
					db,
					status
			);
			dbRealmList.add( (ClientDB) objects[ 0 ] );
		}

		ResponseDB dbItem = new ResponseDB();
		dbItem.setReqId( getReqId() );
		dbItem.setResponseStatus( getStatus() );
		dbItem.setStatus( status );
		dbItem.setResult( dbRealmList );

		RealmResults<RequestForResponseDB> reqItemDbs = db.where( RequestForResponseDB.class )
														  .equalTo(
																  "reqId",
																  getReqId()
														  )
														  .findAll();
		RequestForResponseDB reqItemDb = reqItemDbs.first();
		reqItemDb.setStatus( status );
		return new RealmObject[] { dbItem , reqItemDb };
	}
}
