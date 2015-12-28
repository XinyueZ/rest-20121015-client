package com.rest.client.app.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.app.fragments.EditCommitDialogFragment;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientDB;
import com.rest.client.rest.ExecutePending;
import com.rest.client.rest.RestObject;
import com.rest.client.rest.RestUtils;


public class MainActivity extends BaseActivity {


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
	protected void loadList() {
		App.Instance.getFireManager()
					.selectAll( Client.class );
	}


	@Override
	protected void sendPending() {
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
	protected void showCommentDialog() {
		EditCommitDialogFragment.newInstance(  this )
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
	protected void onDestroy() {
		App.Instance.getFireManager()
					.onDestroy();
		super.onDestroy();
	}
}
