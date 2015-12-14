package com.rest.client.app.adapters;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.ds.Client;
import com.rest.client.ds.ClientProxy;


public final class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int               ITEM_LAYOUT  = R.layout.item_layout;
	/**
	 * Data-source.
	 */
	private              List<ClientProxy> mVisibleData = new LinkedList<>();

	/**
	 * Get current used data-source.
	 *
	 * @return The data-source.
	 */
	public List<ClientProxy> getData() {
		return mVisibleData;
	}

	/**
	 * Set data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void setData( List<ClientProxy> data ) {
		mVisibleData = data;
	}

	/**
	 * Add data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 * @param index
	 * 		Position of new data.
	 */
	public void insertData( ClientProxy data, int index ) {
		mVisibleData.add(
				index,
				data
		);
	}


	/**
	 * Add data-source for list-view.
	 *
	 * @param data
	 * 		Data-source.
	 */
	public void addData( ClientProxy data ) {
		mVisibleData.add( data );
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
		ClientProxy entry = mVisibleData.get( position );
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
