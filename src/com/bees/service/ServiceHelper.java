/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.bees.AppService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * ServiceHelper.java
 * 
 * - Prepares and sends the service request. - Check is the method is already
 * pending - Create the request intent - Add the operation type and unique
 * request ID - Add the method specific parameters - Add the binder call back -
 * call startService(Intent) - return the request id
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class ServiceHelper extends Observable {

	private ServiceConnection service_conn;

	/** A handle on the binded service */
	private AppService.LocalBinder service;

	/** Binder call back receiving service updates */
	private IEventListener binder_callback;

	/** Currently binded activity */
	private IServiceActivity activity;

	/** Holds a map of all pending requests (i.e launched and not yet completed) */
	private Map<String, Boolean> pendingRequests;

	public ServiceHelper() {
		pendingRequests = new HashMap<String, Boolean>();
		initBinderCallback();
		createServiceConnection();
	}

	private void initBinderCallback() {
		binder_callback = new IEventListener() {

			public void onEvent(int command, String uri, int status,
					Object response) {
				pendingRequests.remove(uri);
				notifyAllObservers(command, uri, status, response);
			}
		};
	}

	private void notifyAllObservers(int command, String uri, int status,
			Object response) {
		ServiceHelper.this.setChanged();
		ServiceHelper.this.notifyObservers(new Event(command, uri, status,
				response));
	}

	private void createServiceConnection() {
		service_conn = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
			}

			public void onServiceConnected(ComponentName name,
					IBinder bindedService) {
				service = (AppService.LocalBinder) bindedService;
				service.setBinderCallback(binder_callback);
				activity.onServiceBinded();
			}
		};
	}

	public void unBindService(IServiceActivity activity) {
		((Activity) activity).unbindService(service_conn);
	}

	public void bindService(IServiceActivity activity) {
		this.activity = activity;
		((Activity) activity).bindService(new Intent((Activity) activity,
				AppService.class), service_conn, Context.BIND_AUTO_CREATE);
	}

	public void request(int command, String uri, boolean useCache) {
		if (!isMethodPending(uri)) {
			startService(command, uri, useCache);
		} else {
			notifyAllObservers(command, uri, Event.TRANSACTION_PENDING, null);
		}
	}

	/**
	 * Determines if a request is pending.
	 * 
	 * @param key
	 *            The request URI
	 * @return true if request was launched and its status is PENDING
	 */
	private boolean isMethodPending(String key) {
		return pendingRequests.containsKey(key);
	}

	private void startService(int command, String uri, boolean useCache) {
		pendingRequests.put(uri, null);
		service.processQueue(command, uri, useCache);
	}
	
	public void emptyServiceQueue(){
		pendingRequests.clear();
		service.emptyQueueItems();
	}
}
