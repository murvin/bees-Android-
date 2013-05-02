/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.io;

import org.apache.http.HttpResponse;

/**
 * IConnectionListener.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public interface IConnectionListener {

	void reportException(int command, String uri, Exception exception,
			Object param);

	void reportCompletion(int command, String uri, HttpResponse response,
			Object param);

}
