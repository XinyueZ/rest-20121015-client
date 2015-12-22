package com.rest.client.app.adapters;

import java.util.List;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rest.client.BR;
import com.rest.client.R;
import com.rest.client.app.App;

import io.realm.RealmObject;


public final class ListAdapter<T extends RealmObject> extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements Observable {
	private PropertyChangeRegistry mRegistry = new PropertyChangeRegistry();
	private
	@Bindable
	long mCount;
	/**
	 * Main layout for this component.
	 */
	private static final int     ITEM_LAYOUT  = R.layout.item_layout;
	/**
	 * Data-source.
	 */
	private              List<T> mVisibleData;

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<T> getData() {
		return mVisibleData;
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 *
	 * @return This object.
	 */
	public ListAdapter<T> setData( List<T> data ) {
		mVisibleData = data;
		mCount = mVisibleData.size();
		mRegistry.notifyChange(
				this,
				BR.count
		);
		return this;
	}


	/**
	 * Add data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 *
	 * @return This object.
	 */
	public ListAdapter<T> addData( T data ) {
		mVisibleData.add( data );
		mCount = mVisibleData.size();
		mRegistry.notifyChange(
				this,
				BR.count
		);
		return this;
	}


	/**
	 * Add data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 *
	 * @return This object.
	 */
	public ListAdapter<T> addData( List<T> data ) {
		mVisibleData.addAll( data );
		mCount = mVisibleData.size();
		mRegistry.notifyChange(
				this,
				BR.count
		);
		return this;
	}

	@Override
	public int getItemCount() {
		return mVisibleData == null ? 0 : mVisibleData.size();
	}

	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		Context        cxt      = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from( cxt );
		ViewDataBinding binding = DataBindingUtil.inflate(
				inflater,
				ITEM_LAYOUT,
				parent,
				false
		);
		return new ListAdapter.ViewHolder( binding );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position ) {
		T entry = mVisibleData.get( position );
		holder.mBinding.setVariable(
				com.rest.client.BR.client,
				entry
		);
		holder.mBinding.setVariable(
				com.rest.client.BR.position,
				position
		);
		holder.mBinding.setVariable(
				com.rest.client.BR.cxt,
				App.Instance
		);
		holder.mBinding.executePendingBindings();
	}

	public long getCount() {
		return mCount;
	}

	@Override
	public void addOnPropertyChangedCallback( OnPropertyChangedCallback callback ) {
		mRegistry.add( callback );
	}

	@Override
	public void removeOnPropertyChangedCallback( OnPropertyChangedCallback callback ) {
		mRegistry.remove( callback );
	}

	/**
	 * ViewHolder for the list.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;

		ViewHolder( ViewDataBinding binding ) {
			super( binding.getRoot() );
			mBinding = binding;
		}
	}
}
