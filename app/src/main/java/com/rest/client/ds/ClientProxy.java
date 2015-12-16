package com.rest.client.ds;


import com.rest.client.rest.RestObject;
import com.rest.client.rest.RestObjectProxy;

public final class ClientProxy extends RestObjectProxy {
	public ClientProxy( RestObject restObject ) {
		super( restObject );
	}


	public String getComment() {
		return ( (Client) getRestObject() ).getComment();
	}
}
