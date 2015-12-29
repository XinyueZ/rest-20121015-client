package com.rest.client.app.noactivities;


import java.util.Random;
import java.util.UUID;

import android.os.Build;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.rest.client.api.Api;
import com.rest.client.ds.Client;
import com.chopping.rest.RestApiManager;

public final class AppGuardService2 extends GcmTaskService {
	private static final String TAG = "AppGuardService2";

	public AppGuardService2() {
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
		RestApiManager mgr = new RestApiManager();
		mgr.onCreate();
		Client client = new Client();
		client.setReqId( UUID.randomUUID()
							 .toString() );
		client.setReqTime( System.currentTimeMillis() );
		client.setComment( Build.MODEL + "--Bk--API---" + random() );
		mgr.execSync(
				Api.Retrofit.create( Api.class )
							.addClient( client ),
				client
		);
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
