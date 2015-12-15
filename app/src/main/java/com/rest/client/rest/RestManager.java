package com.rest.client.rest;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rest.client.rest.events.AuthenticatedEvent;
import com.rest.client.rest.events.AuthenticationErrorEvent;
import com.rest.client.rest.events.RestConnectedEvent;
import com.rest.client.rest.events.RestObjectAddedEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class RestManager implements AuthResultHandler, ChildEventListener {

	private static String URL  = "https://rest-20121015.firebaseio.com";
	private static String AUTH = "IJ0kevPaQaMof0DxBXkwM54DdJ36cWK8wbedkoMe";
	public  Firebase DB;
	/**
	 * DB for storing pending queue.
	 */
	private Realm    mRealm;
	static  boolean  DB_CONNECTED;

	private  Class<?> mRestClazz;

	public void init( Application app,   Class<?> clazz) {
		mRestClazz = clazz;
		mRealm = Realm.getInstance( app );
		DB_CONNECTED = isNetworkAvailable( app );
		Firebase.setAndroidContext( app );
		DB = new Firebase( URL );
		DB.keepSynced( true );
		DB.authWithCustomToken(
				AUTH,
				this
		);
		DB.addChildEventListener( this );

		/**
		 The Firebase suggestion of handling connection and disconnection is nice.
		 However in order to give client better feeling of disconnection, the rest package
		 Use {@link RestNetworkChangeReceiver} to covert "disconnect" because it is little sensitive.
		 The "connection" works well.
		 */
		Firebase connectedRef = new Firebase( URL + "/.info/connected" );
		connectedRef.addValueEventListener( new ValueEventListener() {
			@Override
			public void onDataChange( DataSnapshot snapshot ) {
				boolean connected = snapshot.getValue( Boolean.class );
				if( connected ) {
					boolean hasPending = mRealm.where( RestPendingObject.class )
											   .count() > 0;
					if( hasPending ) {
						mRealm.beginTransaction();
						mRealm.clear( RestPendingObject.class );
						mRealm.commitTransaction();
					}
					EventBus.getDefault()
							.post( new RestConnectedEvent() );
				}
			}

			@Override
			public void onCancelled( FirebaseError error ) {
				System.err.println( "Listener was cancelled" );
			}
		} );
	}

	//[AuthResultHandler]
	@Override
	public void onAuthenticated( AuthData authData ) {
		EventBus.getDefault()
				.post( new AuthenticatedEvent( authData ) );
	}

	@Override
	public void onAuthenticationError( FirebaseError firebaseError ) {
		EventBus.getDefault()
				.post( new AuthenticationErrorEvent( firebaseError ) );
	}

	/**
	 * Helper for checking current network status.
	 *
	 * @param cxt
	 * 		{@link Context}.
	 *
	 * @return {@code true} if network is o.k.
	 */
	static boolean isNetworkAvailable( Context cxt ) {
		ConnectivityManager connectivityManager = (ConnectivityManager) cxt.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo         activeNetworkInfo   = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


	/**
	 * Save data on DB.
	 *
	 * @param t
	 * 		{@link RestObject} to save on DB.
	 */
	public void save( RestObject t ) {
		//TO PENDING QUEUE.
		mRealm.beginTransaction();
		RestPendingObject restPendingObject = new RestPendingObject();
		restPendingObject.setReqId( t.getReqId() );
		mRealm.copyToRealm( restPendingObject );
		mRealm.commitTransaction();
		//SAVE ON SERVER.
		DB.child( t.getReqId() )
		  .setValue( t );
		DB.push();
	}


	//[ChildEventListener]
	@Override
	public void onChildAdded( DataSnapshot dataSnapshot, String s ) {
		RestObject serverData = (RestObject) dataSnapshot.getValue( mRestClazz );
		long count = mRealm.where( RestPendingObject.class )
						   .equalTo(
								   "reqId",
								   serverData.getReqId()
						   )
						   .count();
		int status = DB_CONNECTED || count == 0 ? RestObjectProxy.SYNCED : RestObjectProxy.NOT_SYNCED;
		RestObjectProxy proxy = serverData.createProxy( serverData );
		proxy.setStatus( status );
		EventBus.getDefault()
				.post( new RestObjectAddedEvent( proxy ) );
	}

	@Override
	public void onChildChanged( DataSnapshot dataSnapshot, String s ) {

	}

	@Override
	public void onChildRemoved( DataSnapshot dataSnapshot ) {

	}

	@Override
	public void onChildMoved( DataSnapshot dataSnapshot, String s ) {

	}

	@Override
	public void onCancelled( FirebaseError firebaseError ) {

	}

}
