package com.rest.client.app.fragments;


import java.util.UUID;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import com.rest.client.R;
import com.rest.client.app.App;
import com.rest.client.ds.Client;
import com.rest.client.events.InsertClientEvent;

import de.greenrobot.event.EventBus;

public final class EditCommitDialogFragment extends DialogFragment {
	private EditText mCommentEt;

	public static DialogFragment newInstance( Context context ) {
		return (DialogFragment) EditCommitDialogFragment.instantiate(
				context,
				EditCommitDialogFragment.class.getName()
		);
	}

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		mCommentEt = new EditText( App.Instance );
		Resources resources = App.Instance.getResources();
		int       rightleft = resources.getDimensionPixelSize( R.dimen.activity_horizontal_margin );
		int       topbottom = resources.getDimensionPixelSize( R.dimen.activity_vertical_margin );
		mCommentEt.setTextColor( ContextCompat.getColor(
				App.Instance,
				android.R.color.holo_blue_dark
		) );
		mCommentEt.setInputType( InputType.TYPE_TEXT_FLAG_MULTI_LINE );
		mCommentEt.setPadding(
				rightleft,
				topbottom,
				rightleft,
				topbottom
		);
		return new AlertDialog.Builder( getActivity() ).setTitle( "Comment" )
													   .setView( mCommentEt )
													   .setCancelable( true )
													   .setPositiveButton(
															   android.R.string.ok,
															   new DialogInterface.OnClickListener() {
																   public void onClick( DialogInterface dialog, int whichButton ) {

																	   String uuid = UUID.randomUUID()
																						 .toString();
																	   long time = System.currentTimeMillis();
																	   String comment = Build.MODEL + "---" + mCommentEt.getText()
																														.toString();
																	   Client client = new Client(
																			   uuid,
																			   time,
																			   comment
																	   );

																	   EventBus.getDefault()
																			   .post( new InsertClientEvent( client ) );
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
