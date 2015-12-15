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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.app.adapters.ListAdapter;
import com.rest.client.app.fragments.EditCommitDialogFragment;
import com.rest.client.databinding.MainBinding;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientPending;
import com.rest.client.ds.ClientProxy;
import com.rest.client.events.InsertClientEvent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import io.realm.Realm;
import io.realm.RealmResults;


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
	 * DB for storing pending data to upload.
	 */
	private Realm mRealm;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.rest.client.events.InsertClientEvent}.
	 *
	 * @param e
	 * 		Event {@link com.rest.client.events.InsertClientEvent}.
	 */
	@Subscribe
	public void onEvent( InsertClientEvent e ) {
		Client client = e.getClient();

		//TO PENDING QUEUE.
		mRealm.beginTransaction();
		ClientPending pending = new ClientPending(
				client.getReqId(),
				client.getReqTime(),
				client.getComment()
		);
		mRealm.copyToRealm( pending );
		mRealm.commitTransaction();

		//SAVE ON SERVER.
		save( client );
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
		mBinding.setAdapter( new ListAdapter() );
		App.Instance.DB.addChildEventListener( new ChildEventListener() {
			@Override
			public void onChildAdded( DataSnapshot snapshot, String previousChild ) {
				Log.i(
						MainActivity.class.getName(),
						"onChildAdded"
				);
				Client serverData = snapshot.getValue( Client.class );
				//CHECK FOR PENDING.
				//PUT TO PENDING COLLECTION.
				RealmResults<ClientPending> pendingObjects = mRealm.where( ClientPending.class )
																   .equalTo(
																		   "reqId",
																		   serverData.getReqId()
																   )
																   .findAll();


				ClientProxy proxy = new ClientProxy(
						serverData,
						App.Instance.DB_CONNECTED || pendingObjects.size() == 0 ? ClientProxy.SYNCED  : ClientProxy.NOT_SYNCED
				);
				//FRESH SERVER DATA TO LOCAL.
				mBinding.getAdapter()
						.addData( proxy );
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

			@Override
			public void onChildChanged( DataSnapshot dataSnapshot, String s ) {
				Log.i(
						MainActivity.class.getName(),
						"onChildChanged: " + dataSnapshot.toString()
				);
			}

			@Override
			public void onChildRemoved( DataSnapshot dataSnapshot ) {
				Log.i(
						MainActivity.class.getName(),
						"onChildRemoved: " + dataSnapshot.toString()
				);
			}

			@Override
			public void onChildMoved( DataSnapshot dataSnapshot, String s ) {
				Log.i(
						MainActivity.class.getName(),
						"onChildMoved: " + dataSnapshot.toString()
				);
			}

			@Override
			public void onCancelled( FirebaseError firebaseError ) {
				Log.i(
						MainActivity.class.getName(),
						"onCancelled"
				);
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
		if( id == R.id.action_settings ) {
			return true;
		}

		return super.onOptionsItemSelected( item );
	}


	private void save( Client client ) {
		App.Instance.DB.addValueEventListener( new ValueEventListener() {
			@Override
			public void onDataChange( DataSnapshot dataSnapshot ) {
				App.Instance.DB.removeEventListener( this );
				Snackbar.make(
						mBinding.rootView,
						"Successfully.",
						Snackbar.LENGTH_SHORT
				)
						.show();
			}

			@Override
			public void onCancelled( FirebaseError firebaseError ) {
				App.Instance.DB.removeEventListener( this );
				Snackbar.make(
						mBinding.rootView,
						"Failure.",
						Snackbar.LENGTH_SHORT
				)
						.show();
			}
		} );
		App.Instance.DB.child( client.getReqId() )
					   .setValue( client );
		App.Instance.DB.push();
	}


	private void scrollToNotifiedPosition() {
		LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mBinding.responsesRv.getLayoutManager();
		if( linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 ) {
			mBinding.responsesRv.getLayoutManager()
								.scrollToPosition( 0 );
		}
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		mRealm = Realm.getInstance( this );
		initComponents();
		initList();
		initFAB();
		Firebase connectedRef = new Firebase( App.Instance.URL + "/.info/connected" );
		connectedRef.addValueEventListener( new ValueEventListener() {
			@Override
			public void onDataChange( DataSnapshot snapshot ) {
				boolean connected = snapshot.getValue( Boolean.class );
				if( connected ) {
					boolean hasPending = mRealm.where( ClientPending.class )
											   .count() > 0;
					if( hasPending ) {
						mRealm.beginTransaction();
						mRealm.clear( ClientPending.class );
						mRealm.commitTransaction();
					}
					int i = 0;
					for( ClientProxy clientProxy : mBinding.getAdapter()
														   .getData() ) {
						if( clientProxy.getStatus() == ClientProxy.NOT_SYNCED ) {
							clientProxy.setStatus( ClientProxy.SYNCED );
							mBinding.getAdapter()
									.notifyItemChanged( i );
						}
						i++;
					}
				}
			}

			@Override
			public void onCancelled( FirebaseError error ) {
				System.err.println( "Listener was cancelled" );
			}
		} );
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault()
				.register( this );
	}

	@Override
	protected void onPause() {
		EventBus.getDefault()
				.unregister( this );
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
