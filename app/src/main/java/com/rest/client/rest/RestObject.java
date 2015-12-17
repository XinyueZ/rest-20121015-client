package com.rest.client.rest;


public abstract class RestObject {
	//Request ID --> must be "reqId" for json/gson/jackson.
	public abstract String getReqId();

	//Time to fire the request --> must be "reqTime" for json/gson/jackson.
	public abstract long getReqTime();

	//Proxy builder.
	public abstract RestObjectProxy createProxy();
}
