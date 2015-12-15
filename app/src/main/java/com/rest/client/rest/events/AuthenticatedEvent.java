package com.rest.client.rest.events;


import com.firebase.client.AuthData;

public final class AuthenticatedEvent {
	private AuthData mAuthData;


	public AuthenticatedEvent( AuthData authData ) {
		mAuthData = authData;
	}


	public AuthData getAuthData() {
		return mAuthData;
	}
}
