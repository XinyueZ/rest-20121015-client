package com.rest.client.app.activities;

import java.util.Collections;
import java.util.Comparator;

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
import com.rest.client.ds.ClientProxy;
import com.rest.client.rest.events.RestChangedAfterConnectEvent;
import com.rest.client.rest.events.RestConnectEvent;
import com.rest.client.rest.events.RestObjectAddedEvent;
import com.rest.client.rest.events.RestApiResponseArrivalEvent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


public class MainActivity extends AppCompatActivity {

	/**
	 * Data-binding.
	 */
	private MainBinding mBinding;
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RestChangedAfterConnectEvent}.
	 *
	 * @param e
	 * 		Event {@link RestChangedAfterConnectEvent}.
	 */
	@Subscribe
	public void onEvent( RestChangedAfterConnectEvent e ) {
		mBinding.getAdapter()
				.notifyItemChanged( (int) e.getIndex() );
	}

	/**
	 * Handler for {@link RestConnectEvent}.
	 *
	 * @param e
	 * 		Event {@link RestConnectEvent}.
	 */
	@Subscribe
	public void onEvent( RestConnectEvent e ) {
		Snackbar.make(
				mBinding.rootView,
				"Network connected.",
				Snackbar.LENGTH_SHORT
		).show();
	}

	/**
	 * Handler for {@link RestObjectAddedEvent}.
	 *
	 * @param e
	 * 		Event {@link RestObjectAddedEvent}.
	 */
	@Subscribe
	public void onEvent( RestObjectAddedEvent e ) {
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
	}

	/**
	 * Handler for {@link RestApiResponseArrivalEvent}.
	 *
	 * @param e
	 * 		Event {@link RestApiResponseArrivalEvent}.
	 */
	@Subscribe
	public void onEvent( RestApiResponseArrivalEvent e ) {
		mBinding.getAdapter()
				.notifyItemChanged( (int) e.getIndex() );
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
		mBinding.setAdapter( new ListAdapter<ClientProxy>() );
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.Instance.getClientRestFireManager()
					.install(
							App.Instance,
							mBinding.getAdapter().getData()
					);
		EventBus.getDefault()
				.register( this );
	}

	@Override
	protected void onPause() {
		App.Instance.getClientRestFireManager()
					.uninstall();
		EventBus.getDefault()
				.unregister( this );
		super.onPause();
	}
}
