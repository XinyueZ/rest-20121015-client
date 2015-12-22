package com.rest.client.app.activities;

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
import android.view.View.OnClickListener;

import com.rest.client.R;
import com.rest.client.api.Api;
import com.rest.client.app.App;
import com.rest.client.app.adapters.ListAdapter;
import com.rest.client.app.fragments.EditCommitDialogFragment2;
import com.rest.client.databinding.MainBinding;
import com.rest.client.ds.ClientDB;
import com.rest.client.ds.RequestForResponse;
import com.rest.client.rest.events.RestResponseEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


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
	 * Handler for {@link RestResponseEvent}.
	 *
	 * @param e
	 * 		Event {@link RestResponseEvent}.
	 */
	public void onEventMainThread( RestResponseEvent e ) {
		if( mSnackbar != null && mSnackbar.isShown() ) {
			mSnackbar.dismiss();
		}
		if( mBinding.getAdapter().getItemCount() < 1 ) {
			//UI update for no-data status.
			mBinding.loadingPb.setVisibility( View.INVISIBLE );
			mSnackbar = Snackbar.make(
					mBinding.rootView,
					"No response...",
					Snackbar.LENGTH_INDEFINITE
			)
								.setAction(
										"Reload",
										new OnClickListener() {
											@Override
											public void onClick( View v ) {
												load();
											}
										}
								);
			mSnackbar.show();
		} else {
			mBinding.getAdapter()
					.notifyItemInserted(0);
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
		//Load all data(local).
		final RealmResults<ClientDB> dbItems = Realm.getDefaultInstance()
													.where( ClientDB.class )
													.findAllAsync();
		dbItems.addChangeListener( new RealmChangeListener() {
			@Override
			public void onChange() {
				dbItems.sort(
						"reqTime",
						Sort.DESCENDING
				);
				mBinding.setAdapter(  new ListAdapter<ClientDB>().setData( dbItems ) );
			}
		} );
	}


	private void load() {
		RequestForResponse rfr = new RequestForResponse();
		rfr.setReqId( UUID.randomUUID()
						  .toString() );
		rfr.setReqTime( System.currentTimeMillis() );
		App.Instance.getResponseRestApiManager()
					.exec(
							Api.Retrofit.create( Api.class )
										.getList( rfr ),
							rfr
					);
		if( mSnackbar != null && mSnackbar.isShown() ) {
			mSnackbar.dismiss();
		}
		mSnackbar = Snackbar.make(
				mBinding.rootView,
				"Getting client list...",
				Snackbar.LENGTH_INDEFINITE
		);
		mSnackbar.show();
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		initComponents();
		initList();
		initFAB();
		App.Instance.getResponseRestApiManager()
					.install();
		App.Instance.getClientRestApiManager()
					.install();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault()
				.registerSticky( this );
		load();
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
		App.Instance.getClientRestApiManager()
					.uninstall();
		super.onDestroy();
	}
}