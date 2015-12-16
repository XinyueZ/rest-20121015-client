package com.rest.client.api;

import com.rest.client.ds.Client;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface Api {
	@POST("/insert")
	Call<Client> getResponse( @Body Client client);
}