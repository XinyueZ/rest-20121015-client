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
import android.telephony.TelephonyManager;

import com.rest.client.app.App;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

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
	public static boolean isNetworkAvailable( Context cxt ) {
		ConnectivityManager connectivityManager = (ConnectivityManager) cxt.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo         activeNetworkInfo   = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Delete all pending objects that might not be synced.
	 *
	 * @param clazz
	 * 		The meta of object.
	 */
	public static void clearPending( Class<? extends RealmObject> clazz ) {
		Realm db = Realm.getDefaultInstance();
		RealmResults<? extends RealmObject> results = db.where( clazz )
														.equalTo(
																"status",
																RestObject.NOT_SYNCED
														)
														.findAll();
		db.beginTransaction();
		results.clear();
		db.commitTransaction();

		if( !db.isClosed() ) {
			db.close();
		}
	}


	/**
	 * Delete all objects .
	 *
	 * @param clazz
	 * 		The meta of object.
	 */
	public static void clear( Class<? extends RealmObject> clazz ) {
		Realm db = Realm.getDefaultInstance();
		RealmResults<? extends RealmObject> results = db.where( clazz )
														.findAll();
		db.beginTransaction();
		results.clear();
		db.commitTransaction();

		if( !db.isClosed() ) {
			db.close();
		}
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
															 .findAllSorted(
																	 "reqTime",
																	 Sort.ASCENDING
															 );
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
	 * @return An array of strings, first element is the base url to Firebase, second is auth code to it, third is last-limit of data.
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
					String limitLast =  prop.getProperty( "firebase_standard_last_limit" );
					return new String[] { url , auth, limitLast };
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

	/**
	 * A common condition check whether application should load new data or not.
	 *
	 * @param cxt
	 * 		{@link Context}.
	 *
	 * @return {@code true} when the application needs to load local data.
	 */
	public static boolean shouldLoadLocal( Context cxt ) {
		boolean should;
		should = !RestUtils.isNetworkAvailable( cxt );
		should |= getCurrentNetworkType( App.Instance ) != CONNECTION_WIFI;
		return should;
	}

	public static final byte CONNECTION_OFFLINE = 1;
	public static final byte CONNECTION_WIFI    = 2;
	public static final byte CONNECTION_ROAMING = 3;
	public static final byte CONNECTION_SLOW    = 4;
	public static final byte CONNECTION_FAST    = 5;

	/**
	 * Evaluate the current network connection and return the corresponding type, e.g. CONNECTION_WIFI.
	 */
	public static byte getCurrentNetworkType( Context _context ) {
		NetworkInfo netInfo = ( (ConnectivityManager) _context.getSystemService( Context.CONNECTIVITY_SERVICE ) ).getActiveNetworkInfo();

		if( netInfo == null ) {
			return CONNECTION_OFFLINE;
		}

		if( netInfo.getType() == ConnectivityManager.TYPE_WIFI ) {
			return CONNECTION_WIFI;
		}

		if( netInfo.isRoaming() ) {
			return CONNECTION_ROAMING;
		}

		if( !( netInfo.getType() == ConnectivityManager.TYPE_MOBILE &&
			   ( netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS || netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA ||
				 netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA || netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA ||
				 netInfo.getSubtype() == 13 // NETWORK_TYPE_LTE
				 || netInfo.getSubtype() == 15 ) ) ) // NETWORK_TYPE_HSPAP
		{

			return CONNECTION_SLOW;
		}

		return CONNECTION_FAST;
	}
}
