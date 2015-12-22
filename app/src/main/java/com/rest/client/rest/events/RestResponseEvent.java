package com.rest.client.rest.events;

import android.support.annotation.Nullable;

import io.realm.RealmObject;

public final class RestResponseEvent {
	private long         mId;
	private
	@Nullable
			RealmObject mDBItem;

	public RestResponseEvent( long id, @Nullable RealmObject dbItem ) {
		mId = id;
		mDBItem = dbItem;
	}


	public long getId() {
		return mId;
	}


	public
	@Nullable
	RealmObject getDBItem() {
		return mDBItem;
	}
}
