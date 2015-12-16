package com.rest.client.rest.events;


public final class RestChangedAfterConnectEvent {
	private int mId;

	public int getId() {
		return mId;
	}

	private long mIndex;

	public RestChangedAfterConnectEvent(  int id,  long index ) {
		mId = id;
		mIndex = index;
	}


	public long getIndex() {
		return mIndex;
	}
}
