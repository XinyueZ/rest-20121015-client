package com.rest.client;

import java.util.UUID;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rest.client.databinding.MainBinding;
import com.rest.client.ds.Client;


public class MainActivity extends AppCompatActivity {
	/**
	 * Data-binding.
	 */
	private MainBinding mBinding;
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;

	private void initComponents() {
		mBinding = DataBindingUtil.setContentView(
				this,
				LAYOUT
		);
		setSupportActionBar( mBinding.toolbar );
	}


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		initComponents();
		FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
		fab.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				String uuid = UUID.randomUUID()
								  .toString();
				long time = System.currentTimeMillis();
				String comment = Build.MODEL + "---" + mBinding.commentEt.getText()
																		 .toString();
				Client client = new Client(
						uuid,
						time,
						comment
				);
				mBinding.commentEt.setEnabled( false );


				App.Instance.DB.addValueEventListener( new ValueEventListener() {
					@Override
					public void onDataChange( DataSnapshot dataSnapshot ) {
						App.Instance.DB.removeEventListener( this );

						mBinding.commentEt.setEnabled( true );
						Snackbar.make(
								mBinding.rootView,
								"Successfully.",
								Snackbar.LENGTH_SHORT
						).show();
					}

					@Override
					public void onCancelled( FirebaseError firebaseError ) {
						App.Instance.DB.removeEventListener( this );

						mBinding.commentEt.setEnabled( true );
						Snackbar.make(
								mBinding.rootView,
								"Failure.",
								Snackbar.LENGTH_SHORT
						).show();
					}
				} );
				App.Instance.DB.child( client.getReqId() )
							   .setValue( client );
				App.Instance.DB.push();
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
}
