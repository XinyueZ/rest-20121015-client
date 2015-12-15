package com.rest.client.rest.events;


import com.rest.client.rest.RestObjectProxy;

public final class RestObjectAddedEvent {
	private RestObjectProxy mRestObjectProxy;


	public RestObjectAddedEvent( RestObjectProxy restObjectProxy ) {
		mRestObjectProxy = restObjectProxy;
	}


	public RestObjectProxy getRestObjectProxy() {
		return mRestObjectProxy;
	}
}
