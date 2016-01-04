package com.rest.client.bus;

import io.realm.RealmObject;

public final class EditEvent {
	private int mPosition;
	private RealmObject mDBObject;

	public EditEvent( int position, RealmObject DBObject ) {
		mPosition = position;
		mDBObject = DBObject;
	}

	public int getPosition() {
		return mPosition;
	}

	public RealmObject getDBObject() {
		return mDBObject;
	}
}
