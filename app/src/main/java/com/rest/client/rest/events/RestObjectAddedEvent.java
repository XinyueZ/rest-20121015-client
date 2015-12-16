package com.rest.client.rest.events;


public final class RestObjectAddedEvent {
	private int mId;

	public RestObjectAddedEvent( int id ) {
		mId = id;
	}

	public int getId() {
		return mId;
	}


}
