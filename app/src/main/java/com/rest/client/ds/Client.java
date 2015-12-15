package com.rest.client.ds;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.rest.client.rest.RestObject;
import com.rest.client.rest.RestObjectProxy;

public class Client implements RestObject {

	@SerializedName("reqId")
	@JsonProperty("reqId")
	private String mReqId;
	@SerializedName("reqTime")
	@JsonProperty("reqTime")
	private long   mReqTime;
	@SerializedName("comment")
	@JsonProperty("comment")
	private String mComment;

	public Client() {
	}

	public Client( String reqId, long reqTime, String comment ) {
		mReqId = reqId;
		mReqTime = reqTime;
		mComment = comment;
	}

	public String getReqId() {
		return mReqId;
	}

	public long getReqTime() {
		return mReqTime;
	}

	public String getComment() {
		return mComment;
	}


	@Override
	public RestObjectProxy createProxy( RestObject restObject ) {
		return new ClientProxy( restObject );
	}
}
