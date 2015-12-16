package com.rest.client.ds;


import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.rest.client.rest.RestObject;
import com.rest.client.rest.RestObjectProxy;

public final class Response implements RestObject {
	@SerializedName("reqId")
	private String mReqId;

	@SerializedName("status")
	private int mStatus;


	@SerializedName("result")
	private List<Client> mResult;

	public Response() {
	}

	public Response( String reqId, int status, List<Client> result ) {
		mReqId = reqId;
		mStatus = status;
		mResult = result;
	}

	public int getStatus() {
		return mStatus;
	}

	public List<Client> getResult() {
		return mResult;
	}

	@Override
	public String getReqId() {
		return mReqId;
	}


	@Override
	public long getReqTime() {
		return 0;
	}

	@Override
	public RestObjectProxy createProxy() {
		return new ResponseProxy( this );
	}
}
