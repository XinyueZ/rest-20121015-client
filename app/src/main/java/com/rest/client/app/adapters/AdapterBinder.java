package com.rest.client.app.adapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.chopping.utils.Utils;
import com.rest.client.R;
import com.rest.client.ds.PhotoUrlDB;
import com.squareup.picasso.Picasso;

public final class AdapterBinder {


	@SuppressWarnings("unchecked")
	@BindingAdapter("listAdapter")
	public static void setEntriesBinder( RecyclerView recyclerView, RecyclerView.Adapter adp ) {
		recyclerView.setAdapter( adp );
	}


	@BindingAdapter({ "imageNormalUrl" })
	public static void loadNormalImage( ImageView view, PhotoUrlDB urls ) {
		String url = "http://tomatofish.com/wp-content/uploads/2013/08/placeholder-tomatofish.jpg";
		if( urls != null ) {
			url = urls.getNormal();
		}
		try {
			Picasso picasso = Picasso.with( view.getContext() );
			picasso.load( Utils.uriStr2URI( url ).toASCIIString() ).placeholder( R.drawable.placeholder ).tag( view.getContext() ).into( view );
		} catch( NullPointerException e ) {
			Picasso.with( view.getContext() ).load( "http://tomatofish.com/wp-content/uploads/2013/08/placeholder-tomatofish.jpg" ).into( view );
		}
	}
}
