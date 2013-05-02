/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees;

import java.util.ArrayList;

import com.bees.service.IEventListener;
import com.bees.service.Processor;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * AppService.java
 * 
 * Allows the application to executes operations in the background even when the
 * front launcher activity is gone.
 * 
 * FORWARD PATH :- Receives an intent sent by the Service Helper and starts the
 * corresponding REST method.
 * 
 * RETURN PATH :- Handles the processor call back and invokes the Service Helper
 * binder callback.
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class AppService extends Service implements Runnable {

	/** Handles the I/O for the request (Database & network) */
	private Processor processor;

	/** true when service is running */
	private boolean isRunning;

	private final ArrayList<Intent> queue;

	/** Denotes the flag that holds the command attribute in every intent */
	public static final String INTENT_COMMAND = "command";

	/** Denotes the flag that holds the use cache attribute in every intent */
	public static final String INTENT_USE_CACHE = "usecache";

	private final IBinder binder = new LocalBinder();

	public AppService() {
		queue = new ArrayList<Intent>();
		isRunning = true;
		new Thread(this).start();
	}

	private void initProcessor(ContentResolver resolver) {
		processor = new Processor();
		processor.setContentResolver(resolver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class LocalBinder extends Binder {
		public IEventListener binder_callback;

		public AppService getService() {
			return AppService.this;
		}

		public void setBinderCallback(IEventListener binder_callback) {
			initProcessor(AppService.this.getContentResolver());
			this.binder_callback = binder_callback;
		}

		public void processQueue(int command, String uri, boolean useCache) {
			addToQueue(command, uri, useCache);
		}
		
		public void emptyQueueItems(){
			emptyQueue();
		}
	}

	public void run() {
		Intent element = null;
		while (isRunning) {
			synchronized (queue) {
				if (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					element = queue.remove(0);
				}
			}

			if (element != null) {
				processor
						.getContent(
								element.getIntExtra(AppService.INTENT_COMMAND,
										0),
								element.getAction(),
								((LocalBinder) binder).binder_callback,
								element.getIntExtra(
										AppService.INTENT_USE_CACHE, -1) == 1 ? true
										: false);

				element = null;
			}
		}
	}
	
	private void emptyQueue(){
		synchronized (queue) {
			queue.clear();
		}
	}

	private void addToQueue(int command, String uri, boolean useCache) {
		synchronized (queue) {
			Intent intent = new Intent(uri);
			intent.putExtra(INTENT_COMMAND, command);
			intent.putExtra(INTENT_USE_CACHE, useCache ? 1 : 0);
			queue.add(intent);
			queue.notify();
		}
	}
}
