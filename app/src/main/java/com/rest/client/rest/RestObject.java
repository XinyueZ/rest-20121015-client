package com.rest.client.rest;


public interface RestObject {
	//Request ID --> must be "reqId" for json.
	String getReqId();
	//Proxy builder.
	RestObjectProxy createProxy(RestObject restObject );
}
