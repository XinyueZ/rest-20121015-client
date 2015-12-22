package com.rest.client.rest;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;


public final class RestUtils {

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

	public static void executePending( ExecutePending exp ) {
		Realm db = Realm.getDefaultInstance();
		RealmResults<? extends RealmObject> notSyncItems = db.where( exp.getPending()
																		.DBType() )
															 .equalTo(
																	 "status",
																	 RestObject.NOT_SYNCED
															 )
															 .findAll();
		List<RestObject> restObjects = new ArrayList<>();
		for( RealmObject item : notSyncItems ) {
			restObjects.add( exp.getPending()
								.fromDB( item ) );
		}
		exp.executePending( restObjects );
	}
}
