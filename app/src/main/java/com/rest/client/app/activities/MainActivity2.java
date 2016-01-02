package com.rest.client.app.activities;

import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.chopping.rest.ExecutePending;
import com.chopping.rest.RestObject;
import com.chopping.rest.RestUtils;
import com.rest.client.R;
import com.rest.client.api.Api;
import com.rest.client.app.App;
import com.rest.client.app.fragments.EditCommitDialogFragment2;
import com.rest.client.bus.DeleteEvent;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientDB;
import com.rest.client.ds.ClientDeleteRequest;
import com.rest.client.ds.RequestForResponse;


public class MainActivity2 extends BaseActivity {

	/**
	 * Handler for {@link DeleteEvent}.
	 *
	 * @param e
	 * 		Event {@link DeleteEvent}.
	 */
	public void onEventMainThread( DeleteEvent e ) {
		getBinding().getAdapter()
					.notifyItemChanged( e.getPosition() );
		ClientDeleteRequest delClient = new ClientDeleteRequest();
		delClient.setReqId( new Client().fromDB( e.getDBObject() )
										.getReqId() );
		App.Instance.getApiManager()
					.deleteAsync(
							Api.Retrofit.create( Api.class )
										.deleteClient( delClient ),
							delClient
					);
	}

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


	@Override
	protected void sendPending() {
		App.Instance.getApiManager()
					.executePending(
							new ExecutePending() {
								@Override
								public void executePending( List<RestObject> pendingItems ) {
									for( RestObject object : pendingItems ) {
										Client client = (Client) object;
										App.Instance.getApiManager()
													.execAsync(
															Api.Retrofit.create( Api.class )
																		.addClient( client ),
															client
													);
									}
								}

								@Override
								public RestObject build() {
									return new Client();
								}
							},
							RestObject.NOT_SYNCED
					);


		App.Instance.getApiManager()
					.executePending(
							new ExecutePending() {
								@Override
								public void executePending( List<RestObject> pendingItems ) {
									for( RestObject object : pendingItems ) {
										ClientDeleteRequest delClient = new ClientDeleteRequest();
										delClient.setReqId( object.getReqId() );
										App.Instance.getApiManager()
													.deleteAsync(
															Api.Retrofit.create( Api.class )
																		.deleteClient( delClient ),
															delClient
													);
									}
								}

								@Override
								public RestObject build() {
									return new Client();
								}
							},
							RestObject.DELETE
					);
	}

	@Override
	protected void loadList() {
		RequestForResponse rfr = new RequestForResponse();
		rfr.setReqId( UUID.randomUUID()
						  .toString() );
		rfr.setReqTime( System.currentTimeMillis() );
		App.Instance.getApiManager()
					.execAsync(
							Api.Retrofit.create( Api.class )
										.getList( rfr ),
							rfr
					);
	}

	@Override
	protected void showCommentDialog() {
		EditCommitDialogFragment2.newInstance( this )
								 .show(
										 getSupportFragmentManager(),
										 null
								 );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate(
				R.menu.menu_main_2,
				menu
		);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		int id = item.getItemId();
		switch( id ) {
			case R.id.action_api_example:
				MainActivity.showInstance( this );
				return true;
			case R.id.action_clear_pending:
				RestUtils.clearPending( ClientDB.class );
				return true;
		}

		return super.onOptionsItemSelected( item );
	}


}