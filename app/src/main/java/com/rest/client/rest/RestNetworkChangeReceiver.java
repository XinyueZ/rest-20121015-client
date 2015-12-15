package com.rest.client.rest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rest.client.app.App;

public class RestNetworkChangeReceiver extends BroadcastReceiver {
	public RestNetworkChangeReceiver() {
	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		App.Instance.getClientRestManager()
					.setConnected( RestManager.isNetworkAvailable( context ) );
	}
}
