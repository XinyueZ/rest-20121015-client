package com.rest.client.events;


import com.rest.client.ds.Client;

public final class InsertClientEvent {
	private Client mClient;

	public InsertClientEvent( Client client ) {
		mClient = client;
	}

	public Client getClient() {
		return mClient;
	}
}
