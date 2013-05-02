/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

import com.bees.model.EventIdVisitor;
import com.bees.model.EventItem;
import com.bees.model.EventSearchResult;
import com.bees.model.Image;
import com.bees.service.MapServiceActivity;
import com.bees.utils.Constants;
import com.bees.utils.Utils;
import com.bees.widgets.EventOverlayItem;
import com.bees.widgets.HorizontialListView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * BeesActivity.java
 * 
 * @author MURVIN BHANTOOA
 * @date 02/05/2012
 */
public class BeesActivity extends MapServiceActivity implements
		AdapterView.OnItemSelectedListener {

	private BaseAdapter eventsAdapter;
	private ArrayList<EventItem> events;

	private MapView mapView;

	/** Search results */
	private List<Overlay> mapOverlays;

	/** Holds the current location */
	protected MyLocationOverlay myLocationOverlay;

	/** true when info window is overlaid on map */
	private boolean isInfoWindowShown;

	private String keyword;

	private TextView txtSubTitle;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		events = BeesServiceHelper.getInstance().getEvents();
		keyword = Utils.getPrefValue(this, Constants.SHARED_PREF_KEYWORD_KEY);
		checkGPSStatus();
		initMap();

		initActionBar();
	}

	private void initActionBar() {
		TextView txtTitle = (TextView) findViewById(R.id.textViewActionBarTitle);
		txtTitle.setText(R.string.menu_help);

		ImageButton btnHome = (ImageButton) findViewById(R.id.imageButtonHome);
		btnHome.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				showDialog(Constants.DIALOG_EXIT);
			}
		});

		LinearLayout l_list = (LinearLayout) findViewById(R.id.linearLayoutList);
		l_list.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startListAtIndex(0);
			}
		});

		LinearLayout l_more = (LinearLayout) findViewById(R.id.linearLayoutMore);
		l_more.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				fetchEventsNext();
			}
		});

		LinearLayout l_help = (LinearLayout) findViewById(R.id.linearLayoutHelp);
		l_help.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(BeesActivity.this, HelpActivity.class));
			}
		});

		LinearLayout l_search = (LinearLayout) findViewById(R.id.linearLayoutSearch);
		l_search.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				showDialog(Constants.DIALOG_SEARCH);
			}
		});

		txtSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (myLocationOverlay != null) {
			myLocationOverlay.disableMyLocation();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateSubTitle();
		if (myLocationOverlay != null) {
			myLocationOverlay.enableMyLocation();
		}

		if (BeesServiceHelper.getInstance().hasNewContent()) {
			BeesServiceHelper.getInstance().setHasNewContent(false);
			events = BeesServiceHelper.getInstance().getEvents();
			updateDisplay();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showDialog(Constants.DIALOG_EXIT);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void updateSubTitle() {
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);
		txtSubTitle.setText(Utils.getSearchTitle(this, settings.getString(
				Constants.SHARED_PREF_KEYWORD_KEY, Constants.KEYWORD),
				BeesServiceHelper.getInstance().getCurrentPage(),
				BeesServiceHelper.getInstance().getTotalPages()));

	}

	static class EventPreviewViewHolder {
		TextView txtEventTitle;
		ImageView imgEvent;
	}

	private void initEventsAdapter() {
		eventsAdapter = new BaseAdapter() {

			public int getCount() {
				return events.size();
			}

			public Object getItem(int position) {
				return events.get(position);
			}

			public long getItemId(int position) {
				return position;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				EventPreviewViewHolder holder;
				EventItem event = (EventItem) getItem(position);
				if (convertView == null) {
					convertView = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.eventpreviewitem, null);
					holder = new EventPreviewViewHolder();
					holder.imgEvent = (ImageView) convertView
							.findViewById(R.id.imageViewLoading);
					holder.txtEventTitle = (TextView) convertView
							.findViewById(R.id.textViewEventTitle);

					convertView.setTag(holder);

				} else {
					holder = (EventPreviewViewHolder) convertView.getTag();
				}

				if (event.getImage() != null) {
					Bitmap bitmap = event.getImage().getBitmapImageBlock();
					if (bitmap != null) {
						holder.imgEvent.setImageBitmap(bitmap);
					} else {
						holder.imgEvent
								.setImageResource(R.drawable.bee_loading);
					}
				} else {
					holder.imgEvent.setImageResource(R.drawable.bee_loading);
				}

				holder.txtEventTitle.setText((event.getTitle() == null ? ""
						: event.getTitle()));

				return convertView;
			}
		};
	}

	private void initHorzList() {
		HorizontialListView listview = (HorizontialListView) findViewById(R.id.listviewEvents);
		listview.setAdapter(eventsAdapter);
		listview.setOnItemSelectedListener(this);
	}

	private void fetchEventsNext() {
		if (BeesServiceHelper.getInstance().hasNextContent()) {
			showDialog(Constants.DIALOG_PROGRESS);

			String uri = Utils.getNextSearchRequest(this);

			serviceHelper.emptyServiceQueue();
			serviceHelper.request(Constants.COMMAND_EVENT_LIST, uri, true);
		} else {
			Utils.showToastLong(this, getString(R.string.no_next_content));
		}
	}

	private void fetchEvents(String keyword) {
		this.keyword = keyword;
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.SHARED_PREF_KEYWORD_KEY, keyword);
		editor.putString(Constants.SHARED_PREF_MAP_CENTER,
				Utils.getGeoPointToLatLongStr(mapView.getMapCenter()));
		editor.commit();

		showDialog(Constants.DIALOG_PROGRESS);

		String uri = Utils.getSearchRequest(this);
		serviceHelper.emptyServiceQueue();
		serviceHelper.request(Constants.COMMAND_EVENT_LIST, uri, true);
	}

	private void fetchAllEventImgs() {
		if (events != null) {
			for (int i = 0; i < events.size(); i++) {
				Image img = events.get(i).getImage();
				if (img != null) {
					if (img.getBitmapImageBlock() == null
							&& img.getBlockUrl() != null) {
						serviceHelper.request(i, img.getBlockUrl(), true);
					}
				}
			}
		}
	}

	private void updateDisplay() {
		handler.post(new Runnable() {

			public void run() {
				updateSubTitle();
				initEventsAdapter();
				initHorzList();
				addEventItems();
			}
		});
		fetchAllEventImgs();
	}

	public void update(Observable observable, Object response) {
		com.bees.service.Event event = (com.bees.service.Event) response;
		if (event.status == com.bees.service.Event.TRANSACTION_COMPLETED) {
			byte[] data = (byte[]) event.response;
			if (event.command == Constants.COMMAND_EVENT_LIST) {
				if (data != null) {
					String res = new String(data);
					EventSearchResult results = new EventSearchResult();
					try {
						results.deserialise(new JSONObject(res));
						events = results.getEvents();

						if (events != null && !events.isEmpty()) {
							BeesServiceHelper.getInstance().setCurrentPage(
									results.getPageNumber());
							BeesServiceHelper.getInstance().setTotalPages(
									results.getPageCount());
							BeesServiceHelper.getInstance().setEvents(events);

							updateDisplay();
						} else {
							handler.post(new Runnable() {
								public void run() {
									Utils.showToastLong(BeesActivity.this,
											getString(R.string.no_events_found));
								}
							});
						}

						removeDialog(Constants.DIALOG_PROGRESS);
					} catch (JSONException e) {
						e.printStackTrace();
						removeDialog(Constants.DIALOG_PROGRESS);
						handler.post(new Runnable() {
							public void run() {
								Utils.showToastLong(BeesActivity.this,
										getString(R.string.event_parsing_error));
							}
						});
					}
				} else {
					removeDialog(Constants.DIALOG_PROGRESS);
					handler.post(new Runnable() {
						public void run() {
							Utils.showToastLong(BeesActivity.this,
									getString(R.string.event_search_timeout));
						}
					});
				}
			} else {
				try {
					if (data != null) {
						EventItem eventItem = null;

						eventItem = BeesServiceHelper.getInstance().getEvents()
								.get(event.command);
						if (eventItem.getImage() != null
								&& eventItem.getImage().getBlockUrl() != null) {
							eventItem.getImage().setImgDataBlock(data);
							handler.post(new Runnable() {
								public void run() {
									eventsAdapter.notifyDataSetChanged();
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (event.status == com.bees.service.Event.TRANSACTION_FAILED) {
			Exception ex = (Exception) event.response;
			ex.printStackTrace();
			if (event.command == Constants.COMMAND_EVENT_LIST) {
				dismissDialog(Constants.DIALOG_PROGRESS);
			}
		}
	}

	@Override
	public void onServiceBinded() {
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);

		if (events == null || events.isEmpty()) {
			fetchEvents(settings.getString(Constants.SHARED_PREF_KEYWORD_KEY,
					Constants.KEYWORD));
		} else {
			updateDisplay();
		}

		fetchAllEventImgs();
	}

	@Override
	public void setServiceHelper() {
		super.serviceHelper = BeesServiceHelper.getInstance();
	}

	public void onItemSelected(AdapterView<?> arg0, View v, int index, long id) {
		startListAtIndex(index);
	}

	private void startListAtIndex(int index) {
		Intent intent = new Intent(BeesActivity.this, EventsActivity.class);
		intent.putExtra(Constants.INDEX, index);
		startActivity(intent);
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case Constants.DIALOG_PROGRESS: {
			dialog = new ProgressDialog(new ContextThemeWrapper(this,
					R.style.AlertDialogCustom));
			StringBuffer msg = new StringBuffer();
			msg.append(getString(R.string.fetching_events))
					.append(" ")
					.append(this.keyword)
					.append(" ")
					.append(getString(R.string.under_category))
					.append(" ")
					.append(Utils.getPrefValue(this,
							Constants.SHARED_PREF_CATEGORIES_INDEX_KEY));
			msg.append(". ").append(getString(R.string.please_wait));
			((ProgressDialog) dialog).setIcon(R.drawable.ic_launcher2);
			dialog.setTitle(getString(R.string.app_name));
			((ProgressDialog) dialog).setMessage(msg.toString());
			break;
		}
		case Constants.DIALOG_EXIT: {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));
			StringBuffer msg = new StringBuffer();
			msg.append("\n");
			msg.append(getResources().getString(R.string.exit_confirm));
			msg.append(" ").append(getString(R.string.app_name)).append("?\n");
			builder.setTitle(getString(R.string.app_name))
					.setIcon(R.drawable.ic_launcher2)
					.setMessage(msg.toString())
					.setPositiveButton(getString(android.R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
								}
							})
					.setNegativeButton(getString(android.R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			dialog = builder.create();
			break;
		}
		case Constants.GPS_SETTINGS_DIALOG: {
			AlertDialog.Builder gps_builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));
			gps_builder
					.setTitle(getString(R.string.app_name))
					.setIcon(R.drawable.ic_launcher2)
					.setMessage(getString(R.string.gps_disabled))
					.setPositiveButton(getString(R.string.settings),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Utils.showGpsOptions(BeesActivity.this);
								}
							})
					.setNegativeButton(getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			dialog = gps_builder.create();
			break;
		}
		case Constants.DIALOG_SEARCH: {
			AlertDialog.Builder gps_builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));
			View v = getLayoutInflater().inflate(R.layout.search, null);
			final EditText txtSearch = (EditText) v
					.findViewById(R.id.editTextSearch);
			gps_builder
					.setTitle(getString(R.string.menu_search))
					.setIcon(R.drawable.ic_launcher2)
					.setView(v)
					.setPositiveButton(getString(R.string.menu_search),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (txtSearch.getText() == null
											|| Utils.isEmpty(txtSearch
													.getText().toString())) {
										Utils.showToastLong(
												BeesActivity.this,
												getString(R.string.enter_event_keyword));
									} else {
										BeesActivity.this.fetchEvents(txtSearch
												.getText().toString());
									}
								}
							})
					.setNegativeButton(getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			dialog = gps_builder.create();
			break;
		}
		}

		return dialog;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void checkGPSStatus() {
		LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showDialog(Constants.GPS_SETTINGS_DIALOG);
		}
	}

	private void initMap() {
		mapView = (MapView) findViewById(R.id.mapViewMain);
		mapView.setClickable(true);
		mapOverlays = mapView.getOverlays();
		mapView.setBuiltInZoomControls(true);
		mapOverlays
				.add(myLocationOverlay = new MyLocationOverlay(this, mapView));
		mapView.getController().setZoom(Constants.INITIAL_ZOOM_LEVEL);

		String lastMapCenter = Utils.getPrefValue(this,
				Constants.SHARED_PREF_MAP_CENTER);
		if (lastMapCenter.equals(Constants.MAPCENTER_DEFAULT)) { // first
																	// application
																	// run
			Location lastKnown = ((LocationManager) getSystemService(LOCATION_SERVICE))
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastKnown == null) {
				lastKnown = ((LocationManager) getSystemService(LOCATION_SERVICE))
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			if (lastKnown != null) {
				handler.postDelayed(new Runnable() {

					public void run() {
						Utils.showToastLong(BeesActivity.this,
								getString(R.string.map_centered_lastknown));
					}
				}, 1000);

				animateToLocation(Utils.getLocationToGeopoint(lastKnown));
			} else {

			}
		} else {
			GeoPoint center = Utils.getLatLongStrToGeoPoint(lastMapCenter);
			if (center != null) {
				handler.postDelayed(new Runnable() {

					public void run() {
						Utils.showToastLong(BeesActivity.this,
								getString(R.string.map_centered_last_search));
					}
				}, 1000);

				animateToLocation(center);
			}
		}
	}

	private void animateToLocation(GeoPoint point) {
		if (point == null) {
			Utils.showToastLong(this, getString(R.string.location_undetermined));
		} else {
			mapView.getController().setCenter(point);
			mapView.getController().animateTo(point);
		}
	}

	private void addEventItems() {
		if (events != null && !events.isEmpty()) {
			EventItemOverlay itemOverlay = new EventItemOverlay(getResources()
					.getDrawable(R.drawable.hivebee));
			for (int i = 0; i < events.size(); i++) {
				EventItem e = events.get(i);
				if (e.hasLocation()) {
					itemOverlay.addOverlay(new EventOverlayItem(new GeoPoint(
							(int) (Double.valueOf(e.getLatitude()) * 1E6),
							(int) (Double.valueOf(e.getLongitude()) * 1E6)), e
							.getId(), e.getTitle(), e.getImage() == null ? null
							: e.getImage().getBitmapImageBlock()));
				}
			}

			mapOverlays.clear();
			mapOverlays.add(itemOverlay);
		}
	}

	public class EventItemOverlay extends ItemizedOverlay<OverlayItem> {

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		private ArrayList<Boolean> isWindowOn = new ArrayList<Boolean>();

		private GeoPoint selectedPoint;

		private Point point;

		private int y_offset = 5;

		private Bitmap eventImage, eventBg, defaultEventImg;

		/** Only drawn when no image is available */
		private String eventTitle;

		/** Paint objects reused for drawing */
		private Paint textPaint;

		private EventIdVisitor eventVisitor;

		public EventItemOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));

			float density = getResources().getDisplayMetrics().density;
			y_offset *= density;

			point = new Point();
			eventBg = BitmapFactory.decodeResource(
					BeesActivity.this.getResources(), R.drawable.shadow);
			defaultEventImg = BitmapFactory.decodeResource(
					BeesActivity.this.getResources(),
					R.drawable.bee_loading_map);

			eventVisitor = new EventIdVisitor(null);

		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, false);
			if (isInfoWindowShown) {
				if (selectedPoint != null) {
					mapView.getProjection().toPixels(selectedPoint, point);
					RectF infoWindowRect = new RectF(0, 0, eventBg.getWidth(),
							eventBg.getHeight());
					RectF infoWindowRectImg = null;

					if (eventImage == defaultEventImg) {
						infoWindowRectImg = new RectF(
								((eventBg.getWidth() - defaultEventImg
										.getWidth())),
								((eventBg.getHeight() - defaultEventImg
										.getHeight())), defaultEventImg
										.getWidth(), defaultEventImg
										.getHeight());
					} else {
						infoWindowRectImg = new RectF(y_offset, y_offset,
								eventBg.getWidth() - (y_offset),
								eventBg.getHeight() - (y_offset));
					}

					int infoWindowOffsetX = point.x
							- (eventImage.getWidth() / 2);
					int infoWindowOffsetY = point.y + y_offset;
					infoWindowRect.offset(infoWindowOffsetX, infoWindowOffsetY);
					infoWindowRectImg.offset(infoWindowOffsetX,
							infoWindowOffsetY);

					canvas.drawBitmap(eventBg, null, infoWindowRect, null);
					canvas.drawBitmap(eventImage, null, infoWindowRectImg, null);

					if (eventImage == defaultEventImg && eventTitle != null) {
						canvas.drawText(eventTitle, infoWindowOffsetX
								+ (y_offset * 2),
								infoWindowOffsetY + (eventBg.getHeight() / 2)
										+ (y_offset * 3), getTextPaint());
					}
				}
			}
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (OverlayItem) (mOverlays.get(i));
		}

		@Override
		public int size() {
			return mOverlays.size();
		}

		public void addOverlay(EventOverlayItem overlay) {
			mOverlays.add((OverlayItem) overlay);
			isWindowOn.add(new Boolean(false));
			populate();
		}

		@Override
		protected boolean onTap(int index) {
			if (isWindowOn.get(index) == true) {
				EventOverlayItem item = (EventOverlayItem) createItem(index);
				handleMapClick(item.getTitle());
			} else {
				EventOverlayItem item = (EventOverlayItem) createItem(index);
				eventImage = defaultEventImg;

				selectedPoint = item.getPoint();
				if (item.getImage() != null) {
					eventImage = item.getImage();
				} else {
					eventVisitor.setEventId(item.getTitle());
					BeesServiceHelper.getInstance().accept(eventVisitor);
					int idx = eventVisitor.getEventIndex();
					EventItem e = BeesServiceHelper.getInstance().getEvents()
							.get(idx);
					if (e.getImage() != null
							&& e.getImage().getBitmapImageBlock() != null) {
						eventImage = e.getImage().getBitmapImageBlock();
						item.setImage(e.getImage().getBitmapImageBlock());
					} else {
						eventImage = defaultEventImg;
					}
				}
				eventTitle = item.getSnippet();
				if (eventTitle != null && eventTitle.length() > 10) {
					eventTitle = eventTitle.substring(0, 10);
					eventTitle = eventTitle + "..";
				}

				resetAllWindowIndices();
				isWindowOn.set(index, new Boolean(true));
				isInfoWindowShown = true;
			}

			return true;
		}

		private void resetAllWindowIndices() {
			for (int i = 0; i < isWindowOn.size(); i++) {
				isWindowOn.set(i, new Boolean(false));
			}
		}

		private Paint getTextPaint() {
			if (textPaint == null) {
				textPaint = new Paint();
				textPaint.setARGB(255, 255, 255, 255);
				textPaint.setTextSize(14);
				textPaint.setAntiAlias(true);
			}
			return textPaint;
		}

		private void handleMapClick(String id) {
			eventVisitor.setEventId(id);
			BeesServiceHelper.getInstance().accept(eventVisitor);
			int index = eventVisitor.getEventIndex();
			startListAtIndex(index);
		}
	}
}