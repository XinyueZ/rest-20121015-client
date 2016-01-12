package com.rest.client.ds;


import android.support.annotation.NonNull;

import com.chopping.rest.RestObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;

public final class PhotoUrl extends RestObject {

	@JsonProperty("reqId")
	private String mReqId;


	@SerializedName("normal")
	@JsonProperty("normal")
	private String mNormal;

	@SerializedName("hd")
	@JsonProperty("hd")
	private String mHd;


	@Override
	public String getReqId() {
		return mReqId;
	}

	public void setReqId( String reqId ) {
		mReqId = reqId;
	}

	public String getNormal() {
		return mNormal;
	}

	public void setNormal( String normal ) {
		mNormal = normal;
	}

	public String getHd() {
		return mHd;
	}

	public void setHd( String hd ) {
		mHd = hd;
	}

	@Override
	public long getReqTime() {
		//Can ignore.
		return 0;
	}

	@NonNull
	@Override
	protected RealmObject[] newInstances( Realm db, int status ) {
		PhotoUrlDB dbItem = new PhotoUrlDB();
		dbItem.setReqId( getReqId() );
		dbItem.setHd( getHd() );
		dbItem.setNormal( getNormal() );
		dbItem.setStatus( status );
		return new RealmObject[] { dbItem };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return PhotoUrlDB.class;
	}

	@Override
	public RestObject newFromDB( RealmObject dbItem ) {
		PhotoUrlDB urlDB = (PhotoUrlDB) dbItem;
		PhotoUrl   url   = new PhotoUrl();
		url.setReqId( urlDB.getReqId() );
		url.setHd( urlDB.getHd() );
		url.setNormal( urlDB.getNormal() );
		return url;
	}
}
