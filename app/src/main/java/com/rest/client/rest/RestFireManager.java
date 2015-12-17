package com.rest.client.rest;

import java.util.List;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rest.client.ds.ClientProxy;
import com.rest.client.rest.events.AuthenticatedEvent;
import com.rest.client.rest.events.AuthenticationErrorEvent;
import com.rest.client.rest.events.RestChangedAfterConnectEvent;
import com.rest.client.rest.events.RestConnectEvent;
import com.rest.client.rest.events.RestObjectAddedEvent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import io.realm.Realm;

/**
 * Architecture for working with Firebase.
 *
 * @author Xinyue Zhao
 */
public class RestFireManager implements AuthResultHandler, ChildEventListener {
	//Firebase.
	private static String URL  = "https://rest-20121015.firebaseio.com";
	private static String AUTH = "IJ0kevPaQaMof0DxBXkwM54DdJ36cWK8wbedkoMe";
	private Firebase                    mDatabase;
	/**
	 * Local storage for storing pending queue and back-off data.
	 */
	private Realm                       mSentReqIds;
	/**
	 * The network connect status.
	 */
	private boolean                     mConnected;
	/**
	 * Meta class of {@link RestObject}.
	 */
	private Class<? extends RestObject> mRestClazz;
	/**
	 * Pool to hold data from Firebase.
	 */
	private
	@Nullable
	List<RestObjectProxy> mProxyPool;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link AuthenticatedEvent}.
	 *
	 * @param e
	 * 		Event {@link AuthenticatedEvent}.
	 */
	@Subscribe
	public void onEvent( AuthenticatedEvent e ) {

	}

	/**
	 * Handler for {@link AuthenticatedEvent}.
	 *
	 * @param e
	 * 		Event {@link AuthenticatedEvent}.
	 */
	@Subscribe
	public void onEvent( AuthenticationErrorEvent e ) {

	}

	/**
	 * Handler for {@link RestObjectAddedEvent}.
	 *
	 * @param e
	 * 		Event {@link RestObjectAddedEvent}.
	 */
	@Subscribe
	public void onEvent( RestObjectAddedEvent e ) {

	}


	/**
	 * Handler for {@link RestConnectEvent}.
	 *
	 * @param e
	 * 		Event {@link RestConnectEvent}.
	 */
	@Subscribe
	public void onEvent( RestConnectEvent e ) {

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
	private void setId( int id ) {
		mId = id;
	}

	/**
	 * Constructor of {@link RestFireManager}.
	 *
	 * @param restClazz
	 * 		The class meta of from server returned data.
	 */
	public RestFireManager( Class<? extends RestObject> restClazz ) {
		mRestClazz = restClazz;
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
		mSentReqIds = Realm.getInstance( app );
		Firebase.setAndroidContext( app );
		mDatabase = new Firebase( URL );
		mDatabase.keepSynced( true );
		mDatabase.authWithCustomToken(
				AUTH,
				this
		);
		Firebase connectedRef = new Firebase( URL + "/.info/connected" );
		connectedRef.addValueEventListener( mDBConnectStatusHandler );
	}


	/**
	 * Setup the manager on UI.
	 *
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 * @param proxyPool
	 * 		Pool to hold data from Firebase.
	 */
	public void install( Application app, List<RestObjectProxy> proxyPool ) {
		EventBus.getDefault()
				.register( this );
		mConnected = RestUtils.isNetworkAvailable( app );
		setProxyPool( proxyPool );
		mDatabase.addChildEventListener( this );
	}

	/**
	 * Remove the manager from UI.
	 */
	public void uninstall() {
		EventBus.getDefault()
				.unregister( this );
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
			if( connected ) {
				clearPending();
				if( getProxyPool() != null ) {
					int i = 0;
					for( RestObjectProxy proxy : getProxyPool() ) {
						if( proxy.getStatus() == ClientProxy.NOT_SYNCED ) {
							proxy.setStatus( ClientProxy.SYNCED );
							EventBus.getDefault()
									.post( new RestChangedAfterConnectEvent(
											getId(),
											i
									) );
						}
						i++;
					}
				}
				EventBus.getDefault()
						.post( new RestConnectEvent( getId() ) );
			}
		}

		@Override
		public void onCancelled( FirebaseError error ) {
			System.err.println( "Listener was cancelled" );
		}
	};

	/**
	 * Remove all posted pending requests of offline.
	 */
	private void clearPending() {
		boolean hasPending = mSentReqIds.where( RestPendingObject.class )
										.count() > 0;
		if( hasPending ) {
			mSentReqIds.beginTransaction();
			mSentReqIds.clear( RestPendingObject.class );
			mSentReqIds.commitTransaction();
		}
	}

	/**
	 * Set pool to hold data from Firebase.
	 */
	public void setProxyPool( @Nullable List<RestObjectProxy> proxyPool ) {
		mProxyPool = proxyPool;
	}

	/**
	 * Get pool to hold data from Firebase.
	 */
	@Nullable
	public List<RestObjectProxy> getProxyPool() {
		return mProxyPool;
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


	/**
	 * Save data on Firebase.
	 *
	 * @param t
	 * 		{@link RestObject} to save on Firebase.
	 */
	public void save( RestObject t ) {
		//TO PENDING QUEUE FOR OFFLINE.
		//BECAUSE SOMETIMES THE SAVE IS
		//DONE UNDER OFFLINE AND FIREBAES
		//HANDLES IT LOCAL. IN ORDER TO MARK
		//OFFLINE STATUS ON APP-CLIENT THE
		//QUEUE IS USED.
		if( !isConnected() ) {
			mSentReqIds.beginTransaction();
			RestPendingObject restPendingObject = new RestPendingObject();
			restPendingObject.setReqId( t.getReqId() );
			restPendingObject.setReqTime( t.getReqTime() );
			mSentReqIds.copyToRealm( restPendingObject );
			mSentReqIds.commitTransaction();
		}
		//SAVE ON SERVER.
		mDatabase.child( t.getReqId() )
				 .setValue( t );
		mDatabase.push();
	}


	//[ChildEventListener]
	@Override
	public void onChildAdded( DataSnapshot dataSnapshot, String s ) {
		RestObject serverData = dataSnapshot.getValue( mRestClazz );
		long count = mSentReqIds.where( RestPendingObject.class )
								.equalTo(
										"reqId",
										serverData.getReqId()
								)
								.count();
		int             status = count == 0 ? RestObjectProxy.SYNCED : RestObjectProxy.NOT_SYNCED;
		RestObjectProxy proxy  = serverData.createProxy();
		proxy.setStatus( status );
		boolean find = false;

		//TO ENSURE NO-DUPLICATED REQUESTS.
		if( getProxyPool() != null ) {
			for( RestObjectProxy polled : getProxyPool() ) {
				if( TextUtils.equals(
						polled.getReqId(),
						proxy.getReqId()
				) ) {
					find = true;
					break;
				}
			}
		}
		if( !find ) {
			if( getProxyPool() != null ) {
				getProxyPool().add( proxy );
			}
			EventBus.getDefault()
					.post( new RestObjectAddedEvent( getId() ) );
		}
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
