package com.rest.client.rest.events;

import android.support.annotation.Nullable;

import com.rest.client.rest.RestObjectProxy;

public final class RestApiResponseArrivalEvent {
	private int mId;

	private long mIndex;

	private @Nullable
	RestObjectProxy mRestObjectProxy;

	private boolean mFromHistory;

	public RestApiResponseArrivalEvent( int id, long index, @Nullable RestObjectProxy restObjectProxy, boolean fromHistory ) {
		mId = id;
		mIndex = index;
		mRestObjectProxy = restObjectProxy;
		mFromHistory = fromHistory;
	}


	public long getIndex() {
		return mIndex;
	}

	@Nullable
	public RestObjectProxy getArrivalRestObjectProxy() {
		return mRestObjectProxy;
	}


	public int getId() {
		return mId;
	}


	public boolean isFromHistory() {
		return mFromHistory;
	}
}
