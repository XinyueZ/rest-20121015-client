package com.rest.client;


import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import android.os.Build;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.rest.client.api.Api;
import com.rest.client.ds.Client;
import com.rest.client.ds.Response;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public final class AppGuardService extends GcmTaskService {
	private static final String TAG = "AppGuardService";

	private Api mApi;


	public AppGuardService() {
		super();
		Log.i( TAG, "AppGuardService::ctro" );
		Retrofit retrofit = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create() )
												  .baseUrl( "http://rest-20121015.appspot.com/" )
												  .build();
		mApi = retrofit.create( Api.class );
	}

	@Override
	public void onInitializeTasks() {
		super.onInitializeTasks();
		Log.i( TAG, "onInitializeTasks" );
	}

	@Override
	public int onRunTask( TaskParams taskParams ) {
		Log.i( TAG, "onRunTask: Call by API." );
		String uuid = UUID.randomUUID()
						  .toString();
		long           time     = System.currentTimeMillis();
		String         comment  = Build.MODEL + "---" + random();
		Client         client   = new Client( uuid, time, comment );
		Call<Response> response = mApi.getResponse( client );
		try {
			response.execute();
		} catch( IOException e ) {
			//Ignore.
		}
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
