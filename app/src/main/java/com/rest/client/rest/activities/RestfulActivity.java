package com.rest.client.rest.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rest.client.app.App;
import com.rest.client.rest.RestUtils;
import com.rest.client.rest.events.UpdateNetworkStatusEvent;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A common base {@link android.app.Activity} that calls REST.
 *
 * @author Xinyue Zhao
 */
public abstract class RestfulActivity<C extends Class<? extends RealmObject>> extends AppCompatActivity {

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


	private Realm                               mRealm;
	private RealmResults<? extends RealmObject> mRealmData;
	private RealmChangeListener mListListener = new RealmChangeListener() {
		@Override
		public void onChange() {
			buildRestUI();
		}
	};

	private void load() {
		if( !RestUtils.shouldLoadLocal( App.Instance ) ) {
			loadList();
		}
		sendPending();
	}


	protected abstract void initDataBinding();

	protected void initRestUI() {
		mRealmData = mRealm.where( getDataClazz() )
						   .findAllSorted(
								   "reqTime",
								   Sort.DESCENDING
						   );
		mRealmData.addChangeListener( mListListener );
		if( RestUtils.shouldLoadLocal( App.Instance ) ) {
			buildRestUI();
		}
	}

	protected abstract Class<? extends RealmObject> getDataClazz();

	protected abstract void sendPending();

	protected abstract void loadList();

	protected abstract void buildRestUI();

	protected RealmResults<? extends RealmObject> getData() {
		return mRealmData;
	}

	protected boolean isDataLoaded() {
		return mRealmData.isLoaded();
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
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		mRealm = Realm.getDefaultInstance();
		initDataBinding();
		initRestUI();
		load();
	}

	@Override
	protected void onDestroy() {
		if( mRealmData != null && mListListener != null ) {
			mRealmData.removeChangeListener( mListListener );
		}
		if( mRealm != null && !mRealm.isClosed() ) {
			mRealm.close();
		}
		super.onDestroy();
	}
}
