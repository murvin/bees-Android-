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
 * Performer.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class Performer implements ISerializable {
	String id;
	String url;
	String name;
	String short_bio;
	String long_bio;

	public void deserialise(JSONObject source) throws JSONException {
		if (source.has("id")) {
			id = source.getString("id");
		}

		if (source.has("url")) {
			url = source.getString("url");
		}

		if (source.has("name")) {
			name = source.getString("name");
		}

		if (source.has("short_bio")) {
			short_bio = source.getString("short_bio");
		}
	}

	public String getId() {
		return this.id;
	}

	public String getUrl() {
		return this.url;
	}

	public String getName() {
		return this.name;
	}

	public String getShortBio() {
		return this.short_bio;
	}

	public String getLongBio() {
		return this.long_bio;
	}
}
