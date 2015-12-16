package com.rest.client.rest.events;

public final class RestResponseArrivalEvent {
	private long mIndex;

	public RestResponseArrivalEvent( long index ) {
		mIndex = index;
	}


	public long getIndex() {
		return mIndex;
	}
}
