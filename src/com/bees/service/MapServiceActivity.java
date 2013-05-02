/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.maps.MapActivity;

/**
 * MapServiceActivity.java
 * 
 * @author MURVIN BHANTOOA
 * @date 08 May 2012
 */
public abstract class MapServiceActivity extends MapActivity implements
		IServiceActivity {

	/** Handles service call back UI updates */
	protected Handler handler;

	protected ServiceHelper serviceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setServiceHelper();
		serviceHelper.addObserver(this);
		serviceHelper.bindService(this);
		handler = new Handler();
	}

	public abstract void onServiceBinded();

	public abstract void setServiceHelper();

	@Override
	protected void onPause() {
		super.onPause();
		serviceHelper.deleteObserver(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		serviceHelper.deleteObserver(this);
		serviceHelper.unBindService(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		serviceHelper.addObserver(this);
	}

}
