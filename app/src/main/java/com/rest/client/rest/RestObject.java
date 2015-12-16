package com.rest.client.rest;


public interface RestObject {
	//Request ID --> must be "reqId" for json/gson/jackson.
	String getReqId();
	//Proxy builder.
	RestObjectProxy createProxy( );
}
