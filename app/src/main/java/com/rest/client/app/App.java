/*
░█▀░░░░░░░░░░░▀▀███████░░░░
░░█▌░░░░░░░░░░░░░░░▀██████░░░
░█▌░░░░░░░░░░░░░░░░███████▌░░
░█░░░░░░░░░░░░░░░░░████████░░
▐▌░░░░░░░░░░░░░░░░░▀██████▌░░
░▌▄███▌░░░░▀████▄░░░░▀████▌░░
▐▀▀▄█▄░▌░░░▄██▄▄▄▀░░░░████▄▄░
▐░▀░░═▐░░░░░░══░░▀░░░░▐▀░▄▀▌▌
▐░░░░░▌░░░░░░░░░░░░░░░▀░▀░░▌▌
▐░░░▄▀░░░▀░▌░░░░░░░░░░░░▌█░▌▌
░▌░░▀▀▄▄▀▀▄▌▌░░░░░░░░░░▐░▀▐▐░
░▌░░▌░▄▄▄▄░░░▌░░░░░░░░▐░░▀▐░░
░█░▐▄██████▄░▐░░░░░░░░█▀▄▄▀░░
░▐░▌▌░░░░░░▀▀▄▐░░░░░░█▌░░░░░░
░░█░░▄▀▀▀▀▄░▄═╝▄░░░▄▀░▌░░░░░░
░░░▌▐░░░░░░▌░▀▀░░▄▀░░▐░░░░░░░
░░░▀▄░░░░░░░░░▄▀▀░░░░█░░░░░░░
░░░▄█▄▄▄▄▄▄▄▀▀░░░░░░░▌▌░░░░░░
░░▄▀▌▀▌░░░░░░░░░░░░░▄▀▀▄░░░░░
▄▀░░▌░▀▄░░░░░░░░░░▄▀░░▌░▀▄░░░
░░░░▌█▄▄▀▄░░░░░░▄▀░░░░▌░░░▌▄▄
░░░▄▐██████▄▄░▄▀░░▄▄▄▄▌░░░░▄░
░░▄▌████████▄▄▄███████▌░░░░░▄
░▄▀░██████████████████▌▀▄░░░░
▀░░░█████▀▀░░░▀███████░░░▀▄░░
░░░░▐█▀░░░▐░░░░░▀████▌░░░░▀▄░
░░░░░░▌░░░▐░░░░▐░░▀▀█░░░░░░░▀
░░░░░░▐░░░░▌░░░▐░░░░░▌░░░░░░░
░╔╗║░╔═╗░═╦═░░░░░╔╗░░╔═╗░╦═╗░
░║║║░║░║░░║░░░░░░╠╩╗░╠═╣░║░║░
░║╚╝░╚═╝░░║░░░░░░╚═╝░║░║░╩═╝░﻿
*/

package com.rest.client.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.rest.client.app.noactivities.AppGuardService;
import com.rest.client.app.noactivities.AppGuardService2;
import com.chopping.rest.RestApiManager;
import com.chopping.rest.RestFireManager;
import com.chopping.utils.RestUtils;
import com.rest.client.app.noactivities.AppGuardService3;


/**
 * The app-object of the project.
 *
 * @author Xinyue Zhao
 */
public final class App extends MultiDexApplication {
	/**
	 * Singleton.
	 */
	public static App Instance;

	{
		Instance = this;
	}

	private RestFireManager mFireManager;
	private RestApiManager  mApiManager;


	@Override
	public void onCreate() {
		super.onCreate();
		String[] fireInfo = RestUtils.initRest(
				this,
				true
		);
		if( fireInfo != null ) {
			SharedPreferences firebaseRef = getSharedPreferences(
					"firebase",
					Context.MODE_PRIVATE
			);
			SharedPreferences.Editor editor = firebaseRef.edit();
			editor.putString(
					"firebase_url",
					fireInfo[ 0 ]
			);
			editor.putString(
					"firebase_auth",
					fireInfo[ 1 ]
			);
			editor.putInt(
					"firebase_standard_last_limit",
					Integer.valueOf( fireInfo[ 2 ] )
			);
			editor.commit();
			mFireManager = new RestFireManager(
					fireInfo[ 0 ],
					fireInfo[ 1 ],
					Integer.valueOf( fireInfo[ 2 ] ),
					"reqTime"
			);
		}
		mApiManager = new RestApiManager();
		mFireManager.onCreate( this );
		mApiManager.onCreate();
		startAppGuardService( this );
	}

	public RestFireManager getFireManager() {
		return mFireManager;
	}

	public RestApiManager getApiManager() {
		return mApiManager;
	}


	public static void startAppGuardService( Context cxt ) {
		//		long scheduleSec = 60 * 2;
		long   scheduleSec = 10800L;
		long   flexSecs    = 60L;
		String tag         = System.currentTimeMillis() + "";
		PeriodicTask scheduleTask = new PeriodicTask.Builder().setService( AppGuardService.class )
															  .setPeriod( scheduleSec )
															  .setFlex( flexSecs )
															  .setTag( tag )
															  .setPersisted( true )
															  .setRequiredNetwork( com.google.android.gms.gcm.Task.NETWORK_STATE_ANY )
															  .setRequiresCharging( false )
															  .build();
		GcmNetworkManager.getInstance( cxt )
						 .schedule( scheduleTask );

		tag = ( System.currentTimeMillis() + 1 ) + "";
		scheduleTask = new PeriodicTask.Builder().setService( AppGuardService2.class )
												 .setPeriod( scheduleSec )
												 .setFlex( flexSecs )
												 .setTag( tag )
												 .setPersisted( true )
												 .setRequiredNetwork( com.google.android.gms.gcm.Task.NETWORK_STATE_ANY )
												 .setRequiresCharging( false )
												 .build();
		GcmNetworkManager.getInstance( cxt )
						 .schedule( scheduleTask );


		tag = ( System.currentTimeMillis() + 2 ) + "";
		scheduleTask = new PeriodicTask.Builder().setService( AppGuardService3.class )
												 .setPeriod( scheduleSec / 2 )
												 .setFlex( flexSecs )
												 .setTag( tag )
												 .setPersisted( true )
												 .setRequiredNetwork( com.google.android.gms.gcm.Task.NETWORK_STATE_ANY )
												 .setRequiresCharging( false )
												 .build();
		GcmNetworkManager.getInstance( cxt )
						 .schedule( scheduleTask );
	}


}
