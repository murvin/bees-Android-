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

/**
 * EventSearch.java
 * 
 * @author Murvin Bhantooa
 * @date 25/04/2012
 */
@SuppressWarnings("serial")
public class EventSearchRequest extends LocationAwareSearchRequest {
	// / DATE TAG CONSTANTS REPRESENTATION //
	public static final String ALL_DATE_TAG = "All";

	public static final String FUTURE_DATE_TAG = "Future";

	public static final String PAST_DATE_TAG = "Past";

	public static final String TODAY_DATE_TAG = "Today";

	public static final String LAST_WEEK_DATE_TAG = "Last Week";

	public static final String THIS_WEEK_DATE_TAG = "This Week";

	public static final String NEXT_WEEK_DATE_TAG = "Next Week";

	// SORT_ORDER TAG CONSTANTS REPRESENTATION //
	public static final String DATE_SORTORDER_TAG = "date";

	public static final String RELEVANCE_SORTORDER_TAG = "relevance";

	public static final String VENUENAME_SORTORDER_TAG = "venue_name";

	// MATURE TAG CONSTANTS REPRESENTATION //
	public static final String ALL_MATURE_TAG = "all";

	public static final String NORMAL_MATURE_TAG = "normal";

	public static final String SAFE_MATURE_TAG = "safe";

	public EventSearchRequest() {
		super("events/search?");
	}

	/**
	 * Limit this list of results to a date range, specified by label or exact
	 * range. Currently supported labels include: "All", "Future", "Past",
	 * "Today", "Last Week", "This Week", "Next week", and months by name, e.g.
	 * "October". Exact ranges take the form 'YYYYMMDDHH-YYYYMMDDHH', e.g.
	 * '2008042500-2008042723'. (optional)
	 */
	public void setDate(String date) {
		put("&date=", date);
	}

	/**
	 * Limit the search results to this category ID. See /categories/list for a
	 * list of categories and their IDs. (optional)
	 */
	public void setCategory(String category) {
		try {
			category = URLEncoder.encode(category, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		put("&category=", category);
	}

	/**
	 * Sets the level of filtering for events marked as "mature" due to keywords
	 * in the title or description. One of 'all' (no filtering), 'normal', or
	 * 'safe'. 'Normal' mature filtering consists of words that are clearly
	 * profanities and inappropriate for younger audiences. 'Safe' mature
	 * filtering consists of all normal mature filtered words, as well as other
	 * terms that may be used as inappropriate innuendo. A 'safe' filter may
	 * eliminate events that are benign in nature, but contain questionable
	 * content based on keywords. Defaults to 'all'. (optional)
	 */
	public void setMature(String mature) {
		if (!mature.equalsIgnoreCase(NORMAL_MATURE_TAG)
				&& !mature.equalsIgnoreCase(SAFE_MATURE_TAG)
				&& !mature.equalsIgnoreCase(ALL_DATE_TAG)) {
			throw new IllegalArgumentException();
		}
		put("&mature=", mature);
	}

	/**
	 * One of 'popularity', 'date', 'title', 'relevance', or 'venue_name'.
	 * Default is 'date'. (optional)
	 */
	public void setSortOrder(String sort_order) {
		if (!sort_order.equalsIgnoreCase(DATE_SORTORDER_TAG)
				&& (!sort_order.equalsIgnoreCase(RELEVANCE_SORTORDER_TAG))
				&& (!sort_order.equalsIgnoreCase(VENUENAME_SORTORDER_TAG) && (!sort_order
						.equalsIgnoreCase(POPULARITY_SORTORDER_TAG)))) {
			throw new IllegalArgumentException();
		}
		put("&sort_order=", sort_order);
	}
}
