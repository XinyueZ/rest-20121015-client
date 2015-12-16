package com.rest.client.rest.events;


public final class RestChangedAfterConnectEvent {
	private long mIndex;

	public RestChangedAfterConnectEvent( long index ) {
		mIndex = index;
	}


	public long getIndex() {
		return mIndex;
	}
}
