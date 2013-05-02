/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

/**
 * Event.java
 * 
 * Utility object used to parse information through binder interfaces across
 * objects.
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class Event {

	public static final int TRANSACTION_PENDING = 0x0006;

	public static final int TRANSACTION_COMPLETED = 0x0007;

	public static final int TRANSACTION_FAILED = 0x0008;

	public int command;

	public String uri;

	public int status;

	public Object response;

	public Event(int command, String uri, int status, Object response) {
		this.command = command;
		this.uri = uri;
		this.status = status;
		this.response = response;
	}
}
