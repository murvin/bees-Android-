/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * EventSearchResult.java
 * 
 * @author Murvin Bhantooa
 * @date 25/04/2012
 */
public class EventSearchResult extends SearchResult {

	/** An array of events. */
	private ArrayList<EventItem> events;

	public void deserialise(JSONObject source) throws JSONException {
		super.deserialise(source);

		if (total_items > 1) {
			JSONObject eventsTag = source.getJSONObject("events");
			if (eventsTag != null && !eventsTag.equals("null")) {
				if (eventsTag.has("event")) {
					JSONArray eventsArray = eventsTag.getJSONArray("event");

					events = new ArrayList<EventItem>();
					for (int i = eventsArray.length() - 1; i >= 0; i--) {
						EventItem event = new EventItem();
						event.deserialise((JSONObject) eventsArray.get(i));
						events.add(event);
					}
				}
			}
		}
	}

	public ArrayList<EventItem> getEvents() {
		return events;
	}
}