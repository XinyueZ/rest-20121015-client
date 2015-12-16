package com.rest.client.rest;


public interface RestObject {
	//Request ID --> must be "reqId" for json/gson/jackson.
	String getReqId();
	//Time to fire the request --> must be "reqTime" for json/gson/jackson.
	long getReqTime();
	//Proxy builder.
	RestObjectProxy createProxy(RestObject restObject );
}
