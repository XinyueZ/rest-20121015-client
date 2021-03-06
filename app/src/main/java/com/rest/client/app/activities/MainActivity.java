package com.rest.client.app.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.chopping.rest.ExecutePending;
import com.chopping.rest.RestObject;
import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.app.fragments.EditCommitDialogFragment;
import com.rest.client.bus.DeleteEvent;
import com.rest.client.bus.EditEvent;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientDB;
import com.rest.client.ds.ClientEditRequest;


public class MainActivity extends BaseActivity {
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link DeleteEvent}.
	 *
	 * @param e
	 * 		Event {@link DeleteEvent}.
	 */
	public void onEventMainThread( DeleteEvent e ) {
		getBinding().getAdapter()
					.notifyItemChanged( e.getPosition() );
		App.Instance.getFireManager()
					.delete( new Client().newFromDB( e.getDBObject() ) );
	}


	/**
	 * Handler for {@link EditEvent}.
	 *
	 * @param e
	 * 		Event {@link EditEvent}.
	 */
	public void onEventMainThread( EditEvent e ) {
		getBinding().getAdapter()
					.notifyItemChanged( e.getPosition() );
		EditCommitDialogFragment.newInstance(
				this,
				new ClientEditRequest().assignFromClient( (Client) new Client().newFromDB( e.getDBObject() ) )
		)
								.show(
										getSupportFragmentManager(),
										null
								);
	}

	//------------------------------------------------


	@Override
	protected boolean isRefreshable() {
		return true;
	}

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

	@Override
	protected void buildViews() {
		super.buildViews();
		onRestApiSuccess();
	}

	@Override
	protected void loadList() {
		App.Instance.getFireManager()
					.selectAll( Client.class );
	}


	@Override
	protected void sendPending() {
		App.Instance.getFireManager()
					.executePending(
							new ExecutePending() {
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
							},
							RestObject.NOT_SYNCED
					);


		App.Instance.getFireManager()
					.executePending(
							new ExecutePending() {
								@Override
								public void executePending( List<RestObject> pendingItems ) {
									for( RestObject object : pendingItems ) {
										Client client = (Client) object;
										App.Instance.getFireManager()
													.delete( client );
									}
								}

								@Override
								public RestObject build() {
									return new Client();
								}
							},
							RestObject.DELETE
					);


		App.Instance.getFireManager()
					.executePending(
							new ExecutePending() {
								@Override
								public void executePending( List<RestObject> pendingItems ) {
									for( RestObject object : pendingItems ) {
										ClientEditRequest client = (ClientEditRequest) object;
										App.Instance.getFireManager()
													.update( client );
									}
								}

								@Override
								public RestObject build() {
									return new ClientEditRequest();
								}
							},
							RestObject.UPDATE
					);
	}

	@Override
	protected void showCommentDialog() {
		EditCommitDialogFragment.newInstance( this )
								.show(
										getSupportFragmentManager(),
										null
								);
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate(
				R.menu.menu_main,
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
			case R.id.action_api_example:
				MainActivity2.showInstance( this );
				return true;
			case R.id.action_clear_pending:
				clearPendings();
				return true;
			case R.id.action_list_photos:
				PhotosActivity.showInstance( this );
				return true;
			case R.id.action_photo_calendar:
				PhotoCalendarActivity.showInstance( this );
		}

		return super.onOptionsItemSelected( item );
	}


	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		getBinding().loadMoreFab.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				ClientDB clientMin = (ClientDB) getData().get( getData().size() - 1 );
				App.Instance.getFireManager()
							.selectFrom(
									new Client().newFromDB( clientMin )
							);
				getBinding().contentSrl.setRefreshing( true );
				if( getBinding().loadMoreFab.isShown() ) {
					getBinding().loadMoreFab.hide();
				}
			}
		} );
	}

	@Override
	protected void onDestroy() {
		App.Instance.getFireManager()
					.onDestroy();
		super.onDestroy();
	}
}
