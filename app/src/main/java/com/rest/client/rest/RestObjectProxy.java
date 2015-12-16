package com.rest.client.rest;


public class RestObjectProxy implements RestObject {
	public static final int NOT_SYNCED = 0;
	public static final int SYNCED     = 1;
	private RestObject mRestObject;
	private int        mStatus;

	public RestObjectProxy( RestObject restObject ) {
		mRestObject = restObject;
	}


	protected RestObject getRestObject() {
		return mRestObject;
	}

	public int getStatus() {
		return mStatus;
	}


	public void setStatus( int status ) {
		mStatus = status;
	}


	@Override
	public String getReqId() {
		return mRestObject.getReqId();
	}


	@Override
	public RestObjectProxy createProxy(  ) {
		return null;
	}
}
