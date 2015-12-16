package com.rest.client.rest;

import java.util.List;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.rest.client.rest.events.RestObjectAddedEvent;
import com.rest.client.rest.events.RestResponseArrivalEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Architecture for working with Retrofit.
 *
 * @author Xinyue Zhao
 */
public class RestApiManager<T extends RestObject> implements Callback<T> {
	/**
	 * Local storage for storing pending queue and back-off data.
	 */
	private Realm   mSentReqIds;
	/**
	 * The network connect status.
	 */
	private boolean mConnected;
	/**
	 * Pool to hold data from Firebase.
	 */
	private
	@Nullable
	List<? extends RestObjectProxy> mProxyPool;

	/**
	 * Initialize the manager.
	 *
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 */

	public void init( Application app  ) {
		mSentReqIds = Realm.getInstance( app );
	}


	/**
	 * Setup the manager on UI.
	 *
	 * @param app
	 * 		{@link Application} The application domain to control manager.
	 * @param proxyPool
	 * 		Pool to hold data from Firebase.
	 */
	public void install( Application app, List<? extends RestObjectProxy> proxyPool ) {
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
	 * Set pool to hold data from Firebase.
	 */
	protected void setProxyPool( @Nullable List<? extends RestObjectProxy> proxyPool ) {
		mProxyPool = proxyPool;
	}

	/**
	 * Get pool to hold data from Firebase.
	 */
	@Nullable
	protected List<? extends RestObjectProxy> getProxyPool() {
		return mProxyPool;
	}


	/**
	 * Run a rest request.
	 *
	 * @param call
	 * 		The {@link Call} to the request.
	 * @param restObject
	 * 		The object to post on sever.
	 */
	public void exec( Call<T> call, RestObject restObject ) {
		//TO PENDING QUEUE.
		mSentReqIds.beginTransaction();
		RestPendingObject restPendingObject = new RestPendingObject();
		restPendingObject.setReqId( restObject.getReqId() );
		mSentReqIds.copyToRealm( restPendingObject );
		mSentReqIds.commitTransaction();

		//CALL API.
		call.enqueue( this );

		//REFRESH APP-CLIENT.
		RestObjectProxy proxy = restObject.createProxy();
		proxy.setStatus( RestObjectProxy.NOT_SYNCED );
		EventBus.getDefault()
				.post( new RestObjectAddedEvent( proxy ) );
	}

	@Override
	public void onResponse( Response<T> response, Retrofit retrofit ) {
		RestObject serverData = response.body();
		RealmResults<RestPendingObject> pendingObjects = mSentReqIds.where( RestPendingObject.class )
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
					polled.setStatus( RestObjectProxy.SYNCED );
					EventBus.getDefault()
							.post( new RestResponseArrivalEvent(i) );
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
