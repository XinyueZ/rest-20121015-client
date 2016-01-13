package com.rest.client.app.activities;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chopping.activities.RestfulActivity;
import com.chopping.rest.ExecutePending;
import com.chopping.rest.RestObject;
import com.rest.client.R;
import com.rest.client.api.Api;
import com.rest.client.app.App;
import com.rest.client.app.adapters.PhotoListAdapter;
import com.rest.client.app.fragments.YearMonthDialogFragment;
import com.rest.client.bus.SelectYearMonthEvent;
import com.rest.client.databinding.PhotosBinding;
import com.rest.client.ds.PhotoDB;
import com.rest.client.ds.RequestPhotoList;

import io.realm.RealmObject;
import io.realm.RealmQuery;


public class PhotoCalendarActivity extends RestfulActivity {


	/**
	 * Data-binding.
	 */
	private PhotosBinding mBinding;
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_photos;
	/**
	 * Message holder.
	 */
	private Snackbar mSnackbar;

	private String mKeyword = null;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link SelectYearMonthEvent}.
	 *
	 * @param e
	 * 		Event {@link SelectYearMonthEvent}.
	 */
	public void onEvent( SelectYearMonthEvent e ) {
		mKeyword = String.format(
				"%d-%d-",
				e.getYear(),
				e.getMonth()
		);

		mBinding.getAdapter()
				.setData( null );
		initRestUI();
		loadPhotoList(
				e.getYear(),
				e.getMonth(),
				Calendar.getInstance().getTimeZone()
						.getID()
		);
	}

	//------------------------------------------------

	/**
	 * Show single instance of {@link PhotoCalendarActivity}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance( Activity cxt ) {
		Intent intent = new Intent(
				cxt,
				PhotoCalendarActivity.class
		);
		intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP );
		ActivityCompat.startActivity(
				cxt,
				intent,
				null
		);
	}


	@Override
	protected void loadList() {
		Calendar calendar = Calendar.getInstance();
		int      year     = calendar.get( Calendar.YEAR );
		int      month    = calendar.get( Calendar.MONTH ) + 1;
		String timeZone = calendar.getTimeZone()
							   .getID();

		loadPhotoList(
				year,
				month,
				timeZone
		);
	}

	private void loadPhotoList( int year, int month, String timeZone ) {
		RequestPhotoList requestPhotoList = new RequestPhotoList();
		requestPhotoList.setReqId( UUID.randomUUID()
									   .toString() );
		requestPhotoList.setYear( year );
		requestPhotoList.setMonth( month );
		requestPhotoList.setTimeZone( timeZone );
		App.Instance.getApiManager()
					.execAsync(
							Api.RetrofitPhoto.create( Api.class )
											 .getPhotoMonthList( requestPhotoList ),
							requestPhotoList
					);
	}


	@Override
	protected void sendPending() {
		App.Instance.getApiManager()
					.executePending(
							new ExecutePending() {
								@Override
								public void executePending( List<RestObject> pendingItems ) {
									for( RestObject object : pendingItems ) {
										RequestPhotoList requestPhotoList = (RequestPhotoList) object;
										App.Instance.getApiManager()
													.execAsync(
															Api.RetrofitPhoto.create( Api.class )
																			 .getPhotoMonthList( requestPhotoList ),
															requestPhotoList
													);
									}
								}

								@Override
								public RestObject build() {
									return new RequestPhotoList();
								}
							},
							RestObject.NOT_SYNCED
					);
	}


	@Override
	protected void initDataBinding() {
		mBinding = DataBindingUtil.setContentView(
				this,
				LAYOUT
		);
		setSupportActionBar( mBinding.toolbar );

		mSnackbar = Snackbar.make(
				mBinding.rootView,
				"Getting photos ...",
				Snackbar.LENGTH_INDEFINITE
		);
		mSnackbar.show();
	}


	@Override
	protected void initRestUI() {
		mBinding.loadingPb.setVisibility( View.VISIBLE );
		mBinding.responsesRv.setLayoutManager( new LinearLayoutManager( this ) );
		super.initRestUI();
	}


	@Override
	protected void buildQuery( RealmQuery<? extends RealmObject> q ) {
		if( !TextUtils.isEmpty( mKeyword ) ) {
			q.contains(
					"date",
					mKeyword
			);
		}
	}

	@Override
	protected void buildRestUI() {
		if( isDataLoaded() ) {
			if( mBinding.getAdapter() == null ) {
				mBinding.setAdapter( new PhotoListAdapter() );
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
		return PhotoDB.class;
	}

	//onNetworkConnected() ignored.

	@Override
	protected void onRestApiSuccess() {
		mBinding.contentSrl.setRefreshing( false );
		mBinding.loadingPb.setVisibility( View.GONE );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate(
				R.menu.menu_photo_calendar,
				menu
		);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		int id = item.getItemId();
		switch( id ) {
			case R.id.action_fire_example:
				MainActivity.showInstance( this );
				return true;

			case R.id.action_api_example:
				MainActivity2.showInstance( this );
				return true;

			case R.id.action_list_photos:
				PhotosActivity.showInstance( this );
				return true;
			case R.id.action_search:
				YearMonthDialogFragment.newInstance( this )
									   .show(
											   getSupportFragmentManager(),
											   null
									   );
				return true;
		}

		return super.onOptionsItemSelected( item );
	}


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Calendar calendar = Calendar.getInstance();
		int      year     = calendar.get( Calendar.YEAR );
		int      month    = calendar.get( Calendar.MONTH ) + 1;
		mKeyword = String.format(
				"%d-%d-",
				year,
				month
		);
		super.onCreate( savedInstanceState );
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
