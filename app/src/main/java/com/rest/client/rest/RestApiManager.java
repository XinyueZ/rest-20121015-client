package com.rest.client.rest;

import java.util.List;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.rest.client.rest.events.RestApiResponseArrivalEvent;
import com.rest.client.rest.events.RestObjectAddedEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Architecture for working with Retrofit.
 *
 * @param <LD>
 * 		"Local data". Meta class of local data that will be posted on server.
 * @param <SD>
 * 		"Server data". Meta class of server data that will be returned from server.
 *
 * @author Xinyue Zhao
 */
public class RestApiManager<LD extends RestObject, SD extends RestObject> implements Callback<SD> {
	/**
	 * Local storage for storing pending queue and back-off data.
	 */
	private Realm   mSentReqIds;
	/**
	 * The network connect status.
	 */
	private boolean mConnected;
	/**
	 * Pool to hold data posted for Retrofit.
	 */
	private
	@Nullable
	List<RestObjectProxy> mProxyPool;


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RestApiResponseArrivalEvent}.
	 * @param e Event {@link RestApiResponseArrivalEvent}.
	 */
	public void onEvent(RestApiResponseArrivalEvent e) {

	}

	/**
	 * Handler for {@link RestObjectAddedEvent}.
	 *
	 * @param e
	 * 		Event {@link RestObjectAddedEvent}.
	 */
	public void onEvent( RestObjectAddedEvent e ) {

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
	}


	/**
	 * Setup the manager on UI.
	 *
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 * @param proxyPool
	 * 		Pool to hold data from server.
	 */
	public void install( Application app, List<RestObjectProxy> proxyPool ) {
		mConnected = RestUtils.isNetworkAvailable( app );
		setProxyPool( proxyPool );
	}

	/**
	 * Remove the manager from UI.
	 */
	public void uninstall() {

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
	 * Remove all posted pending objects.
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
	 * Set pool to hold data posted for Retrofit.
	 */
	public void setProxyPool( @Nullable List<RestObjectProxy> proxyPool ) {
		mProxyPool = proxyPool;
	}

	/**
	 * Get pool to hold data posted for Retrofit.
	 */
	@Nullable
	public List<RestObjectProxy> getProxyPool() {
		return mProxyPool;
	}

	/**
	 * Meta type of the temp pending object to queue to represent a posted object.
	 */
	private Class<? extends RealmObject> mPendingClazz;
	/**
	 * Run a rest request.
	 *
	 * @param call
	 * 		The {@link Call} to the request.
	 * @param restObject
	 * 		The request data to post on server.
	 * @param pending
	 * 		The temp pending object to queue to represent a posted object.
	 */
	public void exec( Call<SD> call, LD restObject, RealmObject pending ) {
		mPendingClazz = pending.getClass();

		//TO PENDING QUEUE.
		mSentReqIds.beginTransaction();
		mSentReqIds.copyToRealm( pending );
		mSentReqIds.commitTransaction();

		//CALL API.
		call.enqueue( this );

		//REFRESH APP-CLIENT.
		RestObjectProxy proxy = restObject.createProxy();
		proxy.setStatus( RestObjectProxy.NOT_SYNCED );
		if( getProxyPool() != null ) {
			getProxyPool().add( proxy );
		}
		EventBus.getDefault()
				.post( new RestObjectAddedEvent( getId() ) );
	}

	@Override
	public void onResponse( Response<SD> response, Retrofit retrofit ) {
		RestObject serverData = response.body();
		RealmResults<? extends RealmObject> pendingObjects = mSentReqIds.where( mPendingClazz )
																	.equalTo(
																			"reqId",
																			serverData.getReqId()
																	)
																	.findAll();
		if( pendingObjects.size() > 0 ) {
			mSentReqIds.beginTransaction();
			pendingObjects.get( 0 )
						  .removeFromRealm();
			mSentReqIds.commitTransaction();
		}


		if( getProxyPool() != null ) {
			int i = 0;
			for( RestObjectProxy polled : getProxyPool() ) {
				if( TextUtils.equals(
						polled.getReqId(),
						serverData.getReqId()
				) ) {
					//UPDATE LOCAL STATUS OF POSTED DATA.
					polled.setStatus( RestObjectProxy.SYNCED );
					//GIVE SERVER DATA TO APP-CLIENT.
					RestObjectProxy proxy = serverData.createProxy();
					proxy.setStatus( RestObjectProxy.SYNCED );
					EventBus.getDefault()
							.post( new RestApiResponseArrivalEvent(
									getId(),
									i,
									proxy
							) );
					break;
				}
				i++;
			}
		}

	}

	@Override
	public void onFailure( Throwable t ) {
		Log.e(
				getClass().getSimpleName(),
				"onFailure: " + t.toString()

		);
	}
}
