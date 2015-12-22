package com.rest.client.app.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
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
import com.rest.client.rest.events.RestResponseEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity {

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
		EventBus.getDefault()
				.removeStickyEvent( e );
		if( mSnackbar != null && mSnackbar.isShown() ) {
			mSnackbar.dismiss();
		}

		mBinding.getAdapter()
				.notifyItemInserted(0);
	}
	//------------------------------------------------


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
				mBinding.setAdapter( new ListAdapter<ClientDB>().setData( dbItems ) );
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
		if( id == R.id.action_api_example ) {
			MainActivity2.showInstance( MainActivity.this );
			return true;
		}

		return super.onOptionsItemSelected( item );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		initComponents();
		initList();
		initFAB();
		mSnackbar = Snackbar.make(
				mBinding.rootView,
				"Network connected.",
				Snackbar.LENGTH_INDEFINITE
		);
		mSnackbar.show();

		App.Instance.getClientRestFireManager()
					.install(
							App.Instance
					);
		App.Instance.getClientRestFireManager()
					.selectAll( ClientDB.class,
								Client.class
					);
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
		App.Instance.getClientRestFireManager()
					.uninstall();
		super.onDestroy();
	}
}
