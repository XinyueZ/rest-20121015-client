package com.rest.client.rest;

import android.util.Log;

import com.rest.client.ds.ClientDB;
import com.rest.client.rest.events.RestResponseEvent;
import com.rest.client.rest.events.UpdateNetworkStatusEvent;

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
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RestResponseEvent}.
	 *
	 * @param e
	 * 		Event {@link RestResponseEvent}.
	 */
	public void onEventMainThread( RestResponseEvent e ) {

	}


	/**
	 * Handler for {@link UpdateNetworkStatusEvent}.
	 *
	 * @param e
	 * 		Event {@link UpdateNetworkStatusEvent}.
	 */
	public void onEventMainThread( UpdateNetworkStatusEvent e ) {
		if( e.isConnected() ) {
			RealmResults<ClientDB> dbItems = Realm.getDefaultInstance( )
												  .where( ClientDB.class )
												  .equalTo( "status", RestObject.NOT_SYNCED )
												  .findAll();
			//TODO Reconnected and resume to push data...
		}
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
	 */

	public void init( int id ) {
		setId( id );
	}


	/**
	 * Setup the manager on UI.
	 */
	public void install() {
		if( !EventBus.getDefault()
					 .isRegistered( this ) ) {
			EventBus.getDefault()
					.register( this );
		}
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
	 * Run a rest request.
	 *
	 * @param call
	 * 		The {@link Call} to the request.
	 * @param requestObject
	 * 		The request data to post on server.
	 */
	public void exec( Call<SD> call, LD requestObject ) {
		//MAKE A LOCAL STATUS.
		requestObject.updateDB( RestObject.NOT_SYNCED );
		//CALL API.
		call.enqueue( this );
	}


	@Override
	public void onResponse( Response<SD> response, Retrofit retrofit ) {
		//-------------------------
		//THE REQUEST IS SUCCESS.
		//-------------------------
		RestObject serverData = response.body();
		//UPDATE LOCAL STATUS.
		RealmObject dbItem = serverData.updateDB( RestObject.SYNCED );
		EventBus.getDefault()
				.postSticky( new RestResponseEvent(
						getId(),
						dbItem
				) );
	}


	@Override
	public void onFailure( Throwable t ) {
		Log.d(
				getClass().getSimpleName(),
				"onFailure: " + t.toString()

		);
		//---------------------------------
		//THE REQUEST IS NOT SUCCESSFULLY.
		//---------------------------------
		EventBus.getDefault()
				.postSticky( new RestResponseEvent(
						getId(),
						null
				) );
	}


}
