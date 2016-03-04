package com.rest.client.app.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chopping.activities.RestfulActivity;
import com.rest.client.R;
import com.rest.client.api.Api;
import com.rest.client.app.App;
import com.rest.client.app.adapters.PhotoListAdapter;
import com.rest.client.app.fragments.InputDateDialogFragment;
import com.rest.client.app.noactivities.AppGuardService3;
import com.rest.client.bus.SelectDateTime;
import com.rest.client.databinding.PhotosBinding;
import com.rest.client.ds.PhotoDB;
import com.rest.client.ds.RequestPhotoDayList;
import com.rest.client.ds.RequestPhotoLastThreeList;
import com.rest.client.ds.RequestPhotoList;

import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


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

	private int mQueryType;

	private Date mKeyword = null;


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link SelectDateTime}.
	 *
	 * @param e
	 * 		Event {@link SelectDateTime}.
	 */
	public void onEvent( SelectDateTime e ) throws ParseException {
		mQueryType = e.getQueryType();
		switch( mQueryType ) {
			case InputDateDialogFragment.QUERY_DAY:
				mKeyword = new SimpleDateFormat( "yyyy-M-d" ).parse( String.format(
						"%d-%d-%d",
						e.getYear(),
						e.getMonth(),
						e.getDay()
				) );
				break;
			case InputDateDialogFragment.QUERY_SINGLE_MONTH:
				Calendar calendar = Calendar.getInstance();
				calendar.set(
						Calendar.YEAR,
						e.getYear()
				);
				calendar.set(
						Calendar.MONTH,
						e.getMonth() - 1
				);
				int lastDay = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
				calendar.set(
						Calendar.DAY_OF_MONTH,
						lastDay
				);
				mKeyword = calendar.getTime();
				break;
		}


		mBinding.getAdapter()
				.setData( null );
		queryLocalData();
		loadPhotoList(
				e.getYear(),
				e.getMonth(),
				e.getTimeZone()
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
		switch( mQueryType ) {
			case InputDateDialogFragment.QUERY_DAY:
				RequestPhotoDayList requestPhotoDayList = new RequestPhotoDayList();
				requestPhotoDayList.setReqId( UUID.randomUUID()
												  .toString() );
				List<String> datetimes = new ArrayList<>();
				datetimes.add( new SimpleDateFormat( "yyyy-M-d" ).format( mKeyword ) );
				requestPhotoDayList.setDateTimes( datetimes );
				requestPhotoDayList.setTimeZone( timeZone );
				App.Instance.getApiManager()
							.execAsync(
									AppGuardService3.RetrofitPhoto.create( Api.class )
																  .getPhotoList( requestPhotoDayList ),
									requestPhotoDayList
							);
				break;
			case InputDateDialogFragment.QUERY_SINGLE_MONTH:
				RequestPhotoList requestPhotoList = new RequestPhotoList();
				requestPhotoList.setReqId( UUID.randomUUID()
											   .toString() );
				requestPhotoList.setYear( year );
				requestPhotoList.setMonth( month );
				requestPhotoList.setTimeZone( timeZone );
				App.Instance.getApiManager()
							.execAsync(
									AppGuardService3.RetrofitPhoto.create( Api.class )
																  .getPhotoMonthList( requestPhotoList ),
									requestPhotoList
							);
				break;
			case InputDateDialogFragment.QUERY_LAST_THREE_DAYS:
				RequestPhotoLastThreeList requestPhotoLastThreeList = new RequestPhotoLastThreeList();
				requestPhotoLastThreeList.setReqId( UUID.randomUUID()
														.toString() );
				requestPhotoLastThreeList.setTimeZone( timeZone );
				App.Instance.getApiManager()
							.execAsync(
									AppGuardService3.RetrofitPhoto.create( Api.class )
																  .getPhotoLastThreeList( requestPhotoLastThreeList ),
									requestPhotoLastThreeList
							);
				break;
		}
	}


	@Override
	protected void sendPending() {
		//		App.Instance.getApiManager()
		//					.executePending(
		//							new ExecutePending() {
		//								@Override
		//								public void executePending( List<RestObject> pendingItems ) {
		//									for( RestObject object : pendingItems ) {
		//										RequestPhotoList requestPhotoList = (RequestPhotoList) object;
		//										App.Instance.getApiManager()
		//													.execAsync(
		//															AppGuardService3.RetrofitPhoto.create( Api.class )
		//																						  .getPhotoMonthList( requestPhotoList ),
		//															requestPhotoList
		//													);
		//									}
		//								}
		//
		//								@Override
		//								public RestObject build() {
		//									return new RequestPhotoList();
		//								}
		//							},
		//							RestObject.NOT_SYNCED
		//					);
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
	protected void queryLocalData() {
		mBinding.loadingPb.setVisibility( View.VISIBLE );
		mBinding.responsesRv.setLayoutManager( new LinearLayoutManager( this ) );
		super.queryLocalData();
	}


	@Override
	protected void buildQuery( RealmQuery<? extends RealmObject> q ) {
		switch( mQueryType ) {
			case InputDateDialogFragment.QUERY_DAY:
				if( mKeyword != null ) {
					q.equalTo(
							"date",
							mKeyword
					);
				}
				break;
			case InputDateDialogFragment.QUERY_SINGLE_MONTH:
				if( mKeyword != null ) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(
							Calendar.MINUTE,
							0
					);
					calendar.set(
							Calendar.SECOND,
							0
					);
					calendar.set(
							Calendar.MILLISECOND,
							0
					);
					calendar.setTime( mKeyword );
					int lastDay = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
					calendar.set(
							Calendar.DAY_OF_MONTH,
							lastDay
					);
					Date timeMax = calendar.getTime();
					q.lessThanOrEqualTo(
							"date",
							timeMax
					);
					calendar.set(
							Calendar.DAY_OF_MONTH,
							0
					);
					Date timeMin = calendar.getTime();
					q.greaterThanOrEqualTo(
							"date",
							timeMin
					);
				}
				break;
			case InputDateDialogFragment.QUERY_LAST_THREE_DAYS:
				Calendar calendar = Calendar.getInstance();
				calendar.set(
						Calendar.MINUTE,
						0
				);
				calendar.set(
						Calendar.SECOND,
						0
				);
				calendar.set(
						Calendar.MILLISECOND,
						0
				);
				Date timeMax = calendar.getTime();
				calendar.add(
						Calendar.DAY_OF_MONTH,
						-3
				);
				Date timeMin = calendar.getTime();
				q.lessThanOrEqualTo(
						"date",
						timeMax
				)
				 .greaterThanOrEqualTo(
						 "date",
						 timeMin
				 );
				break;
		}
	}

	@Override
	protected RealmResults<? extends RealmObject> createQuery( RealmQuery<? extends RealmObject> q ) {
		RealmResults<? extends RealmObject> results = q.findAllSortedAsync(
				"date",
				Sort.DESCENDING
		);
		return results;
	}


	@Override
	protected void buildViews() {
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
			case R.id.action_vector_image:
				VectorImageActivity.showInstance( this );
				return true;
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
				InputDateDialogFragment.newInstance( this )
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
		mQueryType = InputDateDialogFragment.QUERY_SINGLE_MONTH;
		Calendar calendar = Calendar.getInstance();
		int      lastDay  = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
		calendar.set(
				Calendar.DAY_OF_MONTH,
				lastDay
		);
		mKeyword = calendar.getTime();

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
		mBinding.fab.hide();
	}

}
