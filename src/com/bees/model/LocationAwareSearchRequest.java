/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@SuppressWarnings("serial")
public abstract class LocationAwareSearchRequest extends SearchRequest {
	// UNITS TAG CONSTANTS REPRESENTATION //
	public static final String MI_UNITS_TAG = "mi";

	public static final String KM_UNITS_TAG = "km";

	LocationAwareSearchRequest(String url) {
		super(url);
	}

	/**
	 * A location name to use in filtering the search results. Locations in the
	 * form "San Diego", "San Diego, TX", "London, United Kingdom", and
	 * "Calgary, Alberta, Canada" are accepted, as are postal codes ("92122")
	 * and venue IDs ("V0-001-000268633-5"). Full addresses
	 * ("1 Infinite Loop, Cupertino, CA") and common geocoordinate formats
	 * ("32.746682, -117.162741") are also accepted, but the "within" parameter
	 * is recommended in order to set a search radius. (optional)
	 * 
	 * @param location
	 */
	public void setLocation(String location) {
		try {
			location = URLEncoder.encode(location, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		put("&location=", location);
	}

	/**
	 * If within is set and the "location" parameter resolves to a specific
	 * geolocation, the search will be restricted to the specified radius. If
	 * the "location" parameter does not resolve to a specific location, this
	 * parameter is ignored. (optional)
	 */
	public void setWithin(String within) {
		if (within == null) {
			throw new IllegalArgumentException();
		}
		put("&within=", "" + within);
	}

	/**
	 * One of "mi" or "km", the units of the "within" parameter. Defaults to
	 * "mi".(optional)
	 */
	public void setUnits(String units) {
		if (!units.equals(MI_UNITS_TAG) && !units.equals(KM_UNITS_TAG)) {
			throw new IllegalArgumentException();
		}

		put("&units=", units);
	}
}