package com.rest.client.app.noactivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rest.client.app.App;
import com.rest.client.ds.ClientPending;

import io.realm.Realm;

public class NetworkChangeReceiver extends BroadcastReceiver {
	public NetworkChangeReceiver() {
	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		App.Instance.DB_CONNECTED = isNetworkAvailable( context );
		if( App.Instance.DB_CONNECTED ) {
			Realm realm = Realm.getInstance( App.Instance );
			boolean hasPending = realm.where( ClientPending.class )
									  .count() > 0;
			if( hasPending ) {
				realm.beginTransaction();
				realm.clear( ClientPending.class );
				realm.commitTransaction();
			}
		}
	}

	private boolean isNetworkAvailable( Context cxt ) {
		ConnectivityManager connectivityManager = (ConnectivityManager) cxt.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
