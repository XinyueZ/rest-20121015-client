package com.rest.client.rest.events;


public final class RestChangedAfterConnectEvent {
	private int mIndex;

	public RestChangedAfterConnectEvent( int index ) {
		mIndex = index;
	}


	public int getIndex() {
		return mIndex;
	}
}
