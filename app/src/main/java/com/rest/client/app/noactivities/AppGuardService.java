package com.rest.client.app.noactivities;


import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.rest.client.ds.Client;
import com.rest.client.rest.RestFireManager;
import com.rest.client.rest.RestObjectProxy;

public final class AppGuardService extends GcmTaskService {
	private static final String          TAG                        = "AppGuardService";
	private              RestFireManager  mBackgroundRestFireManager ;

	public AppGuardService() {
		super();
		Log.i(
				TAG,
				"AppGuardService::ctro"
		);

	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		mBackgroundRestFireManager = new RestFireManager(Client.class);
		mBackgroundRestFireManager.init(
				0,
				getApplication()
		);
		mBackgroundRestFireManager.install(
				getApplication(),
				new ArrayList<RestObjectProxy>()
		);
		return super.onStartCommand(
				intent,
				flags,
				startId
		);
	}

	@Override
	public void onDestroy() {
		mBackgroundRestFireManager.uninstall();
		super.onDestroy();
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
		mBackgroundRestFireManager.save( client );
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
