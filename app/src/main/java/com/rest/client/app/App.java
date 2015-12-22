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
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.rest.client.app.noactivities.AppGuardService;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientAddedResponse;
import com.rest.client.ds.RequestForResponse;
import com.rest.client.ds.Response;
import com.rest.client.rest.RestApiManager;
import com.rest.client.rest.RestFireManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;


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

	private RestFireManager                              mClientRestFireManager  = new RestFireManager();
	private RestApiManager<Client, ClientAddedResponse>  mClientRestApiManager   = new RestApiManager<>();
	private RestApiManager<RequestForResponse, Response> mResponseRestApiManager = new RestApiManager<>();

	@Override
	public void onCreate() {
		super.onCreate();
		RealmConfiguration config = new RealmConfiguration.Builder( this ).build();
		Realm.setDefaultConfiguration( config );

		startAppGuardService( this );
		mClientRestFireManager.init(
				0,
				this
		);
		mResponseRestApiManager.init(
				1
		);
		mClientRestApiManager.init(
				2
		);
	}

	public RestFireManager getClientRestFireManager() {
		return mClientRestFireManager;
	}

	public RestApiManager<Client, ClientAddedResponse> getClientRestApiManager() {
		return mClientRestApiManager;
	}

	public RestApiManager<RequestForResponse, Response> getResponseRestApiManager() {
		return mResponseRestApiManager;
	}

	public static void startAppGuardService( Context cxt ) {
		long scheduleSec = 60 * 2;
		//long   scheduleSec = 10800L;
		long   flexSecs = 60L;
		String tag      = System.currentTimeMillis() + "";
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
	}

}
