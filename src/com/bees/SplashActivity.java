/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees;

import com.bees.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * SplashActivity.java
 * 
 * @author MURVIN BHANTOOA
 * @date 09/05/2012
 */
public class SplashActivity extends Activity implements AnimationListener {

	private final int DELAY = 1500;

	private Animation logoAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		ImageView imgViewLogo = (ImageView) findViewById(R.id.ImageViewLogo);

		imgViewLogo.setAnimation(logoAnimation = new Animation() {
			@Override
			public void initialize(int width, int height, int parentWidth,
					int parentHeight) {

				super.initialize(width, height, parentWidth, parentHeight);
				setDuration(DELAY);
				setFillAfter(true);
				setInterpolator(new AccelerateInterpolator());
			}

			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				t.setAlpha(interpolatedTime);
			}
		});

		logoAnimation.setAnimationListener(this);
	}

	public void onAnimationEnd(Animation animation) {
		if (hasAcceptedTerms()) {
			navigateHome();
		} else {
			showDialog(Constants.DIALOG_TERMS);
		}
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}

	private void navigateHome() {
		finish();
		SplashActivity.this.startActivity(new Intent(SplashActivity.this,
				BeesActivity.class));
	}

	private boolean hasAcceptedTerms() {
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);

		SharedPreferences.Editor editor = settings.edit();

		boolean hasAccepted = settings.getBoolean(
				Constants.SHARED_PREF_TERMS_ACCEPTED_KEY, false);

		if (!hasAccepted) {
			editor.putInt(Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY, 0);
			editor.putInt(Constants.SHARED_PREF_CATEGORIES_INDEX_KEY, 0);
			editor.putInt(Constants.SHARED_PREF_PROXIMITY_INDEX_KEY, 5);
			editor.putInt(Constants.SHARED_PREF_SORT_ORDER_INDEX_KEY, 0);
			editor.putString(Constants.SHARED_PREF_KEYWORD_KEY,
					Constants.KEYWORD);
			editor.putString(Constants.SHARED_PREF_MAP_CENTER,
					Constants.MAPCENTER_DEFAULT);
			editor.commit();
		}

		return hasAccepted;
	}

	protected Dialog onCreateDialog(int id) {
		if (id == Constants.DIALOG_TERMS) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));
			View v = getLayoutInflater().inflate(R.layout.terms, null);
			builder.setCancelable(false)
					.setTitle(getString(R.string.eula))
					.setIcon(R.drawable.ic_launcher2)
					.setView(v)
					.setPositiveButton(getString(R.string.accept),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SharedPreferences settings = getSharedPreferences(
											Constants.SHARED_PREF, 0);
									SharedPreferences.Editor editor = settings
											.edit();
									editor.putBoolean(
											Constants.SHARED_PREF_TERMS_ACCEPTED_KEY,
											true);
									editor.commit();
									navigateHome();
								}
							})
					.setNegativeButton(getString(R.string.decline),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
								}
							});
			AlertDialog alert = builder.create();
			return alert;
		}
		return null;
	}
}