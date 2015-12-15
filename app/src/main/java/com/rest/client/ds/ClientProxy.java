package com.rest.client.ds;


import com.rest.client.rest.RestObject;
import com.rest.client.rest.RestObjectProxy;

public final class ClientProxy extends RestObjectProxy {
	public ClientProxy( RestObject restObject ) {
		super( restObject );
	}

	public long getReqTime() {
		return ( (Client) getRestObject() ).getReqTime();
	}

	public String getComment() {
		return ( (Client) getRestObject() ).getComment();
	}
}
