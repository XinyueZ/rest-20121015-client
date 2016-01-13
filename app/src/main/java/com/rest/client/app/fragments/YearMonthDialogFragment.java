package com.rest.client.app.fragments;


import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.bus.SelectYearMonthEvent;

import de.greenrobot.event.EventBus;

public final class YearMonthDialogFragment extends DialogFragment {
	private EditText mYearEt;
	private EditText mMonthEt;

	public static DialogFragment newInstance( Context context ) {
		return (DialogFragment) YearMonthDialogFragment.instantiate(
				context,
				YearMonthDialogFragment.class.getName()
		);
	}


	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		LinearLayout li = new LinearLayout( App.Instance );
		li.setOrientation( LinearLayout.VERTICAL );
		mYearEt = new EditText( App.Instance );

		mMonthEt = new EditText( App.Instance );

		Resources resources = App.Instance.getResources();
		int       rightleft = resources.getDimensionPixelSize( R.dimen.activity_horizontal_margin );
		int       topbottom = resources.getDimensionPixelSize( R.dimen.activity_vertical_margin );

		mYearEt.setTextColor( ContextCompat.getColor(
				App.Instance,
				android.R.color.holo_blue_dark
		) );
		mYearEt.setInputType( InputType.TYPE_TEXT_FLAG_MULTI_LINE );
		mYearEt.setPadding(
				rightleft,
				topbottom,
				rightleft,
				topbottom
		);
		mYearEt.setTextColor( getResources().getColor( R.color.colorPrimary ) );
		mYearEt.setHint( "Year" );
		mYearEt.setHintTextColor( getResources().getColor( R.color.colorPrimary ) );
		mYearEt.setInputType( InputType.TYPE_CLASS_NUMBER );

		mMonthEt.setTextColor( ContextCompat.getColor(
				App.Instance,
				android.R.color.holo_blue_dark
		) );
		mMonthEt.setInputType( InputType.TYPE_TEXT_FLAG_MULTI_LINE );
		mMonthEt.setPadding(
				rightleft,
				topbottom,
				rightleft,
				topbottom
		);
		mMonthEt.setTextColor( getResources().getColor( R.color.colorPrimary ) );
		mMonthEt.setHint( "Month" );
		mMonthEt.setHintTextColor( getResources().getColor( R.color.colorPrimary ) );
		mMonthEt.setInputType( InputType.TYPE_CLASS_NUMBER );

		li.addView( mYearEt );
		li.addView( mMonthEt );

		Calendar calendar = Calendar.getInstance();
		int      year     = calendar.get( Calendar.YEAR );
		mYearEt.setText( "" + year );
		int      month    = calendar.get( Calendar.MONTH ) + 1;
		mMonthEt.setText( "" + month );
		return new AlertDialog.Builder( getActivity() ).setTitle( "Year and month input" )
													   .setView( li )
													   .setCancelable( true )
													   .setPositiveButton(
															   android.R.string.ok,
															   new DialogInterface.OnClickListener() {
																   public void onClick( DialogInterface dialog, int whichButton ) {
																	   int year = Integer.parseInt( mYearEt.getText()
																										   .toString() );
																	   int month = Integer.parseInt( mMonthEt.getText()
																										   .toString() );
																	   EventBus.getDefault()
																			   .post( new SelectYearMonthEvent(year, month) );
																	   dismiss();
																   }
															   }
													   )
													   .setNegativeButton(
															   android.R.string.cancel,
															   null
													   )
													   .create();
	}


}

