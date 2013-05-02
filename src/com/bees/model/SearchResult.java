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
 * SearchResult.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public abstract class SearchResult {

	/**
	 * The total number of events matching this search. This can be an
	 * approximation if we wish.
	 */
	protected int total_items;

	/** The number of events per "page" returned. */
	protected int page_size;

	/** The number of "pages" of output. */
	protected int page_count;

	/** The current "page" number. */
	protected int page_number;

	/**
	 * The number of events returned in this block. This is not necessarily the
	 * same as page_size. The last page, for instance, will probably not be a
	 * full page.
	 */
	protected String page_items;

	/** The item number of the first element on this page. */
	protected String first_item;

	/** The item number of the last element on this page. */
	protected String last_item;

	/** The fractional number of seconds it took to satisfy this query. */
	protected float search_time;

	public void deserialise(JSONObject source) throws JSONException {

		last_item = source.getString("last_item");
		total_items = source.getInt("total_items");
		first_item = source.getString("first_item");
		page_number = source.getInt("page_number");
		page_size = source.getInt("page_size");
		page_items = source.getString("page_items");
		page_count = source.getInt("page_count");
	}

	public int getTotalItems() {
		return total_items;
	}

	public int getPageSize() {
		return page_size;
	}

	public int getPageCount() {
		return page_count;
	}

	public int getPageNumber() {
		return page_number;
	}

	public String getPageItems() {
		return page_items;
	}

	public String getFirstItem() {
		return first_item;
	}

	public String getLastItem() {
		return last_item;
	}

	public float getSearchTime() {
		return search_time;
	}
}
