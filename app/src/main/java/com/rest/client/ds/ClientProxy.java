package com.rest.client.ds;


public final class ClientProxy  extends Client{
	public static final int NOT_SYNCED = 0;
	public static final int SYNCED     = 1;

	private Client mClient;
	private int    mStatus;

	public ClientProxy( Client client, int status ) {
		mClient = client;
		mStatus = status;
	}


	public String getReqId() {
		return mClient.getReqId();
	}

	public long getReqTime() {
		return mClient.getReqTime();
	}

	public String getComment() {
		return mClient.getComment();
	}

	public int getStatus() {
		return mStatus;
	}


	public void setStatus( int status ) {
		mStatus = status;
	}

	public Client getClient() {
		return mClient;
	}
}
