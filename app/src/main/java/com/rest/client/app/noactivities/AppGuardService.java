package com.rest.client.app.noactivities;


import java.util.Random;
import java.util.UUID;

import android.os.Build;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.rest.client.app.App;
import com.rest.client.ds.Client;

public final class AppGuardService extends GcmTaskService {
	private static final String TAG = "AppGuardService";


	public AppGuardService() {
		super();
		Log.i(
				TAG,
				"AppGuardService::ctro"
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
		String uuid = UUID.randomUUID()
						  .toString();
		long   time    = System.currentTimeMillis();
		String comment = Build.MODEL + "---" + random();
		Client client = new Client(
				uuid,
				time,
				comment
		);
		App.Instance.getClientRestManager()
					.save( client );
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
