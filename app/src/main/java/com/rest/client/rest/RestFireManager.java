package com.rest.client.rest;

import android.app.Application;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.rest.client.rest.events.AuthenticatedEvent;
import com.rest.client.rest.events.AuthenticationErrorEvent;
import com.rest.client.rest.events.RestResponseEvent;

import de.greenrobot.event.EventBus;
import io.realm.RealmObject;

/**
 * Architecture for working with Firebase.
 *
 * @author Xinyue Zhao
 */
public class RestFireManager implements AuthResultHandler, ChildEventListener {
	//Firebase.
	private static String URL  = "https://rest-20121015.firebaseio.com";
	private static String AUTH = "IJ0kevPaQaMof0DxBXkwM54DdJ36cWK8wbedkoMe";
	private Firebase                    mFirebase;
	private Class<? extends RestObject> mRespType;
	/**
	 * The id of manger.
	 */
	private long                        mId;


	/**
	 * Initialize the manager.
	 *
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 */
	public void onCreate( Application app ) {
		setId( System.currentTimeMillis() );
		Firebase.setAndroidContext( app );
		mFirebase = new Firebase( URL );
		mFirebase.keepSynced( true );
		mFirebase.authWithCustomToken(
				AUTH,
				this
		);
	}

	/**
	 * Set the id of manger.
	 */
	private void setId( long id ) {
		mId = id;
	}

	/**
	 * Called when do not need manager.
	 */
	public void onDestory() {
		mFirebase.removeEventListener( this );
	}


	//[AuthResultHandler]
	@Override
	public void onAuthenticated( AuthData authData ) {
		EventBus.getDefault()
				.post( new AuthenticatedEvent(
						mId,
						authData
				) );
	}

	@Override
	public void onAuthenticationError( FirebaseError firebaseError ) {
		EventBus.getDefault()
				.post( new AuthenticationErrorEvent(
						mId,
						firebaseError
				) );
	}


	/**
	 * Save data on Firebase.
	 *
	 * @param newData
	 * 		{@link RestObject} to save on Firebase.
	 */
	public void save( RestObject newData ) {
		mFirebase.addChildEventListener( this );
		saveInBackground( newData );
	}

	/**
	 * Save data on Firebase in background, call this in thread.
	 *
	 * @param newData
	 * 		{@link RestObject} to save on Firebase.
	 */
	public void saveInBackground( RestObject newData ) {
		mRespType = newData.getClass();
		newData.updateDB( RestObject.NOT_SYNCED );
		mFirebase.child( newData.getReqId() )
				 .setValue( newData );
		mFirebase.push();
	}

	/**
	 * Get all data from Firebase of type {@code respType}.
	 *
	 * @param respType
	 * 		Server data type {@code respType}.
	 */
	public void selectAll( Class<? extends RestObject> respType ) {
		mRespType = respType;
		mFirebase.addChildEventListener( this );
	}

	public void executePending( ExecutePending exp ) {
		RestUtils.executePending( exp );
	}

	//[ChildEventListener]
	@Override
	public void onChildAdded( DataSnapshot dataSnapshot, String s ) {
		RestObject  serverData = dataSnapshot.getValue( mRespType );
		RealmObject dbItem     = serverData.updateDB( RestObject.SYNCED );
		EventBus.getDefault()
				.postSticky( new RestResponseEvent(
						mId,
						dbItem
				) );
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
