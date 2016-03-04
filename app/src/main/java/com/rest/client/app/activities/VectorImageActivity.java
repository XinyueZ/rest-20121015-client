package com.rest.client.app.activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.rest.client.R;

public final class VectorImageActivity extends AppCompatActivity {
	/**
	 * Show single instance of {@link VectorImageActivity}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance( Activity cxt ) {
		Intent intent = new Intent(
				cxt,
				VectorImageActivity.class
		);
		intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP );
		ActivityCompat.startActivity(
				cxt,
				intent,
				null
		);
	}

	@Override
	protected void onCreate( @Nullable Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_vector_image );
		ImageView iv       = (ImageView) findViewById( R.id.iv_2 );
		Drawable  drawable = iv.getDrawable();
		if( drawable instanceof Animatable ) {
			( (Animatable) drawable ).start();
		}
	}
}
