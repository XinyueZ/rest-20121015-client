package com.rest.client.rest.events;


public final class UpdateNetworkStatus {
	private boolean mConnected;

	public UpdateNetworkStatus( boolean connected ) {
		mConnected = connected;
	}


	public boolean isConnected() {
		return mConnected;
	}
}
