package com.rest.client.rest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rest.client.rest.events.UpdateNetworkStatus;

import de.greenrobot.event.EventBus;

public class RestNetworkChangeReceiver extends BroadcastReceiver {
	public RestNetworkChangeReceiver() {
	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		EventBus.getDefault()
				.post( new UpdateNetworkStatus( RestUtils.isNetworkAvailable( context ) ) );
	}
}
