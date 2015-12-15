package com.rest.client.rest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestNetworkChangeReceiver extends BroadcastReceiver {
	public RestNetworkChangeReceiver() {
	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		RestManager.DB_CONNECTED = RestManager.isNetworkAvailable( context );
	}
}
