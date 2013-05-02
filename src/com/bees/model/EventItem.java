/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bees.utils.Utils;

/**
 * Event.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class EventItem implements ISerializable {

	/** The unique ID for this event. */
	String id;

	/** The page URL for this event on eventful.com. */
	String url;

	/** The event title. */
	String title;

	/** The event description. */
	String description;

	/** The event start time, in ISO 8601 format (e.g. "2005-03-01 19:00:00"). */
	String start_time;

	/** The event stop time, in ISO 8601 format (e.g. "2005-03-01 19:00:00"). */
	String stop_time;

	/** The venue ID. */
	String venue_id;

	/** The page URL for this venue on eventful.com. */
	String venue_url;

	/** The venue name. */
	String venue_name;

	/**
	 * Whether or not the venue name should be displayed in certain
	 * circumstances. Eventful's notion of a venue is a bit broader than you
	 * might think. For example, events for which only the postal code is known
	 * have a venue named "Postal code 46311, US", for instance. In these cases,
	 * a traditional address block looks a bit unusual, and Eventful chooses not
	 * to display the venue name in these cases. Y ou may wish to do the same,
	 * and the venue_display parameter allows you to do that.
	 */
	String venue_display;

	/** The venue address. */
	String venue_address;

	/** The venue city_name. */
	String city_name;

	/** The venue state/province/other name. */
	String region_name;

	/** The venue postal code. */
	String postal_code;

	/** The venue country name. */
	String country_name;

	/** If true, this event lasts all day. */
	String all_day;

	/** The venue latitude. */
	String latitude;

	/** The venue longitude. */
	String longitude;

	/** Contain image details */
	Image image;

	/** Performers at this event */
	Performer[] performers;

	public EventItem() {
	}

	public void deserialise(JSONObject source) throws JSONException {
		if (source.has("id")) {
			id = source.getString("id");
		}

		if (source.has("url")) {
			url = source.getString("url");
		}

		if (source.has("title")) {
			title = source.getString("title");
		}

		if (source.has("description")) {
			description = source.getString("description");
		}

		if (source.has("start_time")) {
			start_time = source.getString("start_time");
		}

		if (source.has("stop_time")) {
			stop_time = source.getString("stop_time");
		}

		if (source.has("venue_id")) {
			venue_id = source.getString("venue_id");
		}

		if (source.has("venue_url")) {
			venue_url = source.getString("venue_url");
		}

		if (source.has("venue_name")) {
			venue_name = source.getString("venue_name");
		}

		if (source.has("venue_display")) {
			venue_display = source.getString("venue_display");
		}

		if (source.has("venue_address")) {
			venue_address = source.getString("venue_address");
		}

		if (source.has("city_name")) {
			city_name = source.getString("city_name");
		}

		if (source.has("region_name")) {
			region_name = source.getString("region_name");
		}

		if (source.has("postal_code")) {
			postal_code = source.getString("postal_code");
		}

		if (source.has("country_name")) {
			country_name = source.getString("country_name");
		}

		if (source.has("all_day")) {
			all_day = source.getString("all_day");
		}

		if (source.has("latitude")) {
			latitude = source.getString("latitude");
		}

		if (source.has("longitude")) {
			longitude = source.getString("longitude");
		}

		if (source.has("image")) {
			try {
				image = new Image();
				image.deserialise(source.getJSONObject("image"));
			} catch (JSONException jsne) {
				image = null;
			}
		}

		if (source.has("performers")) {
			try {
				performers = extractPerformers(source
						.getJSONObject("performers"));
			} catch (JSONException jsne) {
			}
		}
	}

	/** @return The unique ID for this event. */
	public String getId() {
		return this.id;
	}

	/** @return The page URL for this event on eventful.com. */
	public String getUrl() {
		return this.url;
	}

	/** @return The event title. */
	public String getTitle() {
		return this.title;
	}

	/** @return The event description. */
	public String getDescription() {
		return this.description.trim();
	}

	/**
	 * @return The event start time, in ISO 8601 format (e.g.
	 *         "2005-03-01 19:00:00").
	 */
	public String getStartTime() {
		return this.start_time;
	}

	/**
	 * Sets the start time for this event
	 * 
	 * @param start_time
	 *            A temptative value when not specified
	 */
	public void setStartTime(String start_time) {
		this.start_time = start_time;
	}

	/**
	 * @return The event stop time, in ISO 8601 format (e.g.
	 *         "2005-03-01 19:00:00").
	 */
	public String getStopTime() {
		return this.stop_time;
	}

	/**
	 * Sets the stop time for this event
	 * 
	 * @param stop_time
	 *            A temptative value when not specified
	 */
	public void setStopTime(String stop_time) {
		this.stop_time = stop_time;
	}

	/** @return The venue ID. */
	public String getVenueId() {
		return this.venue_id;
	}

	/** @return The page URL for this venue on eventful.com. */
	public String getVenueUrl() {
		return this.venue_url;
	}

	/** @return The venue name. */
	public String getVenueName() {
		return this.venue_name;
	}

	/**
	 * Whether or not the venue name should be displayed in certain
	 * circumstances. Eventful's notion of a venue is a bit broader than you
	 * might think. For example, events for which only the postal code is known
	 * have a venue named "Postal code 46311, US", for instance. In these cases,
	 * a traditional address block looks a bit unusual, and Eventful chooses not
	 * to display the venue name in these cases. Y ou may wish to do the same,
	 * and the venue_display parameter allows you to do that.
	 */
	public boolean getVenueDisplay() {
		return this.venue_display.equals("0") ? false : true;
	}

	/** The venue address. */
	public String getVenueAddress() {
		return this.venue_address;
	}

	/** The venue city_name. */
	public String getCityName() {
		return this.city_name;
	}

	/** The venue state/province/other name. */
	public String getRegionName() {
		return this.region_name;
	}

	/** The venue postal code. */
	public String getPostalCode() {
		return this.postal_code;
	}

	/** The venue country name. */
	public String getCountryName() {
		return this.country_name;
	}

	/** If true, this event lasts all day. */
	public boolean isAllDay() {
		return this.all_day.equals("0") ? false : true;
	}

	/** The venue latitude. */
	public double getLatitude() {
		return Utils.isEmpty(this.latitude) ? -1 : Double
				.valueOf(this.latitude);
	}

	/** The venue longitude. */
	public double getLongitude() {
		return Utils.isEmpty(this.longitude) ? -1 : Double
				.valueOf(this.longitude);
	}

	/** Contain all provided image details */
	public Image getImage() {
		return this.image;
	}

	/** Performers at this event */
	public Performer[] getPerformers() {
		return this.performers;
	}

	public boolean hasLocation() {
		return (getLatitude() != -1 && getLongitude() != -1);
	}
	
	public String getLocation(){
		StringBuffer l = new StringBuffer();
		l.append(getLatitude());
		l.append(",");
		l.append(getLongitude());
		return l.toString();
	}

	private Performer[] extractPerformers(JSONObject source)
			throws JSONException {

		if (source != null) {
			if (source.has("performers")) {
				try {
					JSONArray performerArray = source
							.getJSONArray("performers");
					Performer[] performers = new Performer[performerArray
							.length()];

					for (int i = 0; i < performerArray.length(); i++) {
						performers[i] = new Performer();
						performers[i].deserialise((JSONObject) performerArray
								.get(i));
					}

				} catch (Exception ex) {
				}
				return performers;
			}
		}
		return null;
	}
}
