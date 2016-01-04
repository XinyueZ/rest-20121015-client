package com.rest.client.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rest.client.app.adapters.ListAdapter.ViewHolder;
import com.rest.client.bus.DeleteEvent;
import com.rest.client.bus.EditEvent;

import de.greenrobot.event.EventBus;
import io.realm.RealmObject;

public final class ListItemHandler<T extends RealmObject> {
	private ViewHolder     mViewHolder;
	private ListAdapter<T> mAdapter;

	public ListItemHandler( ViewHolder viewHolder, ListAdapter<T> adapter ) {
		mViewHolder = viewHolder;
		mAdapter = adapter;
	}

	public void deleteEvent( View view ) {
		int pos = mViewHolder.getAdapterPosition();
		if( pos != RecyclerView.NO_POSITION ) {
			EventBus.getDefault()
					.post( new DeleteEvent(
							pos,
							mAdapter.getData()
									.get( pos )
					) );
		}
	}

	public void editEvent( View view ) {
		int pos = mViewHolder.getAdapterPosition();
		if( pos != RecyclerView.NO_POSITION ) {
			EventBus.getDefault()
					.post( new EditEvent(
							pos,
							mAdapter.getData()
									.get( pos )
					) );
		}
	}
}
