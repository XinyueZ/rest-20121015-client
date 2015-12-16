package com.rest.client.rest.events;


import com.firebase.client.AuthData;

public final class AuthenticatedEvent {
	private int      mId;
	private AuthData mAuthData;


	public AuthenticatedEvent( int id, AuthData authData ) {
		mId = id;
		mAuthData = authData;
	}


	public AuthData getAuthData() {
		return mAuthData;
	}

	public int getId() {
		return mId;
	}
}
