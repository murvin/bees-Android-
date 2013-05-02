/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.io;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * GenericRequest.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class GenericRequest extends HttpRequestBase {

	/** The method used (GET, POST, PUT or other) */
	private String method;

	/** A command that identifies this request */
	private int command;

	/** The request URI */
	private String uri;

	/** Optional parameter sent with this request */
	private Object param;

	/** Listener registered for updates */
	private IConnectionListener listener;

	public GenericRequest(int command, String uri) throws Exception {
		this.command = command;
		this.uri = uri;
		setURI(new URI(uri));
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public void get(IConnectionListener listener) {
		this.listener = listener;
		method = "GET";
	}

	public void updateListener(Exception ex) {
		if (listener != null) {
			listener.reportException(command, uri, ex, param);
		}
	}

	public void updateListener(HttpResponse response) {
		if (listener != null) {
			listener.reportCompletion(command, uri, response, param);
		}
	}

	@Override
	public String getMethod() {
		return method;
	}
}
