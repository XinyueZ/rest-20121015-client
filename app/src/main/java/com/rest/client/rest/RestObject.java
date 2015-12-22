package com.rest.client.rest;


import android.support.annotation.Nullable;

import io.realm.RealmObject;

public abstract class RestObject {
	public static final int NOT_SYNCED = 0;
	public static final int SYNCED     = 1;

	//Request ID --> must be "reqId" for json/gson/jackson.
	public abstract String getReqId();

	//Time to fire the request --> must be "reqTime" for json/gson/jackson.
	public abstract long getReqTime();

	//Proxy builder.
	public abstract RealmObject updateDB(int status);

	public abstract Class<? extends RealmObject> DBType();

	public  @Nullable
	RestObject fromDB( RealmObject dbItem){
		return null;
	}
}
