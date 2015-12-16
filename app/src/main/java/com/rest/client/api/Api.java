package com.rest.client.api;

import com.rest.client.ds.Client;
import com.rest.client.ds.Response;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface Api {
	@POST("/insert")
	Call<Response> getResponse( @Body Client client);
}