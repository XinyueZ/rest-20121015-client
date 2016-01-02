package com.rest.client.bus;

import io.realm.RealmObject;

public final class DeleteEvent {
	private int mPosition;
	private RealmObject mDBObject;

	public DeleteEvent( int position, RealmObject DBObject ) {
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
