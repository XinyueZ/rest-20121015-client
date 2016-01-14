package com.rest.client.app.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.bus.SelectDateTime;
import com.rest.client.databinding.QueryDateBinding;

import de.greenrobot.event.EventBus;


public final class InputDateDialogFragment extends DialogFragment implements OnClickListener {
	public static final  int QUERY_DAY             = 0x1;
	public static final  int QUERY_SINGLE_MONTH    = 0x2;
	public static final  int QUERY_LAST_THREE_DAYS = 0x3;
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT                = R.layout.fragment_dialog_input_date;
	/**
	 * Data-binding.
	 */
	private QueryDateBinding mBinding;

	public static DialogFragment newInstance( Context context ) {
		return (DialogFragment) InputDateDialogFragment.instantiate(
				context,
				InputDateDialogFragment.class.getName()
		);
	}


	private boolean validateYear( int year ) {
		return year > 0 && year >= 1998;
	}

	private boolean validateDate( int date ) {
		return date > 0 && date <= 31;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		return inflater.inflate(
				LAYOUT,
				container,
				false
		);
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ) {
		super.onViewCreated(
				view,
				savedInstanceState
		);
		setCancelable( true );
		mBinding = DataBindingUtil.bind( view.findViewById( R.id.query_types_sv ) );
		getDialog().setTitle( R.string.title_query_date );

		mBinding.singleYearEtTil.setHint( App.Instance.getString( R.string.lbl_year ) );
		mBinding.singleYearEtTil.setHintAnimationEnabled( true );

		mBinding.singleMonthEtTil.setHint( App.Instance.getString( R.string.lbl_month ) );
		mBinding.singleMonthEtTil.setHintAnimationEnabled( true );

		mBinding.singleDayEtTil.setHint( App.Instance.getString( R.string.lbl_day ) );
		mBinding.singleDayEtTil.setHintAnimationEnabled( true );

		mBinding.yearEtTil.setHint( App.Instance.getString( R.string.lbl_year ) );
		mBinding.yearEtTil.setHintAnimationEnabled( true );

		mBinding.monthEtTil.setHint( App.Instance.getString( R.string.lbl_month ) );
		mBinding.monthEtTil.setHintAnimationEnabled( true );

		mBinding.queryBtn.setOnClickListener( this );
	}

	@Override
	public void onClick( View v ) {
		switch( v.getId() ) {
			case R.id.query_btn:
				query();
				break;
		}
	}

	private void query() {
		boolean ok = true;
		if( mBinding.dateRb.isChecked() ) {
			int year = Integer.valueOf( mBinding.singleYearEt.getText()
															 .toString() );
			if( !validateYear( year ) ) {
				mBinding.singleYearEtTil.setError( getString( R.string.lbl_invalid_year ) );
				ok = false;
			} else {
				mBinding.singleYearEtTil.setErrorEnabled( false );
				ok = true;
			}

			int month = Integer.valueOf( mBinding.singleMonthEt.getText()
															   .toString() );
			if( !validateDate( month ) ) {
				mBinding.singleMonthEtTil.setError( getString( R.string.lbl_invalid_date ) );
				ok = ok && false;
			} else {
				mBinding.singleMonthEtTil.setErrorEnabled( false );
				ok = ok && true;
			}

			int day = Integer.valueOf( mBinding.singleDayEt.getText()
														   .toString() );
			if( !validateDate( day ) ) {
				mBinding.singleDayEtTil.setError( getString( R.string.lbl_invalid_date ) );
				ok = ok && false;
			} else {
				mBinding.singleDayEtTil.setErrorEnabled( false );
				ok = ok && true;
			}

			if( ok ) {
				EventBus.getDefault()
						.post( new SelectDateTime(
								QUERY_DAY,
								year,
								month,
								day
						) );
				dismiss();
			}
		} else if( mBinding.monthRb.isChecked() ) {
			int year = Integer.valueOf( mBinding.yearEt.getText()
													   .toString() );
			if( !validateYear( year ) ) {
				mBinding.yearEtTil.setError( getString( R.string.lbl_invalid_year ) );
				ok = ok && false;
			} else {
				mBinding.yearEtTil.setErrorEnabled( false );
				ok = ok && true;
			}

			int month = Integer.valueOf( mBinding.monthEt.getText()
														 .toString() );
			if( !validateDate( month ) ) {
				mBinding.monthEtTil.setError( getString( R.string.lbl_invalid_date ) );
				ok = ok && false;
			} else {
				mBinding.monthEtTil.setErrorEnabled( false );
				ok = ok && true;
			}

			if( ok ) {
				EventBus.getDefault()
						.post( new SelectDateTime(
								QUERY_SINGLE_MONTH,
								year,
								month
						) );
				dismiss();
			}
		} else if( mBinding.lastThreeRb.isChecked() ) {
			EventBus.getDefault()
					.post( new SelectDateTime( QUERY_LAST_THREE_DAYS ) );
			dismiss();
		} else if( !mBinding.dateRb.isChecked() && !mBinding.monthRb.isChecked() && !mBinding.lastThreeRb.isChecked() ) {
			dismiss();
		}
	}
}

