package com.rest.client.ds;


public final class Client {

	@com.google.gson.annotations.SerializedName("reqId")
	private String mReqId;
	@com.google.gson.annotations.SerializedName("reqTime")
	private long   mReqTime;
	@com.google.gson.annotations.SerializedName("comment")
	private String mComment;

	public Client( String reqId, long reqTime, String comment ) {
		mReqId = reqId;
		mReqTime = reqTime;
		mComment = comment;
	}

	public void setMReqId( String mReqId ) {
		this.mReqId = mReqId;
	}

	public void setMReqTime( long mReqTime ) {
		this.mReqTime = mReqTime;
	}

	public void setMComment( String mComment ) {
		this.mComment = mComment;
	}

	public String getMReqId() {
		return mReqId;
	}

	public long getMReqTime() {
		return mReqTime;
	}

	public String getMComment() {
		return mComment;
	}
}
