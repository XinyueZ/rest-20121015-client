package com.rest.client.app.activities;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.app.adapters.ListAdapter;
import com.rest.client.app.fragments.EditCommitDialogFragment;
import com.rest.client.databinding.MainBinding;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientDB;
import com.rest.client.rest.ExecutePending;
import com.rest.client.rest.RestObject;
import com.rest.client.rest.RestUtils;
import com.rest.client.rest.events.UpdateNetworkStatusEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity {
	private Realm       mRealm;
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
	 * Handler for {@link UpdateNetworkStatusEvent}.
	 *
	 * @param e
	 * 		Event {@link UpdateNetworkStatusEvent}.
	 */
	public void onEventMainThread( UpdateNetworkStatusEvent e ) {
		if( e.isConnected() ) {
			sendPending();
		}
	}

	//------------------------------------------------

	/**
	 * Show single instance of {@link MainActivity}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance( Activity cxt ) {
		Intent intent = new Intent(
				cxt,
				MainActivity.class
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

		mSnackbar = Snackbar.make(
				mBinding.rootView,
				"Getting client list...",
				Snackbar.LENGTH_INDEFINITE
		);
	}


	private void initFAB() {
		mBinding.fab.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				EditCommitDialogFragment.newInstance( MainActivity.this )
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


	private void initListView() {
		mBinding.loadingPb.setVisibility( View.VISIBLE );
		mBinding.responsesRv.setLayoutManager( new LinearLayoutManager( this ) );
		//Load all data(local).
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(
				Calendar.DAY_OF_MONTH,
				-1
		);
		yesterday.set(
				Calendar.HOUR_OF_DAY,
				23
		);
		yesterday.set(
				Calendar.MINUTE,
				59
		);
		yesterday.set(
				Calendar.SECOND,
				59
		);

		mDBData = mRealm.where( ClientDB.class )
						.greaterThan(
								"reqTime",
								yesterday.getTimeInMillis()
						)
						.findAllSorted(
								"reqTime",
								Sort.DESCENDING
						);
		mDBData.addChangeListener( mListListener );
	}

	private RealmResults<ClientDB> mDBData;
	private RealmChangeListener mListListener = new RealmChangeListener() {
		@Override
		public void onChange() {
			buildListView();
		}
	};

	private void buildListView() {
		mBinding.contentSrl.setRefreshing( false );
		if( mDBData.isLoaded() ) {
			if( mBinding.getAdapter() == null ) {
				mBinding.setAdapter( new ListAdapter<ClientDB>() );
			}
			if( mBinding.getAdapter()
						.getData() == null ) {
				mBinding.getAdapter()
						.setData( mDBData );
			}
			mBinding.getAdapter()
					.notifyDataSetChanged();
			if( mSnackbar != null && mSnackbar.isShown() ) {
				mSnackbar.dismiss();
			}
		}
	}


	private void load() {
		if( !RestUtils.shouldLoadLocal( App.Instance ) ) {
			loadList();
		}
		sendPending();
	}

	private void loadList() {
		App.Instance.getFireManager()
					.selectAll( Client.class );
	}

	private void sendPending() {
		App.Instance.getFireManager()
					.executePending( new ExecutePending() {
						@Override
						public void executePending( List<RestObject> pendingItems ) {
							for( RestObject object : pendingItems ) {
								Client client = (Client) object;
								App.Instance.getFireManager()
											.saveInBackground( client );
							}
						}

						@Override
						public RestObject build() {
							return new Client();
						}
					} );
	}


	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(
				R.menu.menu_main,
				menu
		);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch( id ) {
			case R.id.action_api_example:
				MainActivity2.showInstance( this );
				return true;
			case R.id.action_clear_pending:
				RestUtils.clearPending( ClientDB.class );
				return true;
		}

		return super.onOptionsItemSelected( item );
	}


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		mRealm = Realm.getDefaultInstance();
		initComponents();
		initListView();
		initFAB();
		load();
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

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault()
				.registerSticky( this );

	}

	@Override
	protected void onPause() {
		EventBus.getDefault()
				.unregister( this );
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		App.Instance.getFireManager()
					.onDestroy();
		if( mDBData != null && mListListener != null ) {
			mDBData.removeChangeListener( mListListener );
		}
		if( mRealm != null && !mRealm.isClosed() ) {
			mRealm.close();
		}
		super.onDestroy();
	}
}
