package com.rest.client.app.activities;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rest.client.R;
import com.rest.client.app.adapters.ListAdapter;
import com.rest.client.app.fragments.EditCommitDialogFragment2;
import com.rest.client.databinding.MainBinding;
import com.rest.client.ds.ClientDB;
import com.rest.client.rest.activities.RestfulActivity;

import io.realm.RealmObject;

public abstract class BaseActivity extends RestfulActivity {
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


	private void initFAB() {
		mBinding.fab.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				EditCommitDialogFragment2.newInstance( BaseActivity.this )
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

	//-------------------------------------------------------------------------
	@Override
	protected void initDataBinding() {
		mBinding = DataBindingUtil.setContentView(
				this,
				LAYOUT
		);
		setSupportActionBar( mBinding.toolbar );

		mSnackbar = Snackbar.make(
				mBinding.rootView,
				"Getting client list...",
				Snackbar.LENGTH_INDEFINITE
		);
	}


	@Override
	protected void initRestUI() {
		mBinding.loadingPb.setVisibility( View.VISIBLE );
		mBinding.responsesRv.setLayoutManager( new LinearLayoutManager( this ) );
		super.initRestUI();
	}


	@Override
	protected void buildRestUI() {
		mBinding.contentSrl.setRefreshing( false );
		if( isDataLoaded() ) {
			if( mBinding.getAdapter() == null ) {
				mBinding.setAdapter( new ListAdapter<ClientDB>() );
			}
			if( mBinding.getAdapter()
						.getData() == null ) {
				mBinding.getAdapter()
						.setData( getData() );
			}
			mBinding.getAdapter()
					.notifyDataSetChanged();
			if( mSnackbar != null && mSnackbar.isShown() ) {
				mSnackbar.dismiss();
			}
		}
	}

	protected Class<? extends RealmObject> getDataClazz() {
		return ClientDB.class;
	}

	//-------------------------------------------------------------------------

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		initFAB();
		mBinding.contentSrl.setColorSchemeResources(
				R.color.color_pocket_1,
				R.color.color_pocket_2,
				R.color.color_pocket_3,
				R.color.color_pocket_4
		);
		mBinding.contentSrl.setOnRefreshListener( new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadList();
			}
		} );
	}
}
