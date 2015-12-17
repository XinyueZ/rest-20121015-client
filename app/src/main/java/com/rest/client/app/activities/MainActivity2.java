package com.rest.client.app.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rest.client.R;
import com.rest.client.api.Api;
import com.rest.client.app.App;
import com.rest.client.app.adapters.ListAdapter;
import com.rest.client.app.fragments.EditCommitDialogFragment2;
import com.rest.client.databinding.MainBinding;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientProxy;
import com.rest.client.ds.Response;
import com.rest.client.ds.ResponseProxy;
import com.rest.client.rest.RestObjectProxy;
import com.rest.client.rest.RestPendingObject;
import com.rest.client.rest.events.RestApiResponseArrivalEvent;
import com.rest.client.rest.events.RestObjectAddedEvent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import retrofit.Call;


public class MainActivity2 extends AppCompatActivity {

	/**
	 * Data-binding.
	 */
	private MainBinding mBinding;
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Message holder.
	 */
	private Snackbar mSnackbar;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RestObjectAddedEvent}.
	 *
	 * @param e
	 * 		Event {@link RestObjectAddedEvent}.
	 */
	@Subscribe
	public void onEvent( RestObjectAddedEvent e ) {
		switch( e.getId() ) {
			case 1:
				mSnackbar = Snackbar.make(
						mBinding.rootView,
						"Getting client list...",
						Snackbar.LENGTH_INDEFINITE
				);
				mSnackbar.show();
				break;
			case 2:
				Collections.sort(
						mBinding.getAdapter()
								.getData(),
						new Comparator<ClientProxy>() {
							@Override
							public int compare( ClientProxy lhs, ClientProxy rhs ) {
								return (int) ( rhs.getReqTime() - lhs.getReqTime() );
							}
						}
				);
				mBinding.getAdapter()
						.notifyDataSetChanged();
				break;
		}
	}


	/**
	 * Handler for {@link RestApiResponseArrivalEvent}.
	 *
	 * @param e
	 * 		Event {@link RestApiResponseArrivalEvent}.
	 */
	@Subscribe
	public void onEvent( RestApiResponseArrivalEvent e ) {
		switch( e.getId() ) {
			case 1:
				if( mSnackbar != null ) {
					mSnackbar.dismiss();
				}
				ResponseProxy restObjectProxy = (ResponseProxy) e.getArrivalRestObjectProxy();
				for( Client client : restObjectProxy.getResult() ) {
					ClientProxy proxy = new ClientProxy( client );
					proxy.setStatus( RestObjectProxy.SYNCED );
					mBinding.getAdapter()
							.addData( proxy );
				}
				mBinding.getAdapter()
						.notifyDataSetChanged();

				App.Instance.getClientRestApiManager()
							.install(
									App.Instance,
									mBinding.getAdapter()
											.getData()

							);
				break;
			case 2:
				mBinding.getAdapter()
						.notifyItemChanged( (int) e.getIndex() );
				break;
		}
	}
	//------------------------------------------------

	/**
	 * Show single instance of {@link MainActivity2}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance( Activity cxt ) {
		Intent intent = new Intent(
				cxt,
				MainActivity2.class
		);
		intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP );
		ActivityCompat.startActivity(
				cxt,
				intent,
				null
		);
	}

	private void initComponents() {
		mBinding = DataBindingUtil.setContentView(
				this,
				LAYOUT
		);
		setSupportActionBar( mBinding.toolbar );
	}


	private void initFAB() {
		mBinding.fab.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				EditCommitDialogFragment2.newInstance( MainActivity2.this )
										 .show(
												 getSupportFragmentManager(),
												 null
										 );
			}
		} );

		mBinding.responsesRv.addOnScrollListener( new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
				float y = ViewCompat.getY( recyclerView );
				if( y < dy ) {
					if( mBinding.fab.isShown() ) {
						mBinding.fab.hide();
					}
				} else {
					if( !mBinding.fab.isShown() ) {
						mBinding.fab.show();
					}
				}
			}

		} );
	}

	private void initList() {
		mBinding.responsesRv.setLayoutManager( new LinearLayoutManager( this ) );
		mBinding.setAdapter( new ListAdapter<ClientProxy>() );
	}


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		initComponents();
		initList();
		initFAB();

		App.Instance.getResponseRestApiManager()
					.install(
							App.Instance,
							new ArrayList<RestObjectProxy>()
					);
	}


	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault()
				.register( this );
		Api api = Api.Retrofit.create( Api.class );
		String uuid = UUID.randomUUID()
						  .toString();
		long   time    = System.currentTimeMillis();
		String comment = "get response";
		Client client = new Client(
				uuid,
				time,
				comment
		);
		Call<Response> responseCall = api.getList( client );
		RestPendingObject pendingObject = new RestPendingObject();
		pendingObject.setReqId( uuid );
		pendingObject.setReqTime( time );
		App.Instance.getResponseRestApiManager()
					.exec(
							responseCall,
							client,
							pendingObject
					);
	}

	@Override
	protected void onPause() {
		EventBus.getDefault()
				.unregister( this );
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		App.Instance.getResponseRestApiManager()
					.uninstall();
		super.onDestroy();
	}
}
