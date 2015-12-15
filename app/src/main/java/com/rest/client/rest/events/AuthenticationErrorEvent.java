package com.rest.client.rest.events;


import com.firebase.client.FirebaseError;

public final class AuthenticationErrorEvent {
	private FirebaseError mFirebaseError;


	public AuthenticationErrorEvent( FirebaseError firebaseError ) {
		mFirebaseError = firebaseError;
	}


	public FirebaseError getFirebaseError() {
		return mFirebaseError;
	}
}
