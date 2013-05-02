/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.bees.db.AppServiceProviderMetaData;
import com.bees.io.ConnectionWorker;
import com.bees.io.GenericRequest;
import com.bees.io.IConnectionListener;

/**
 * Processor.java
 * 
 * Mirrors the state of server resources in the local database. A request maps
 * to a row in the db.
 * 
 * ID STATUS RESULT
 * 
 * TRANSACTION STATE FLAG - transitional state of the resource (STATE_PENDING or
 * STATE_COMPLETED) STATUS FLAG - (STATUS_SUCCESS, STATUS_ERROR)
 * 
 * RESULTS - holds the last REST method executed in regards to that particular
 * resource
 * 
 * The processor executes before and after the REST methods
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class Processor implements IConnectionListener {

	/** Delegate that handles I/0 operations from the network */
	private ConnectionWorker worker;

	/** Return path call back listener (typically the service) */
	private IEventListener service_listener;

	/** Delegate that handles I/0 operations from the database */
	private ContentResolver contentResolver;

	public Processor() {
		worker = new ConnectionWorker();
	}

	public void setContentResolver(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	/**
	 * Method to insert a row in the database that corresponds to a transaction.
	 * 
	 * @param command
	 *            The command identifier for the transaction
	 * @param uri
	 *            The transaction URI (unique)
	 */
	private void insertRequest(int command, String url, int status,
			byte[] response) {
		ContentValues values = new ContentValues();
		values.put(AppServiceProviderMetaData.Transaction.COMMAND, command);
		values.put(AppServiceProviderMetaData.Transaction.URL,
				getStrAsciiVal(url, command));
		values.put(AppServiceProviderMetaData.Transaction.STATUS, status);
		values.put(AppServiceProviderMetaData.Transaction.RESPONSE, response);

		contentResolver.insert(
				AppServiceProviderMetaData.Transaction.CONTENT_URI, values);
	}

	private Event getEventFromUrl(String url, int command) {
		StringBuffer selectionClause = new StringBuffer();
		selectionClause.append(AppServiceProviderMetaData.Transaction.URL)
				.append("=").append(getStrAsciiVal(url, command));
		String[] selectionArgs = null;
		String[] projection = new String[] {
				AppServiceProviderMetaData.Transaction.COMMAND,
				AppServiceProviderMetaData.Transaction.STATUS,
				AppServiceProviderMetaData.Transaction.RESPONSE };

		Cursor cursor = contentResolver.query(
				AppServiceProviderMetaData.Transaction.CONTENT_URI, projection,
				selectionClause.toString(), selectionArgs, null);
		if (cursor == null || cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToFirst();

		int functionColumn = cursor
				.getColumnIndex(AppServiceProviderMetaData.Transaction.COMMAND);
		int statusColumn = cursor
				.getColumnIndex(AppServiceProviderMetaData.Transaction.STATUS);
		int responseColumn = cursor
				.getColumnIndex(AppServiceProviderMetaData.Transaction.RESPONSE);

		int c = cursor.getInt(functionColumn);
		int status = cursor.getInt(statusColumn);
		byte[] response = cursor.getBlob(responseColumn);

		return new Event(c, url, status, response);
	}

	public void getContent(int command, String url, IEventListener listener,
			boolean usecache) {
		this.service_listener = listener;
		Event event = null;
		if (usecache) {
			event = getEventFromUrl(url, command);
		}

		if (event == null) {
			try {
				GenericRequest request = new GenericRequest(command, url);
				if (usecache) {
					request.setParam(new Boolean(usecache));
				}

				request.get(this);
				worker.addToQueue(request);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			if (service_listener != null) {
				service_listener.onEvent(command, url,
						Event.TRANSACTION_COMPLETED, event.response);
			}
		}
	}

	public void reportException(int command, String uri, Exception exception,
			Object param) {
		if (service_listener != null) {
			service_listener.onEvent(command, uri, Event.TRANSACTION_FAILED,
					exception);
		}
	}

	public void reportCompletion(int command, String url,
			HttpResponse response, Object param) {
		byte[] data = null;
		try {
			data = EntityUtils.toByteArray(response.getEntity());
			if (param != null) {
				insertRequest(command, url, Event.TRANSACTION_COMPLETED, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (service_listener != null) {
			service_listener.onEvent(command, url, Event.TRANSACTION_COMPLETED,
					data);
		}
	}

	private int getStrAsciiVal(String str, int command) {
		int val = 0;
		for (int i = 0; i < str.length(); i++) {
			val += (int) str.charAt(i);
		}
		return val * (command + 1);
	}
}
