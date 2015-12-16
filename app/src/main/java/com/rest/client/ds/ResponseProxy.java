package com.rest.client.ds;


import java.util.List;

import com.rest.client.rest.RestObject;
import com.rest.client.rest.RestObjectProxy;

public final class ResponseProxy extends RestObjectProxy {
	public ResponseProxy( RestObject restObject ) {
		super( restObject );
	}

	public List<Client> getResult() {
		return ( (Response) getRestObject() ).getResult();
	}
}
