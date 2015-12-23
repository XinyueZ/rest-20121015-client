package com.rest.client.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Utils class for Rest-package.
 */
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

	/**
	 * Help method to execute pending requests.
	 *
	 * @param exp
	 * 		{@link ExecutePending} to execute pending.
	 */
	public static void executePending( ExecutePending exp ) {
		Realm db = Realm.getDefaultInstance();
		RealmResults<? extends RealmObject> notSyncItems = db.where( exp.build()
																		.DBType() )
															 .equalTo(
																	 "status",
																	 RestObject.NOT_SYNCED
															 )
															 .findAll();
		List<RestObject> restObjects = new ArrayList<>();
		for( RealmObject item : notSyncItems ) {
			restObjects.add( exp.build()
								.fromDB( item ) );
		}
		if( !db.isClosed() ) {
			db.close();
		}
		exp.executePending( restObjects );
	}

	/**
	 * Initialize Rest-package.
	 *
	 * @param app
	 * 		The {@link Application} context.
	 * @param useFirebase
	 * 		{@code true} if this application use Firebase.
	 *
	 * @return An array of strings, first element is the base url to Firebase, second is auth code to it.
	 */
	public static
	@Nullable
	String[] initRest( Application app, boolean useFirebase ) {
		RealmConfiguration config = new RealmConfiguration.Builder( app ).build();
		Realm.setDefaultConfiguration( config );
		if( useFirebase ) {
			Properties  prop  = new Properties();
			InputStream input = null;
			try {
			/*From "resources".*/
				input = app.getClassLoader()
						   .getResourceAsStream( "firebase.properties" );
				if( input != null ) {
					// load a properties file
					prop.load( input );
					String url  = prop.getProperty( "firebase_url" );
					String auth = prop.getProperty( "firebase_auth" );
					return new String[] { url , auth };
				}
			} catch( IOException ex ) {
				ex.printStackTrace();
			} finally {
				if( input != null ) {
					try {
						input.close();
					} catch( IOException e ) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
