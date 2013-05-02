/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */
package com.bees.io;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * ConnectionWorker.java
 * 
 * This object is an implementation of the dedicated worker thread concept A
 * synchronised queue is used to hold GenericConnection tasks and the thread
 * wakes up upon addition of tasks in the queue. When all tasks are completed
 * the worker goes in the wait() state releasing resources.
 * 
 * The elements are then processed in terms of their relative priorities instead
 * of a FIFO usual approach.
 * 
 * @author Murvin Bhantooa
 * @date 25/04/2012
 */
public class ConnectionWorker implements Runnable {

	/** Flag to quit worker thread */
	private boolean isQuit;

	/** Convenience collection to hold network task objects */
	private final ArrayList<GenericRequest> queue;

	/** The actual object that is retrieved from the queue */
	private GenericRequest request;

	/** To determine if this worker is busy or not */
	private boolean isIdle = true;

	private HttpClient httpClient;

	public ConnectionWorker() {
		queue = new ArrayList<GenericRequest>();
		httpClient = createHttpClient();
		new Thread(this).start();
	}

	public boolean isIdle() {
		return isIdle;
	}

	public void run() {
		try {
			while (!isQuit) {
				request = null;

				synchronized (queue) {
					if (queue.size() > 0) {
						request = queue.remove(0);
					} else {
						try {
							isIdle = true;
							queue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				isIdle = false;

				if (request != null) {
					try {
						// Log.d(TAG, "STARTING REQUEST " + request.getURI());
						HttpResponse response = httpClient.execute(request);
						request.updateListener(response);
					} catch (Exception ex) {
						ex.printStackTrace();
						request.updateListener(ex);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Overloaded behaviour to allow for a priority and another argument
	 * 
	 * @param o
	 *            The object to process
	 * @return true if object is queued
	 */
	public boolean addToQueue(GenericRequest request) {
		synchronized (queue) {
			if (!isQuit) {
				queue.add(request);
				queue.notify();
				return true;
			}
			return false;
		}
	}

	/**
	 * Used to quit current network task
	 */
	public void quit() {
		synchronized (queue) {
			isQuit = true;
			queue.notify();
		}
	}

	/**
	 * Used to empty the whole queue and thus cancel all pending tasks
	 */
	public void emptyQueue() {
		synchronized (queue) {
			queue.clear();
			queue.notify();
		}
	}

	/**
	 * @return the current size of the <code>GenericConnection</code> queue.
	 */
	public int getQueueSize() {
		synchronized (queue) {
			return queue.size();
		}
	}

	/**
	 * Method to cancel the current task independent of its nature
	 */
	public void cancel() throws Exception {
		if (request != null) {
			request.abort();
		}
	}

	private HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(conMgr, params);

	}
}
