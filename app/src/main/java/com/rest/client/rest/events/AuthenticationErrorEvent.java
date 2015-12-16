package com.rest.client.rest.events;


import com.firebase.client.FirebaseError;

public final class AuthenticationErrorEvent {
	private int mId;

	private FirebaseError mFirebaseError;


	public AuthenticationErrorEvent( int id, FirebaseError firebaseError ) {
		mId = id;
		mFirebaseError = firebaseError;
	}


	public FirebaseError getFirebaseError() {
		return mFirebaseError;
	}

	public int getId() {
		return mId;
	}
}
