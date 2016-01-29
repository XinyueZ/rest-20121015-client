package com.rest.client.app.activities;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chopping.activities.RestfulActivity;
import com.chopping.utils.RestUtils;
import com.rest.client.R;
import com.rest.client.app.adapters.ListAdapter;
import com.rest.client.databinding.MainBinding;
import com.rest.client.ds.ClientDB;
import com.rest.client.ds.RequestForResponseDB;
import com.rest.client.ds.ResponseDB;

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
	//[Begin for detecting scrolling onto bottom]
	private int      mVisibleItemCount;
	private int      mPastVisibleItems;
	private int      mTotalItemCount;
	//[End]

	protected abstract void showCommentDialog();

	protected MainBinding getBinding() {
		return mBinding;
	}

	private void initFAB() {
		mBinding.addFab.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				showCommentDialog();
			}
		} );
		mBinding.responsesRv.addOnScrollListener( new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
				//Calc whether the list has been scrolled on bottom,
				//this lets app to getting next page.
				LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				mVisibleItemCount = linearLayoutManager.getChildCount();
				mTotalItemCount = linearLayoutManager.getItemCount();
				mPastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
				if( ViewCompat.getY( recyclerView ) < dy ) {
					if( isRefreshable() ) {
						if( ( mVisibleItemCount + mPastVisibleItems ) == mTotalItemCount ) {
							if( !mBinding.loadMoreFab.isShown() ) {
								mBinding.loadMoreFab.show();
							}
						}
					}
					if( !mBinding.addFab.isShown() ) {
						mBinding.addFab.show();
					}
				} else {
					if( mBinding.addFab.isShown() ) {
						mBinding.addFab.hide();
					}
					if( isRefreshable() ) {
						if( mBinding.loadMoreFab.isShown() ) {
							mBinding.loadMoreFab.hide();
						}
					}
				}
			}
		} );
	}

	protected boolean isRefreshable() {
		return false;
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
		mSnackbar.show();
	}


	@Override
	protected void queryLocalData() {
		mBinding.loadingPb.setVisibility( View.VISIBLE );
		mBinding.responsesRv.setLayoutManager( new LinearLayoutManager( this ) );
		super.queryLocalData();
	}


	@Override
	protected void buildViews() {
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


	@Override
	protected void onRestApiSuccess() {
		mBinding.contentSrl.setRefreshing( false );
		mBinding.loadingPb.setVisibility( View.GONE );
	}

	//onNetworkConnected() ignored.
	//-------------------------------------------------------------------------

	protected void clearPendings() {
		RestUtils.clearPending( ClientDB.class );
		RestUtils.clearPending( RequestForResponseDB.class );
		RestUtils.clearPending( ResponseDB.class );
	}

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
		mBinding.loadMoreFab.hide();
	}
}
