package com.rest.client.ds;


import com.chopping.rest.RestObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public final class Photo extends RestObject {
	@JsonProperty("reqId")
	@SerializedName("reqId")
	private String   mReqId;
	@JsonProperty("description")
	@SerializedName("description")
	private String   mDescription;
	@JsonProperty("date")
	@SerializedName("date")
	private String   mDate;
	@JsonProperty("title")
	@SerializedName("title")
	private String   mTitle;
	@JsonProperty("type")
	@SerializedName("type")
	private String   mType;
	@JsonProperty("urls")
	@SerializedName("urls")
	private PhotoUrl mPhotoUrl;

	@Override
	public String getReqId() {
		return mReqId;
	}

	public void setReqId( String reqId ) {
		mReqId = reqId;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription( String description ) {
		mDescription = description;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate( String date ) {
		mDate = date;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle( String title ) {
		mTitle = title;
	}

	public String getType() {
		return mType;
	}

	public void setType( String type ) {
		mType = type;
	}

	public PhotoUrl getPhotoUrl() {
		return mPhotoUrl;
	}

	public void setPhotoUrl( PhotoUrl photoUrl ) {
		mPhotoUrl = photoUrl;
	}

	@Override
	public long getReqTime() {
		//Can ignore.
		return 0;
	}

	@Override
	protected RealmObject[] newInstances( Realm db, int status ) {
		PhotoDB    photoDB;
		PhotoUrlDB photoUrlDB;

		switch( status ) {
			case DELETE_SYNCED:
				RealmResults<? extends RealmObject> dbItems = db.where( this.DBType() )
																.equalTo(
																		"reqId",
																		getReqId()
																)
																.findAll();
				photoDB = (PhotoDB) dbItems.first();
				break;
			default:
				photoUrlDB = new PhotoUrlDB();
				photoUrlDB.setReqId( getReqId() );
				photoUrlDB.setReqTime( System.currentTimeMillis() );
				photoUrlDB.setStatus( status );
				photoUrlDB.setHd( getPhotoUrl().getHd() );
				photoUrlDB.setNormal( getPhotoUrl().getNormal() );


				photoDB = new PhotoDB();
				photoDB.setReqId( getReqId() );
				photoDB.setReqTime( System.currentTimeMillis() );
				photoDB.setStatus( status );
				photoDB.setTitle( getTitle() );
				photoDB.setDescription( getDescription() );
				photoDB.setDate( getDate() );
				photoDB.setUrls( photoUrlDB );
				photoDB.setType( getType() );
				break;
		}
		return new RealmObject[] { photoDB };
	}

	@Override
	public Class<? extends RealmObject> DBType() {
		return PhotoDB.class;
	}

	@Override
	public RestObject newFromDB( RealmObject dbItem ) {
		PhotoDB photoDB = (PhotoDB) dbItem;
		Photo   photo   = new Photo();
		photo.setReqId( photoDB.getReqId() );
		photo.setDate( photoDB.getDate() );
		photo.setType( photoDB.getType() );
		photo.setTitle( photoDB.getTitle() );
		photo.setDescription( photoDB.getDescription() );

		PhotoUrl photoUrl = new PhotoUrl();
		photoUrl.setReqId( photoDB.getUrls()
								  .getReqId() );
		photoUrl.setHd( photoDB.getUrls()
							   .getHd() );
		photoUrl.setNormal( photoDB.getUrls()
								   .getNormal() );
		photo.setPhotoUrl( photoUrl );
		return photo;
	}
}
