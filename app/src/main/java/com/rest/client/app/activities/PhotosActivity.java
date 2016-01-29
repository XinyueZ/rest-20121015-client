package com.rest.client.app.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.chopping.activities.RestfulActivity;
import com.chopping.rest.RestFireManager;
import com.rest.client.R;
import com.rest.client.app.SearchSuggestionProvider;
import com.rest.client.app.adapters.PhotoListAdapter;
import com.rest.client.databinding.PhotosBinding;
import com.rest.client.ds.Photo;
import com.rest.client.ds.PhotoDB;

import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


public class PhotosActivity extends RestfulActivity {
	private static RestFireManager sFireMgr;


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

	/**
	 * Suggestion list while tipping.
	 */
	protected SearchRecentSuggestions mSuggestions;
	/**
	 * Keyword that will be searched.
	 */
	private String mKeyword = "";
	/**
	 * The search.
	 */
	private SearchView mSearchView;
	/**
	 * Search menu.
	 */
	private MenuItem   mSearchMenu;

	/**
	 * Show single instance of {@link PhotosActivity}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance( Activity cxt ) {
		Intent intent = new Intent(
				cxt,
				PhotosActivity.class
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
		sFireMgr.selectAll( Photo.class );
	}


	@Override
	protected void sendPending() {

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

		mBinding.contentSrl.setRefreshing( false );
		mBinding.loadingPb.setVisibility( View.GONE );
	}

	protected Class<? extends RealmObject> getDataClazz() {
		return PhotoDB.class;
	}

	//onNetworkConnected() ignored.


	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate(
				R.menu.menu_photos,
				menu
		);

		//Search
		mSearchMenu = menu.findItem( R.id.action_search );
		MenuItemCompat.setOnActionExpandListener(
				mSearchMenu,
				new MenuItemCompat.OnActionExpandListener() {
					@Override
					public boolean onMenuItemActionExpand( MenuItem item ) {
						return true;
					}

					@Override
					public boolean onMenuItemActionCollapse( MenuItem item ) {
						mKeyword = "";
						doSearch();
						return true;
					}
				}
		);
		mSearchView = (SearchView) MenuItemCompat.getActionView( mSearchMenu );
		mSearchView.setOnQueryTextListener( new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange( String newText ) {
				if( TextUtils.isEmpty( newText ) ) {
					mKeyword = null;
					doSearch();
				}
				return false;
			}

			@Override
			public boolean onQueryTextSubmit( String s ) {
				InputMethodManager mgr = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
				mgr.hideSoftInputFromWindow(
						mSearchView.getWindowToken(),
						0
				);
				resetSearchView();
				return false;
			}
		} );
		mSearchView.setIconifiedByDefault( true );
		SearchManager searchManager = (SearchManager) getSystemService( SEARCH_SERVICE );
		if( searchManager != null ) {
			SearchableInfo info = searchManager.getSearchableInfo( getComponentName() );
			mSearchView.setSearchableInfo( info );
		}
		return true;
	}


	/**
	 * Reset the UI status of searchview.
	 */
	protected void resetSearchView() {
		if( mSearchView != null ) {
			mSearchView.clearFocus();
		}
	}


	/**
	 * Search for a photo.
	 */
	private void doSearch() {
		mBinding.getAdapter()
				.setData( null );
		queryLocalData();
	}

	@Override
	protected void buildQuery( RealmQuery<? extends RealmObject> q ) {
		if( !TextUtils.isEmpty( mKeyword ) ) {
			q.contains(
					"description",
					mKeyword
			);
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
	public boolean onOptionsItemSelected( MenuItem item ) {
		int id = item.getItemId();
		switch( id ) {
			case R.id.action_fire_example:
				MainActivity.showInstance( this );
				return true;

			case R.id.action_api_example:
				MainActivity2.showInstance( this );
				return true;

			case R.id.action_photo_calendar:
				PhotoCalendarActivity.showInstance( this );
				return true;
		}

		return super.onOptionsItemSelected( item );
	}

	//[Begin for detecting scrolling onto bottom]
	private int mVisibleItemCount;
	private int mPastVisibleItems;
	private int mTotalItemCount;
	//[End]

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		if( sFireMgr == null ) {
			String      url       = null;
			String      auth      = null;
			String      limitLast = null;
			Properties  prop      = new Properties();
			InputStream input     = null;
			try {
			/*From "resources".*/
				input = getApplication().getClassLoader()
										.getResourceAsStream( "firebase2.properties" );
				if( input != null ) {
					// load a properties file
					prop.load( input );
					url = prop.getProperty( "firebase_url" );
					auth = prop.getProperty( "firebase_auth" );
					limitLast = prop.getProperty( "firebase_standard_limit" );
				}
			} catch( IOException ex ) {
				ex.printStackTrace();
			} finally {
				if( input != null ) {
					try {
						input.close();
					} catch( IOException e ) {
						e.printStackTrace();
					}
				}
			}
			sFireMgr = new RestFireManager(
					url,
					auth,
					Integer.valueOf( limitLast )
			);
			sFireMgr.onCreate( getApplication() );
		}

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

		//For search and suggestions.
		mSuggestions = new SearchRecentSuggestions(
				this,
				getString( R.string.suggestion_auth ),
				SearchSuggestionProvider.MODE
		);
		mBinding.fab.hide();
		mBinding.fab.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				PhotoDB  photoMin = (PhotoDB) getData().get( getData().size() - 1 );
				sFireMgr.selectFrom(new Photo().newFromDB(  photoMin));
				mBinding.contentSrl.setRefreshing( true );
				if( mBinding.fab.isShown() ) {
					mBinding.fab.hide();
				}
			}
		} );
		mBinding.responsesRv.addOnScrollListener( new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
				//Calc whether the list has been scrolled on bottom,
				//this lets app to getting next page.
				LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				mVisibleItemCount = linearLayoutManager.getChildCount();
				mTotalItemCount = linearLayoutManager.getItemCount();
				mPastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
				if( ViewCompat.getY( recyclerView ) < dy ) {
					if( ( mVisibleItemCount + mPastVisibleItems ) == mTotalItemCount ) {
						if( !mBinding.fab.isShown() ) {
							mBinding.fab.show();
						}
					}
				} else {
					if( mBinding.fab.isShown() ) {
						mBinding.fab.hide();
					}
				}
			}
		} );
	}

	@Override
	protected void onNewIntent( Intent intent ) {
		super.onNewIntent( intent );
		setIntent( intent );

		mKeyword = intent.getStringExtra( SearchManager.QUERY );
		if( !TextUtils.isEmpty( mKeyword ) ) {
			mKeyword = mKeyword.trim();
			mSearchView.setQueryHint( Html.fromHtml( "<font color = #ffffff>" + mKeyword + "</font>" ) );

			mKeyword = intent.getStringExtra( SearchManager.QUERY );
			mKeyword = mKeyword.trim();
			resetSearchView();

			//No save for suggestions.
			mSuggestions.saveRecentQuery(
					mKeyword,
					null
			);

			//Move map to searched location.
			doSearch();
		}

	}
}
