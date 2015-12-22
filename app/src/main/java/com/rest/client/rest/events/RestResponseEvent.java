package com.rest.client.rest.events;

import android.support.annotation.Nullable;

import io.realm.RealmObject;

public final class RestResponseEvent {
	private int         mId;
	private
	@Nullable
			RealmObject mDBItem;

	public RestResponseEvent( int id, @Nullable RealmObject dbItem ) {
		mId = id;
		mDBItem = dbItem;
	}


	public int getId() {
		return mId;
	}


	public
	@Nullable
	RealmObject getDBItem() {
		return mDBItem;
	}
}