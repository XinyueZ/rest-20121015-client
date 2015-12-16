package com.rest.client.rest.events;


public final class RestConnectEvent {
	private int mId;

	public RestConnectEvent( int id ) {
		mId = id;
	}

	public int getId() {
		return mId;
	}
}
