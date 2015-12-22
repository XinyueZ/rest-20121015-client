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
import com.rest.client.rest.events.UpdateNetworkStatus;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * Architecture for working with Firebase.
 *
 * @author Xinyue Zhao
 */
public class RestFireManager implements AuthResultHandler, ChildEventListener {
	//Firebase.
	private static String URL  = "https://rest-20121015.firebaseio.com";
	private static String AUTH = "IJ0kevPaQaMof0DxBXkwM54DdJ36cWK8wbedkoMe";
	private Firebase mFirebase;
	/**
	 * Local storage for storing pending queue and back-off data.
	 */
	private Realm    mDB;
	/**
	 * The network connect status.
	 */
	private boolean  mConnected;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link AuthenticatedEvent}.
	 *
	 * @param e
	 * 		Event {@link AuthenticatedEvent}.
	 */
	public void onEventMainThread( AuthenticatedEvent e ) {

	}

	/**
	 * Handler for {@link AuthenticatedEvent}.
	 *
	 * @param e
	 * 		Event {@link AuthenticatedEvent}.
	 */
	public void onEventMainThread( AuthenticationErrorEvent e ) {

	}


	/**
	 * Handler for {@link UpdateNetworkStatus}.
	 *
	 * @param e
	 * 		Event {@link UpdateNetworkStatus}.
	 */
	public void onEventMainThread( UpdateNetworkStatus e ) {
		setConnected( e.isConnected() );
	}
	//------------------------------------------------

	/**
	 * The id of manger.
	 */
	private int mId;

	/**
	 * The id of manger.
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Set the id of manger.
	 */
	public void setId( int id ) {
		mId = id;
	}


	/**
	 * Initialize the manager.
	 *
	 * @param id
	 * 		Manager id.
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 */
	public void init( int id, Application app ) {
		setId( id );
		mDB = Realm.getDefaultInstance();
		Firebase.setAndroidContext( app );
		mFirebase = new Firebase( URL );
		mFirebase.keepSynced( true );
		mFirebase.authWithCustomToken(
				AUTH,
				this
		);
	}


	/**
	 * Setup the manager on UI.
	 *
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 */
	public void install( Application app ) {
		if( !EventBus.getDefault()
					 .isRegistered( this ) ) {
			EventBus.getDefault()
					.register( this );
		}
		mConnected = RestUtils.isNetworkAvailable( app );
	}

	/**
	 * Remove the manager from UI.
	 */
	public void uninstall() {
		if( EventBus.getDefault()
					.isRegistered( this ) ) {
			EventBus.getDefault()
					.unregister( this );
		}
		mFirebase.removeEventListener( this );
	}

	/**
	 * Get network connect status.
	 *
	 * @return {@code true} if connection is o.k.
	 */
	protected boolean isConnected() {
		return mConnected;
	}

	/**
	 * Set network status.
	 *
	 * @param connected
	 * 		{@code true} if connection is o.k.
	 */
	public void setConnected( boolean connected ) {
		mConnected = connected;
	}




	//[AuthResultHandler]
	@Override
	public void onAuthenticated( AuthData authData ) {
		EventBus.getDefault()
				.post( new AuthenticatedEvent(
						getId(),
						authData
				) );
	}

	@Override
	public void onAuthenticationError( FirebaseError firebaseError ) {
		EventBus.getDefault()
				.post( new AuthenticationErrorEvent(
						getId(),
						firebaseError
				) );
	}

	private Class<? extends RealmObject> mDBType;
	private Class<? extends RestObject>  mRespType;


	/**
	 * Save data on Firebase.
	 *
	 * @param newData
	 * 		{@link RestObject} to save on Firebase.
	 */
	public void save( RestObject newData ) {
		mFirebase.addChildEventListener( this );
		saveInBackground(newData);
	}

	/**
	 * Save data on Firebase in background, call this in thread.
	 *
	 * @param newData
	 * 		{@link RestObject} to save on Firebase.
	 */
	public void saveInBackground( RestObject newData ) {
		mRespType = newData.getClass();
		mDBType = newData.DBType();
		newData.updateDB( !isConnected() ? RestObject.NOT_SYNCED : RestObject.SYNCED );
		//SAVE ON SERVER.
		mFirebase.child( newData.getReqId() )
				 .setValue( newData );
		mFirebase.push();
	}

	/**
	 * Get all data from Firebase of type {@code respType}.
	 * @param dbType The type of objects saved local to represent {@code respType}.
	 * @param respType Server data type {@code respType}.
	 */
	public void selectAll( Class<? extends RealmObject> dbType, Class<? extends RestObject> respType ) {
		mDBType = dbType;
		mRespType = respType;
		mFirebase.addChildEventListener( this );
	}

	//[ChildEventListener]
	@Override
	public void onChildAdded( DataSnapshot dataSnapshot, String s ) {
		RestObject serverData = dataSnapshot.getValue( mRespType );
		RealmQuery<? extends RealmObject> notInLocal = mDB.where( mDBType )
														  .equalTo(
																  "reqId",
																  serverData.getReqId()
														  );
		RealmObject dbItem;
		if( notInLocal.count() < 1 ) {
			//NEW DATA FROM OTHER SIDE WHICH IS NOT SAVED LOCAL.
			dbItem = serverData.updateDB( RestObject.SYNCED );
		} else {
			//UPDATE LOCAL STATUS.
			RealmQuery<? extends RealmObject> qNotSynced = mDB.where( mDBType )
															  .equalTo(
																	  "reqId",
																	  serverData.getReqId()
															  )
															  .equalTo(
																	  "status",
																	  RestObject.NOT_SYNCED
															  );
			//THIS REQUEST IS NOT SYNCED.
			boolean notSynced = qNotSynced.count() > 0;
			dbItem = serverData.updateDB( notSynced ? RestObject.NOT_SYNCED : RestObject.SYNCED );
		}
		EventBus.getDefault()
				.postSticky( new RestResponseEvent(
						getId(),
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
