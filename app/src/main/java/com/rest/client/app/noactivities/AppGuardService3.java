package com.rest.client.app.noactivities;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.RealmObject;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public final class AppGuardService3 extends GcmTaskService {
	private static final String   TAG           = "AppGuardService3";
	private static final Gson     GSON          = new GsonBuilder().setDateFormat( "yyyy-M-d" )
																   .setExclusionStrategies( new ExclusionStrategy() {
																	   @Override
																	   public boolean shouldSkipField( FieldAttributes f
																	   ) {
																		   return f.getDeclaringClass()
																				   .equals( RealmObject.class );
																	   }

																	   @Override
																	   public boolean shouldSkipClass( Class<?> clazz
																	   ) {
																		   return false;
																	   }
																   } )
																   .create();
	public static        Retrofit RetrofitPhoto = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create( GSON ) )
																		.baseUrl( "http://nasa-photo-dev3.appspot.com/" )
																		.build();

	public AppGuardService3() {
		super();

	}


	@Override
	public int onRunTask( TaskParams taskParams ) {
		SharedPreferences svIndexPref = getSharedPreferences(
				"server_index",
				Context.MODE_PRIVATE
		);
		int index = svIndexPref.getInt(
				"index",
				3
		);

		String base = "http://nasa-photo-dev%d.appspot.com";
		switch( index ) {
			case 1:
				RetrofitPhoto = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create( GSON ) )
													  .baseUrl( "http://nasa-photo-dev.appspot.com/" )
													  .build();
				index = 2;
				break;
			case 2:
			case 3:
			case 4:
				RetrofitPhoto = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create( GSON ) )
													  .baseUrl( String.format(
															  base,
															  index
													  ) )
													  .build();
				if( index == 4 ) {
					index = 1;
				} else {
					index++;
				}
				break;
		}
		svIndexPref.edit()
				   .putInt(
						   "index",
						   index
				   );
		svIndexPref.edit()
				   .commit();
		return GcmNetworkManager.RESULT_SUCCESS;
	}
}
