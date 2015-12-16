package com.rest.client.rest.events;

import com.rest.client.rest.RestObjectProxy;

public final class RestApiResponseArrivalEvent {
	private int mId;

	public int getId() {
		return mId;
	}

	private long mIndex;

	private RestObjectProxy mRestObjectProxy;

	public RestApiResponseArrivalEvent( int id, long index, RestObjectProxy restObjectProxy ) {
		mId = id;
		mIndex = index;
		mRestObjectProxy = restObjectProxy;
	}


	public long getIndex() {
		return mIndex;
	}

	public RestObjectProxy getRestObjectProxy() {
		return mRestObjectProxy;
	}
}
