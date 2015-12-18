package com.rest.client.rest;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.rest.client.rest.events.RestApiResponseArrivalEvent;
import com.rest.client.rest.events.RestConnectEvent;
import com.rest.client.rest.events.RestObjectAddedEvent;
import com.rest.client.rest.events.UpdateNetworkStatus;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
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
	private Realm   mDB;
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

	private static final Gson GSON = new Gson();
	/**
	 * Meta type of the temp pending object to queue to represent a posted object.
	 */
	private Class<? extends RealmObject> mPendingClazz;
	/**
	 * {@code true} if history will be returned when network results in no-response.
	 */
	private boolean                      mUseHistory;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RestApiResponseArrivalEvent}.
	 *
	 * @param e
	 * 		Event {@link RestApiResponseArrivalEvent}.
	 */
	@Subscribe
	public void onEventMainThread( RestApiResponseArrivalEvent e ) {

	}

	/**
	 * Handler for {@link RestObjectAddedEvent}.
	 *
	 * @param e
	 * 		Event {@link RestObjectAddedEvent}.
	 */
	@Subscribe
	public void onEventMainThread( RestObjectAddedEvent e ) {

	}

	/**
	 * Handler for {@link UpdateNetworkStatus}.
	 *
	 * @param e
	 * 		Event {@link UpdateNetworkStatus}.
	 */
	@Subscribe
	public void onEventMainThread( UpdateNetworkStatus e ) {
		setConnected( e.isConnected() );
		EventBus.getDefault()
				.post( new RestConnectEvent( getId() ) );
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
		if( !EventBus.getDefault()
					 .isRegistered( this ) ) {
			EventBus.getDefault()
					.register( this );
		}
		mConnected = RestUtils.isNetworkAvailable( app );
		setProxyPool( proxyPool );
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
	}

	/**
	 * @return {@code true} if history will be returned when network results in no-response.
	 */
	private boolean isUseHistory() {
		return mUseHistory;
	}

	/**
	 * Set {@code true} if history will be returned when network results in no-response.
	 */
	public void setUseHistory( boolean useHistory ) {
		mUseHistory = useHistory;
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
	 * Run a rest request.
	 *
	 * @param call
	 * 		The {@link Call} to the request.
	 * @param requestObject
	 * 		The request data to post on server.
	 * @param pending
	 * 		The temp pending object to queue to represent a posted object.
	 */
	public void exec( Call<SD> call, LD requestObject, RealmObject pending ) {
		mPendingClazz = pending.getClass();
		//TO PENDING QUEUE.
		mDB.beginTransaction();
		Method method;
		try {
			method = pending.getClass()
							.getMethod(
									"setStatus",
									int.class
							);
			method.invoke(
					pending,
					RestObjectProxy.NOT_SYNCED
			);
		} catch( Exception e ) {
			e.printStackTrace();
		}
		mDB.copyToRealm( pending );
		mDB.commitTransaction();

		//CALL API.
		call.enqueue( this );

		//REFRESH APP-CLIENT.
		RestObjectProxy proxy = requestObject.createProxy();
		proxy.setStatus( RestObjectProxy.NOT_SYNCED );
		if( getProxyPool() != null ) {
			getProxyPool().add( proxy );
		}

		EventBus.getDefault()
				.post( new RestObjectAddedEvent( getId() ) );
	}


	/**
	 * Run a rest request.
	 *
	 * @param call
	 * 		The {@link Call} to the request.
	 * @param pendingClazz
	 * 		Meta type of the temp pending object to queue to represent a posted object.
	 */
	public void exec( Call<SD> call, Class<? extends RealmObject> pendingClazz ) {
		mPendingClazz = pendingClazz;
		//CALL API.
		call.enqueue( this );
		EventBus.getDefault()
				.post( new RestObjectAddedEvent( getId() ) );
	}

	@Override
	public void onResponse( Response<SD> response, Retrofit retrofit ) {
		RestObject serverData = response.body();

		if( isUseHistory() ) {
			//ADD TO HISTORY
			mDB.beginTransaction();
			String json = GSON.toJson( serverData );
			Log.d(
					getClass().getSimpleName(),
					"onResponse: " + json
			);

			RestHistory history = new RestHistory();
			history.setName( getClass().getSimpleName() + "_" + getId() );
			history.setJson( json );
			history.setType( serverData.getClass()
									   .getName() );
			mDB.copyToRealmOrUpdate( history );
			mDB.commitTransaction();
		}


		RealmResults<? extends RealmObject> pendingObjects = mDB.where( mPendingClazz )
																.equalTo(
																		"reqId",
																		serverData.getReqId()
																)
																.findAll();
		if( pendingObjects.size() > 0 ) {
			RealmObject pendingObject = pendingObjects.get( 0 );
			mDB.beginTransaction();
			pendingObject.removeFromRealm();
			mDB.commitTransaction();
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
									proxy,
									false
							) );
					break;
				}
				i++;
			}
		}

	}

	@Override
	public void onFailure( Throwable t ) {
		Log.d(
				getClass().getSimpleName(),
				"onFailure: " + t.toString()

		);


		RealmResults<RestHistory> historyList = mDB.where( RestHistory.class )
												   .equalTo(
														   "name",
														   getClass().getSimpleName() + "_" + getId()
												   )
												   .findAll();

		if( historyList.size() > 0 ) {
			RestHistory history = historyList.get( 0 );
			if( !TextUtils.isEmpty( history.getJson() ) ) {
				try {
					SD serverData = (SD) GSON.fromJson(
							history.getJson(),
							getClass().getClassLoader()
									  .loadClass( history.getType() )
					);
					RestObjectProxy proxy = serverData.createProxy();
					proxy.setStatus( RestObjectProxy.SYNCED );
					EventBus.getDefault()
							.post( new RestApiResponseArrivalEvent(
									getId(),
									0,
									proxy,
									true
							) );
				} catch( ClassNotFoundException e ) {
					e.printStackTrace();
				}
			} else {
				EventBus.getDefault()
						.post( new RestApiResponseArrivalEvent(
								getId(),
								0,
								null,
								false
						) );
			}
		} else {
			EventBus.getDefault()
					.post( new RestApiResponseArrivalEvent(
							getId(),
							0,
							null,
							false
					) );
		}
	}
}
