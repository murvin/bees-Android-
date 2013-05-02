/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.utils;

import java.util.Calendar;
import java.util.TimeZone;

import com.bees.BeesServiceHelper;
import com.bees.R;
import com.bees.model.EventItem;
import com.bees.model.EventSearchRequest;
import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Utills.java
 * 
 * @author MURVIN BHANTOOA
 * @date 02 May 2012
 */
public class Utils {
	private Utils() {
	}

	public static String getPreviewEventAddress(EventItem event) {
		StringBuffer str = new StringBuffer();
		if (!isEmpty(event.getVenueName())) {
			str.append(event.getVenueName());
			str.append("\n");
		}

		if (!isEmpty(event.getVenueAddress())) {
			str.append(event.getVenueAddress());
		} else {
			if (!isEmpty(event.getCityName())) {
				str.append(event.getCityName()).append("\n");
			} else if (!isEmpty(event.getRegionName())) {
				str.append(event.getRegionName()).append("\n");
			}

			if (!isEmpty(event.getPostalCode())) {
				str.append(event.getPostalCode()).append("\n");
			}

			if (!isEmpty(event.getCountryName())) {
				str.append(event.getCountryName());
			}
		}
		return str.toString();
	}

	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		} else {
			if (str.trim().equals("")) {
				return true;
			} else if (str.trim().equalsIgnoreCase("null")) {
				return true;
			}
		}
		return false;
	}

	public static String getEventDistance(EventItem event, String unit,
			GeoPoint myLocation) {
		StringBuffer str = new StringBuffer();
		String dist = null;
		if (event.hasLocation()) {
			float myLat = (float) (myLocation.getLatitudeE6() / 1E6);
			float myLong = (float) (myLocation.getLongitudeE6() / 1E6);

			double distance = getDistanceKm(event.getLatitude(),
					event.getLongitude(), myLat, myLong);
			str.append(distance);
			dist = str.toString();
			if (dist.length() > 5) {
				dist = dist.substring(0, 5);
			}

			dist = dist + unit;
		}
		return dist;
	}

	private static double getDistanceKm(double lat1, double long1, double lat2,
			double long2) {
		double d2r = Math.PI / 180;
		double dlong = (long2 - long1) * d2r;
		double dlat = (lat2 - lat1) * d2r;
		double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(lat1 * d2r)
				* Math.cos(lat2 * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;

		return d;
	}

	public static String getFormattedDate(EventItem event) {
		String start = event.getStartTime(); // the event start time, in ISO
												// 8601 format
												// (e.g."2005-03-01 19:00:00"
		if (!isEmpty(start)) {
			return getIETFDate(start);
		}
		return "";
	}

	public static String getIETFDate(String date) {
		StringBuffer displayDate = new StringBuffer();
		// 2009-12-19 11:00:00
		String[] dateTime = date.split(" ");
		String[] dates = dateTime[0].split("-");
		String[] hours = dateTime[1].split(":");
		displayDate.append(dates[2]).append(" ");
		displayDate.append(getMMMValue(dates[1].trim())).append(" ");
		displayDate.append(dates[0]);
		displayDate.append(", ").append(hours[0]).append(":").append(hours[1]);

		return displayDate.toString();
	}

	public static long getDateToMilliSeconds(String d) {
		String[] dateTime = d.split(" ");
		String[] dates = dateTime[0].split("-");
		String[] hours = dateTime[1].split(":");

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.set(Integer.valueOf(dates[0]), Integer.valueOf(dates[1]) - 1,
				Integer.valueOf(dates[2]), Integer.valueOf(hours[0]),
				Integer.valueOf(hours[1]), 0);

		return c.getTimeInMillis();
	}

	public static String getDisplayDate(String date) {
		StringBuffer displayDate = new StringBuffer();
		// 2009-12-19 11:00:00
		String[] dateTime = date.split(" ");

		String[] dates = dateTime[0].split("-");
		displayDate.append(dates[2]).append("th ")
				.append(getMMMValue(dates[1].trim())).append(" at ")
				.append(dateTime[1].substring(0, 5));

		return displayDate.toString();
	}

	private static String getMMMValue(String month) {
		if (month.equals("12")) {
			return "Dec";
		} else if (month.equals("11")) {
			return "Nov";
		} else if (month.equals("10")) {
			return "Oct";
		} else if (month.equals("09")) {
			return "Sep";
		} else if (month.equals("08")) {
			return "Aug";
		} else if (month.equals("07")) {
			return "Jul";
		} else if (month.equals("06")) {
			return "Jun";
		} else if (month.equals("05")) {
			return "May";
		} else if (month.equals("04")) {
			return "Apr";
		} else if (month.equals("03")) {
			return "Mar";
		} else if (month.equals("02")) {
			return "Feb";
		} else if (month.equals("01")) {
			return "Jan";
		} else
			return "";

	}

	public static int getDisplayWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getDisplayHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static boolean isPortrait(Context context) {
		return getDisplayHeight(context) > getDisplayWidth(context);
	}

	public static String getSearchTitle(Context context, String keyword,
			int pageNum, int totalPages) {
		StringBuffer str = new StringBuffer();
		str.append(context.getResources().getString(R.string.event))
				.append(" ")
				.append(context.getResources().getString(R.string.results));
		str.append(" ")
				.append(context.getResources().getString(R.string.for_word))
				.append(" ").append(keyword);
		if (pageNum != -1) {
			str.append(" (")
					.append(context.getResources().getText(R.string.page))
					.append(" ").append(pageNum).append(" ")
					.append(context.getResources().getText(R.string.of));
			str.append(" ").append(totalPages).append(")");
		}
		return str.toString();
	}

	public static void showGpsOptions(Context context) {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(gpsOptionsIntent);
	}

	public static GeoPoint getLocationToGeopoint(Location location) {
		return location == null ? null : new GeoPoint(
				(int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
	}

	public static Location getGeoPointToLocation(GeoPoint geoPoint) {
		float latitude = (float) (geoPoint.getLatitudeE6() / 1E6);
		float longitude = (float) (geoPoint.getLongitudeE6() / 1E6);

		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(latitude);
		location.setLongitude(longitude);

		return location;
	}

	public static String getGeoPointToLatLongStr(GeoPoint geoPoint) {
		float latitude = (float) (geoPoint.getLatitudeE6() / 1E6);
		float longitude = (float) (geoPoint.getLongitudeE6() / 1E6);

		StringBuffer str = new StringBuffer();
		str.append(latitude).append(Constants.COMMA).append(longitude);
		return str.toString();
	}

	public static GeoPoint getLatLongStrToGeoPoint(String str) {
		if (str != null) {
			int commaIndex = str.indexOf(Constants.COMMA);
			if (commaIndex != -1) {
				String latStr = str.substring(0, commaIndex);
				String lonStr = str.substring(commaIndex + 1);

				float lat = Float.valueOf(latStr);
				float lon = Float.valueOf(lonStr);

				return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
			}
		}
		return null;
	}

	public static void showToastShort(Activity a, String msg) {
		LayoutInflater li = a.getLayoutInflater();
		View layout = li.inflate(R.layout.toast,
				(ViewGroup) a.findViewById(R.id.LinearLayoutCustomToast));
		TextView txtMsg = (TextView) layout
				.findViewById(R.id.textViewToastText);
		txtMsg.setText(msg);

		Toast t = Toast.makeText(a, msg, Toast.LENGTH_SHORT);
		t.setView(layout);
		t.show();
	}

	public static void showToastLong(Activity a, String msg) {
		LayoutInflater li = a.getLayoutInflater();
		View layout = li.inflate(R.layout.toast,
				(ViewGroup) a.findViewById(R.id.LinearLayoutCustomToast));
		TextView txtMsg = (TextView) layout
				.findViewById(R.id.textViewToastText);
		txtMsg.setText(msg);

		Toast t = Toast.makeText(a, msg, Toast.LENGTH_SHORT);
		t.setView(layout);
		t.show();
	}

	public static void showMapAtLatLong(Context activity, String latititude,
			String longitude) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://maps.google.com/maps?f=d&daddr="
				+ latititude + "," + longitude));
		activity.startActivity(intent);
	}

	public static int getPrefIntValue(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(
				Constants.SHARED_PREF, 0);
		return settings.getInt(key, 0);
	}

	public static String getPrefValue(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(
				Constants.SHARED_PREF, 0);
		String val = null;

		if (key.equals(Constants.SHARED_PREF_CATEGORIES_INDEX_KEY)) {
			String[] values = activity.getResources().getStringArray(
					R.array.category_filter);
			val = values[settings.getInt(key, 0)];
		} else if (key.equals(Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY)) {
			String[] values = activity.getResources().getStringArray(
					R.array.date_filter);
			val = values[settings.getInt(key, 0)];
		} else if (key.equals(Constants.SHARED_PREF_KEYWORD_KEY)) {
			val = settings.getString(key, Constants.KEYWORD);
		} else if (key.equals(Constants.SHARED_PREF_PROXIMITY_INDEX_KEY)) {
			String[] values = activity.getResources().getStringArray(
					R.array.radius_filter);
			int index = settings.getInt(key, 0);
			val = values[index];
			val = val.substring(0, val.indexOf(" "));
		} else if (key.equals(Constants.SHARED_PREF_SORT_ORDER_INDEX_KEY)) {
			String[] values = activity.getResources().getStringArray(
					R.array.sort_filter);
			val = values[settings.getInt(key, 0)];
		} else if (key.equals(Constants.SHARED_PREF_MAP_CENTER)) {
			val = settings.getString(key, Constants.MAPCENTER_DEFAULT);
		}

		return val;
	}

	public static String getNextSearchRequest(Activity activity) {
		EventSearchRequest request = new EventSearchRequest();
		request.setPageNumber((BeesServiceHelper.getInstance().getCurrentPage() + 1));
		request.setKeywords(getPrefValue(activity,
				Constants.SHARED_PREF_KEYWORD_KEY));
		request.setLocation(getPrefValue(activity,
				Constants.SHARED_PREF_MAP_CENTER));
		request.setWithin(getPrefValue(activity,
				Constants.SHARED_PREF_PROXIMITY_INDEX_KEY));
		request.setDate(getPrefValue(activity,
				Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY));
		request.setSortOrder(getPrefValue(activity,
				Constants.SHARED_PREF_SORT_ORDER_INDEX_KEY));
		request.setCategory(getPrefValue(activity,
				Constants.SHARED_PREF_CATEGORIES_INDEX_KEY));
		request.setPageSize(Constants.PAGE_SIZE);
		request.setUnits(EventSearchRequest.MI_UNITS_TAG);

		return request.toString();
	}

	public static String getSearchRequest(Activity activity) {
		EventSearchRequest request = new EventSearchRequest();
		request.setKeywords(getPrefValue(activity,
				Constants.SHARED_PREF_KEYWORD_KEY));
		request.setLocation(getPrefValue(activity,
				Constants.SHARED_PREF_MAP_CENTER));
		request.setWithin(getPrefValue(activity,
				Constants.SHARED_PREF_PROXIMITY_INDEX_KEY));
		request.setDate(getPrefValue(activity,
				Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY));
		request.setSortOrder(getPrefValue(activity,
				Constants.SHARED_PREF_SORT_ORDER_INDEX_KEY));
		request.setCategory(getPrefValue(activity,
				Constants.SHARED_PREF_CATEGORIES_INDEX_KEY));
		request.setPageSize(Constants.PAGE_SIZE);
		request.setUnits(EventSearchRequest.MI_UNITS_TAG);

		return request.toString();
	}
}