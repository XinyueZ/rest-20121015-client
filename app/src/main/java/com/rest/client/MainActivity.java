package com.rest.client;

import java.util.UUID;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.rest.client.api.Api;
import com.rest.client.ds.Client;
import com.rest.client.ds.Response;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

	private Api mApi;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );

		Retrofit retrofit = new Retrofit.Builder().addConverterFactory( GsonConverterFactory.create() ).baseUrl( "http://rest-20121015.appspot.com/" )
				.build();
		mApi = retrofit.create( Api.class );

		FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
		fab.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				final View                 rootView  = findViewById( R.id.root_view );
				final FloatingActionButton fab       = (FloatingActionButton) findViewById( R.id.fab );
				final EditText             commentEt = (EditText) findViewById( R.id.comment_et );
				String                     uuid      = UUID.randomUUID().toString();
				long                       time      = System.currentTimeMillis();
				String                     comment   = Build.MODEL + "---" +  commentEt.getText().toString();
				Client                     client    = new Client( uuid, time, comment );
				Call<Response>             response  = mApi.getResponse( client );
				commentEt.setEnabled( false );
				fab.hide();
				response.enqueue( new Callback<Response>() {
					@Override
					public void onResponse( retrofit.Response<Response> response, Retrofit retrofit ) {
						fab.show();
						commentEt.setEnabled( true );
						Snackbar.make( rootView, "Successfully.", Snackbar.LENGTH_SHORT ).show();
					}
					@Override
					public void onFailure( Throwable t ) {
						fab.show();
						commentEt.setEnabled( true );
						Snackbar.make( rootView, "Failure.", Snackbar.LENGTH_SHORT ).show();
					}
				} );
			}
		} );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if( id == R.id.action_settings ) {
			return true;
		}

		return super.onOptionsItemSelected( item );
	}
}
