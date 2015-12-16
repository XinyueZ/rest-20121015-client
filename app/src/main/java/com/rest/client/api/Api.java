package com.rest.client.api;

import com.rest.client.ds.Client;
import com.rest.client.ds.Response;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.POST;

public interface Api {

	Retrofit Retrofit = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create() )
										.baseUrl( "http://rest-20121015.appspot.com/" )
										.build();

	@POST("/insert")
	Call<Client> insertClient( @Body Client client );

	@POST("/list")
	Call<Response> getList( @Body Client client );
}