package com.rest.client.api;

import com.rest.client.ds.Client;
import com.rest.client.ds.ClientAddedResponse;
import com.rest.client.ds.ClientDeleteRequest;
import com.rest.client.ds.ClientDeletedResponse;
import com.rest.client.ds.ClientEditRequest;
import com.rest.client.ds.ClientEditedResponse;
import com.rest.client.ds.PhotoList;
import com.rest.client.ds.RequestForResponse;
import com.rest.client.ds.RequestPhotoLastThreeList;
import com.rest.client.ds.RequestPhotoList;
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

	//  Retrofit RetrofitPhoto = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create() )
	//												  .baseUrl( "http://orbital-stage-648.appspot.com/" )
	//												  .build();
	//	Retrofit RetrofitPhoto = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create() )
	//												   .baseUrl( "http://nasa-photo-dev.appspot.com/" )
	//												   .build();
	//	     Retrofit RetrofitPhoto = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create() )
	//												   .baseUrl( "http://nasa-photo-dev2.appspot.com/" )
	//												   .build();

	@POST("/insert")
	Call<ClientAddedResponse> addClient( @Body Client client );

	@POST("/list")
	Call<Response> getList( @Body RequestForResponse client );

	@POST("/delete")
	Call<ClientDeletedResponse> deleteClient( @Body ClientDeleteRequest deleteRequest );

	@POST("/update")
	Call<ClientEditedResponse> updateClient( @Body ClientEditRequest editRequest );

	@POST("/month_list")
	Call<PhotoList> getPhotoMonthList( @Body RequestPhotoList requestPhotoList );

	@POST("/last_three_list")
	Call<PhotoList> getLastThreeList( @Body RequestPhotoLastThreeList requestPhotoList );
}