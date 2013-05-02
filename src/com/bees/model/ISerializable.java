/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ISerializable.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public interface ISerializable {
	void deserialise(JSONObject source) throws JSONException;
}
