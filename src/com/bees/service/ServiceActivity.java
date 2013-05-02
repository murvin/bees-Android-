/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;

/**
 * ServiceActivity.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public abstract class ServiceActivity extends Activity implements IServiceActivity {

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
