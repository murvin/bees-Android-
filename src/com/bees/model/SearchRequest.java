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
import java.util.Enumeration;
import java.util.Hashtable;

@SuppressWarnings("serial")
public abstract class SearchRequest extends Hashtable<String, String> {

	// SORT_ORDER TAG CONSTANTS REPRESENTATION //
	/**
	 * Formula based on all the interactions people have events, venues,
	 * performers, and demands on Eventful.com, combined with our proprietary
	 * recommendation engine
	 */
	public static final String POPULARITY_SORTORDER_TAG = "popularity";

	// SORT_DIRECTION CONSTANTS REPRESENTATION //
	public static final String ASC_SORTDIRECTION_TAG = "ascending";

	public static final String DESC_SORTDIRECTION_TAG = "descending";

	public static final String APP_KEY = "RcL4hHmPStVHfWGz"; // "PvNVrKLmnp39DR68";

	/** Holds the GET request attributes */
	protected StringBuffer data;

	public SearchRequest(String url) {
		data = new StringBuffer("http://api.eventful.com/json/").append(url)
				.append("app_key=").append(APP_KEY);
	}

	/** The search keywords. (optional) */
	public void setKeywords(String keywords) {
		try {
			keywords = URLEncoder.encode(keywords, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		put("&keywords=", keywords);
	}

	/**
	 * If count_only is set, an abbreviated version of the output will be
	 * returned. Only total_items and search_time elements are included in the
	 * result. (optional)
	 */
	public void setCountOnly(boolean count_only) {
		put("&count_only=", count_only ? "1" : "0");
	}

	public abstract void setSortOrder(String sort_order);

	/**
	 * One of 'ascending' or 'descending'. Default varies by sort_order.
	 * (optional)
	 */
	public void setSortDirection(String sort_direction) {
		if (!sort_direction.equals(ASC_SORTDIRECTION_TAG)
				&& !sort_direction.equals(DESC_SORTDIRECTION_TAG)) {
			throw new IllegalArgumentException();
		}

		put("&sort_direction", sort_direction);
	}

	/**
	 * The desired number of results per page. (optional)
	 */
	public void setPageSize(int page_size) {
		if (page_size < 0) {
			throw new IllegalArgumentException();
		}

		put("&page_size=", "" + page_size);
	}

	/**
	 * The desired page number. (optional)
	 */
	public void setPageNumber(int page_number) {
		if (page_number < 0) {
			throw new IllegalArgumentException();
		}

		put("&page_number=", "" + page_number);
	}

	public String toString() {
		for (Enumeration<String> enu = keys(); enu.hasMoreElements();) {
			String key = enu.nextElement();
			data.append(key.toString()).append(get(key));
		}
		return data.toString();
	}
}
