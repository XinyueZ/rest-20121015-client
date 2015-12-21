package com.rest.client.rest;

import java.lang.reflect.Method;

import android.app.Application;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rest.client.rest.events.AuthenticatedEvent;
import com.rest.client.rest.events.AuthenticationErrorEvent;
import com.rest.client.rest.events.RestResponseEvent;
import com.rest.client.rest.events.UpdateNetworkStatus;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Architecture for working with Firebase.
 *
 * @author Xinyue Zhao
 */
public class RestFireManager implements AuthResultHandler, ChildEventListener {
	//Firebase.
	private static String URL  = "https://rest-20121015.firebaseio.com";
	private static String AUTH = "IJ0kevPaQaMof0DxBXkwM54DdJ36cWK8wbedkoMe";
	private Firebase mDatabase;
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
		mDB = Realm.getInstance( app );
		Firebase.setAndroidContext( app );
		mDatabase = new Firebase( URL );
		mDatabase.keepSynced( true );
		mDatabase.authWithCustomToken(
				AUTH,
				this
		);
	}


	/**
	 * Setup the manager on UI.
	 *
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 * @param restObjectClazz
	 * 		The meta of response object.
	 */
	public void install( Application app, Class<? extends RestObject> restObjectClazz  ) {
		if( !EventBus.getDefault()
					 .isRegistered( this ) ) {
			EventBus.getDefault()
					.register( this );
		}
		mObjectType = restObjectClazz;
		Firebase connectedRef = new Firebase( URL + "/.info/connected" );
		connectedRef.addValueEventListener( mDBConnectStatusHandler );
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
		mDatabase.removeEventListener( this );
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

	/**
	 * The Firebase suggestion of handling connection and disconnection is nice. However in order to give client better feeling of disconnection, the
	 * rest package Use {@link RestNetworkChangeReceiver} to covert "disconnect" because it is little sensitive. The "connection" works well.
	 */
	private ValueEventListener mDBConnectStatusHandler = new ValueEventListener() {
		@Override
		public void onDataChange( DataSnapshot snapshot ) {
			boolean connected = snapshot.getValue( Boolean.class );
			if( connected && mDBType != null) {
				RealmResults<? extends RealmObject> notSyncItems = mDB.where( mDBType )
																	  .equalTo(
																			  "status",
																			  RestObject.NOT_SYNCED
																	  )
																	  .findAll();
				if( notSyncItems.size() > 0 ) {
					mDB.beginTransaction();
					RealmObject item = notSyncItems.get( 0 );
					while( item != null ) {
						Method method;
						try {
							method = item.getClass()
										 .getMethod(
												 "setStatus",
												 int.class
										 );
							method.invoke(
									item,
									RestObject.SYNCED
							);
						} catch( Exception e ) {
							e.printStackTrace();
						}
						mDB.copyToRealmOrUpdate( item );
						EventBus.getDefault()
								.post( new RestResponseEvent(
										getId(),
										item
								) );
						if( notSyncItems.size() > 0 ) {
							item = notSyncItems.get( 0 );
						} else {
							break;
						}
					}
					mDB.commitTransaction();
				}
			}
		}

		@Override
		public void onCancelled( FirebaseError error ) {
			System.err.println( "Listener was cancelled" );
		}
	};


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

	private Class<?  extends RealmObject>   mDBType;
	private Class<? extends RestObject>  mObjectType;


	/**
	 * Save data on Firebase.
	 *
	 * @param newData
	 * 		{@link RestObject} to save on Firebase.
	 */
	public void save( RestObject newData ) {
		mDBType = newData.DBType();
		newData.updateDB( !isConnected() ? RestObject.NOT_SYNCED : RestObject.SYNCED );
		//SAVE ON SERVER.
		mDatabase.child( newData.getReqId() )
				 .setValue( newData );
		mDatabase.push();
	}


	public void selectAll(Class<?  extends RealmObject> dbType) {
		mDBType = dbType;
		mDatabase.addChildEventListener( this );
	}

	//[ChildEventListener]
	@Override
	public void onChildAdded( DataSnapshot dataSnapshot, String s ) {
		RestObject serverData = dataSnapshot.getValue( mObjectType );
		RealmQuery<? extends RealmObject> notInLocal = mDB.where(  mDBType )
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
