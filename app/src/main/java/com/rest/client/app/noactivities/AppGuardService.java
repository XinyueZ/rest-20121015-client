package com.rest.client.app.noactivities;


import java.util.Random;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.rest.client.ds.Client;
import com.rest.client.rest.RestFireManager;

public final class AppGuardService extends GcmTaskService {
	private static final String TAG = "AppGuardService";

	public AppGuardService() {
		super();
		Log.i(
				TAG,
				"ctro"
		);

	}

	@Override
	public void onInitializeTasks() {
		super.onInitializeTasks();
		Log.i(
				TAG,
				"onInitializeTasks"
		);
	}

	@Override
	public int onRunTask( TaskParams taskParams ) {
		Log.i(
				TAG,
				"onRunTask: Call by API."
		);
		SharedPreferences firebaseRef = getSharedPreferences(
				"firebase",
				Context.MODE_PRIVATE
		);
		RestFireManager mgr = new RestFireManager(
				firebaseRef.getString(
						"firebase_url",
						null
				),
				firebaseRef.getString(
						"firebase_auth",
						null
				),
				firebaseRef.getInt(
						"firebase_standard_last_limit",
						30
				),
				"reqTime"
		);
		mgr.onCreate( getApplication() );
		Client client = new Client();
		client.setReqId( UUID.randomUUID()
							 .toString() );
		client.setReqTime( System.currentTimeMillis() );
		client.setComment( Build.MODEL + "--Bk--Firebase---" + random() );
		mgr.saveInBackground( client );
		return GcmNetworkManager.RESULT_SUCCESS;
	}

	private static String random() {
		Random        generator           = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		int           randomLength        = generator.nextInt( 25 );
		char          tempChar;
		for( int i = 0; i < randomLength; i++ ) {
			tempChar = (char) ( generator.nextInt( 96 ) + 32 );
			randomStringBuilder.append( tempChar );
		}
		return randomStringBuilder.toString();
	}
}
