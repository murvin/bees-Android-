/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

import com.bees.model.EventSearchRequest;

/**
 * BeesServiceHelper.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class BeesServiceHelper extends ServiceHelper {

	/** Handles building the event search with required parameters */
	private EventSearchRequest eventSearchRequest;

	/** Singleton instance of the service helper */
	public static BeesServiceHelper instance;

	private BeesServiceHelper() {
		eventSearchRequest = new EventSearchRequest();
	}

	public static BeesServiceHelper getInstance() {
		if (instance == null) {
			instance = new BeesServiceHelper();
		}
		return instance;
	}

	public EventSearchRequest getEventSearchRequest() {
		return this.eventSearchRequest;
	}
}
