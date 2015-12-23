package com.rest.client.rest;

import android.util.Log;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Architecture for working with Retrofit.
 *
 * @author Xinyue Zhao
 */
public class RestApiManager {
	/**
	 * The id of manger.
	 */
	private long mId;


	/**
	 * Initialize the manager.
	 */

	public void onCreated() {
		setId( java.lang.System.currentTimeMillis() );
	}

	/**
	 * Set the id of manger.
	 */
	private void setId( long id ) {
		mId = id;
	}

	/**
	 * Run a rest request.
	 *
	 * @param call
	 * 		The {@link Call} to the request.
	 * @param requestObject
	 * 		The request data to post on server.
	 */
	public <LD extends RestObject, SD extends RestObject> void exec( Call<SD> call, LD requestObject ) {
		//MAKE A LOCAL STATUS.
		requestObject.updateDB( RestObject.NOT_SYNCED );
		//CALL API.
		call.enqueue( new Callback<SD>() {
			@Override
			public void onResponse( Response<SD> response, Retrofit retrofit ) {
				//-------------------------
				//THE REQUEST IS SUCCESS.
				//-------------------------
				RestObject serverData = response.body();
				//UPDATE LOCAL STATUS.
				serverData.updateDB( RestObject.SYNCED );
			}

			@Override
			public void onFailure( Throwable t ) {
				Log.d(
						getClass().getSimpleName(),
						"onFailure: " + t.toString()

				);
			}
		} );
	}

	public void executePending( ExecutePending exp ) {
		RestUtils.executePending( exp );
	}
}
