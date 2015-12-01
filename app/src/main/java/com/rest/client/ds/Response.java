package com.rest.client.ds;


import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Response {

	@SerializedName("status")
	private int mStatus;


	@SerializedName("result")
	private List<Client> mResult;


	public void setStatus( int mStatus ) {
		this.mStatus = mStatus;
	}
	public void setResult( List<Client> mResult ) {
		this.mResult = mResult;
	}
	public int getStatus() {
		return mStatus;
	}
	public List<Client> getResult() {
		return mResult;
	}
}
