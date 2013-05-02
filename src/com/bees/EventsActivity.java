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
import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bees.model.EventItem;
import com.bees.model.EventSearchRequest;
import com.bees.model.EventSearchResult;
import com.bees.model.Image;
import com.bees.service.ServiceActivity;
import com.bees.utils.Constants;
import com.bees.utils.Utils;
import com.bees.widgets.SnapHorzListView;
import com.bees.widgets.SnapHorzListView.IListListener;
import com.google.android.maps.GeoPoint;
import com.sec.android.ad.AdHubView;
import com.sec.android.ad.info.AdSize;

/**
 * EventsActivity.java
 * 
 * @author MURVIN BHANTOOA
 * @date 02/05/2012
 */
public class EventsActivity extends ServiceActivity implements IListListener,
		OnItemClickListener {

	/** Handle on event details snap able list */
	private SnapHorzListView snapList;

	/** Vertical list components */
	private BaseAdapter eventListAdapter;
	private View settingsView;
	private Spinner spinnerDate, spinnerSort, spinnerRadius, spinnerCategories;
	private ListView eventList;

	private TextView txtSubTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		final int index = getIntent().getIntExtra(Constants.INDEX, 0);

		initActionBar();
		initDisplay(index);
		initAd();
	}

	private void initActionBar() {
		TextView txtTitle = (TextView) findViewById(R.id.textViewActionBarTitle);
		txtTitle.setText(R.string.menu_home);

		ImageButton btnHome = (ImageButton) findViewById(R.id.imageButtonHome);
		btnHome.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		LinearLayout l_getMoreOptions = (LinearLayout) findViewById(R.id.linearLayoutMoreOver);
		registerForContextMenu(l_getMoreOptions);

		LinearLayout l_directions = (LinearLayout) findViewById(R.id.linearLayoutDirections);
		l_directions.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (BeesServiceHelper.getInstance().getEvents() != null
						&& !BeesServiceHelper.getInstance().getEvents()
								.isEmpty()) {
					int index = snapList.getActiveIndex();
					EventItem e = BeesServiceHelper.getInstance().getEvents()
							.get(index);
					if (e.hasLocation()) {
						Utils.showMapAtLatLong(EventsActivity.this,
								"" + e.getLatitude(), "" + e.getLongitude());
					} else {
						Utils.showToastLong(EventsActivity.this,
								getString(R.string.event_has_no_location));
					}
				} else {
					Utils.showToastLong(EventsActivity.this,
							getString(R.string.empty_list_option));
				}
			}
		});

		LinearLayout l_more = (LinearLayout) findViewById(R.id.linearLayoutMore);
		l_more.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				fetchEventsNext();
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

	private void initAd() {
		AdHubView adhubView;
		adhubView = (AdHubView) findViewById(R.id.AdLayout);
		adhubView.init(this, "xv0c000000006p", Utils.isPortrait(this) ? AdSize.TABLET_728x90 : AdSize.TABLET_480x60);
		adhubView.setRefreshRate(12 * 1000);
		adhubView.startAd();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, R.string.menu_calendar, 0,
				getString(R.string.menu_calendar));
		menu.add(0, R.string.menu_share, 0, getString(R.string.menu_share));

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.string.menu_calendar) {
			insertCalendarEntry();
		} else if (item.getItemId() == R.string.menu_share) {
			if (BeesServiceHelper.getInstance().getEvents() != null
					&& !BeesServiceHelper.getInstance().getEvents().isEmpty()) {
				int index = snapList.getActiveIndex();
				shareEvent(BeesServiceHelper.getInstance().getEvents()
						.get(index < 0 ? 0 : index));
				return true;
			} else {
				Utils.showToastLong(this, getString(R.string.empty_list_option));
				return true;
			}
		}

		return false;
	}

	private void initDisplay(final int index) {
		initSettingsView();
		initFragment(index);
		initDetailsLayout();
		updateSubTitle();

		handler.postDelayed(new Runnable() {

			public void run() {
				snapList.setActiveIndex(index);
			}
		}, 500);

		fetchAllEventImgs();
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
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.SHARED_PREF_KEYWORD_KEY, keyword);
		editor.commit();

		showDialog(Constants.DIALOG_PROGRESS);

		String uri = Utils.getSearchRequest(this);
		serviceHelper.emptyServiceQueue();
		serviceHelper.request(Constants.COMMAND_EVENT_LIST, uri, true);
	}

	private void fetchAllEventImgs() {
		ArrayList<EventItem> events = BeesServiceHelper.getInstance()
				.getEvents();
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

	private void initSettingsIndices() {
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);

		spinnerDate = (Spinner) settingsView.findViewById(R.id.spinnerDate);
		spinnerDate.setSelection(settings.getInt(
				Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY, 0));
		spinnerDate.setTag(Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY);

		spinnerSort = (Spinner) settingsView.findViewById(R.id.spinnerSortBy);
		spinnerSort.setSelection(settings.getInt(
				Constants.SHARED_PREF_SORT_ORDER_INDEX_KEY, 0));

		spinnerRadius = (Spinner) settingsView.findViewById(R.id.spinnerRadius);
		spinnerRadius.setSelection(settings.getInt(
				Constants.SHARED_PREF_PROXIMITY_INDEX_KEY, 0));

		spinnerCategories = (Spinner) settingsView
				.findViewById(R.id.spinnerCategories);
		spinnerCategories.setSelection(settings.getInt(
				Constants.SHARED_PREF_CATEGORIES_INDEX_KEY, 0));

	}

	private void initSettingsView() {
		if (settingsView == null) {
			settingsView = getLayoutInflater().inflate(R.layout.settings, null);
		}
	}

	private void initFragment(int selectedIndex) {
		if (eventList == null) {
			eventList = (ListView) findViewById(R.id.listViewFragEventList);
			eventList.addHeaderView(settingsView);

			initSettingsIndices();
			setSpinnerListeners();
		}

		if (BeesServiceHelper.getInstance().getEvents() != null) {
			eventListAdapter = new EventListAdapter(this, BeesServiceHelper
					.getInstance().getEvents());

			eventList.setAdapter(eventListAdapter);
			eventList.setSelection(selectedIndex);
			eventList.setOnItemClickListener(this);
			eventListAdapter.notifyDataSetChanged();
		}
	}

	private void commitPref(String key, int value) {
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);

		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private void setSpinnerListeners() {

		spinnerDate.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View v, int index,
					long id) {
				boolean hasChanged = Utils.getPrefIntValue(EventsActivity.this,
						Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY) != index;
				if (hasChanged) {
					commitPref(Constants.SHARED_PREF_DATE_RANGE_INDEX_KEY,
							index);
					fetchEvents(Utils.getPrefValue(EventsActivity.this,
							Constants.SHARED_PREF_KEYWORD_KEY));
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinnerSort.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View v, int index,
					long id) {
				boolean hasChanged = Utils.getPrefIntValue(EventsActivity.this,
						Constants.SHARED_PREF_SORT_ORDER_INDEX_KEY) != index;
				if (hasChanged) {
					commitPref(Constants.SHARED_PREF_SORT_ORDER_INDEX_KEY,
							index);
					fetchEvents(Utils.getPrefValue(EventsActivity.this,
							Constants.SHARED_PREF_KEYWORD_KEY));
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		spinnerRadius.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View v, int index,
					long id) {
				boolean hasChanged = Utils.getPrefIntValue(EventsActivity.this,
						Constants.SHARED_PREF_PROXIMITY_INDEX_KEY) != index;
				if (hasChanged) {
					commitPref(Constants.SHARED_PREF_PROXIMITY_INDEX_KEY, index);
					fetchEvents(Utils.getPrefValue(EventsActivity.this,
							Constants.SHARED_PREF_KEYWORD_KEY));
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		spinnerCategories
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> arg0, View v,
							int index, long id) {
						boolean hasChanged = Utils.getPrefIntValue(
								EventsActivity.this,
								Constants.SHARED_PREF_CATEGORIES_INDEX_KEY) != index;
						if (hasChanged) {
							commitPref(
									Constants.SHARED_PREF_CATEGORIES_INDEX_KEY,
									index);
							fetchEvents(Utils.getPrefValue(EventsActivity.this,
									Constants.SHARED_PREF_KEYWORD_KEY));

						}
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
	}

	private void initDetailsLayout() {
		LinearLayout l = (LinearLayout) findViewById(R.id.linearLayoutEventDetails);
		snapList = new SnapHorzListView(this);
		snapList.setListListener(this);
		if (BeesServiceHelper.getInstance().getEvents() != null) {
			snapList.setFeatureItems(BeesServiceHelper.getInstance()
					.getEvents());
		}

		l.removeAllViews();
		l.addView(snapList, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

	}

	@Override
	protected void onResume() {
		updateSubTitle();
		super.onResume();
	}

	private void updateSubTitle() {
		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREF, 0);
		txtSubTitle.setText(Utils.getSearchTitle(this, settings.getString(
				Constants.SHARED_PREF_KEYWORD_KEY, Constants.KEYWORD),
				BeesServiceHelper.getInstance().getCurrentPage(),
				BeesServiceHelper.getInstance().getTotalPages()));

	}

	private void shareEvent(EventItem event) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		String subject, link, bodyPrefix;
		StringBuffer msg = new StringBuffer();
		bodyPrefix = getString(R.string.share_body_prefix);
		subject = Utils.isEmpty(event.getTitle()) ? getString(R.string.share_subject)
				: event.getTitle();
		link = event.getUrl();

		msg.append(bodyPrefix).append("\n");
		msg.append(link);

		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.toString());

		startActivity(Intent.createChooser(shareIntent,
				getString(R.string.share_subject)));
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
						ArrayList<EventItem> events = results.getEvents();

						if (events != null && !events.isEmpty()) {
							BeesServiceHelper.getInstance().setCurrentPage(
									results.getPageNumber());
							BeesServiceHelper.getInstance().setTotalPages(
									results.getPageCount());
							BeesServiceHelper.getInstance().setEvents(events);
							BeesServiceHelper.getInstance().setHasNewContent(
									true);

							handler.post(new Runnable() {

								public void run() {
									initDisplay(0);
								}
							});
						} else {
							handler.post(new Runnable() {
								public void run() {
									Utils.showToastLong(EventsActivity.this,
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
								Utils.showToastLong(EventsActivity.this,
										getString(R.string.event_parsing_error));
							}
						});
					}
				} else {
					removeDialog(Constants.DIALOG_PROGRESS);
					handler.post(new Runnable() {
						public void run() {
							Utils.showToastLong(EventsActivity.this,
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
									eventListAdapter.notifyDataSetChanged();
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
		}
	}

	@Override
	public void onServiceBinded() {

	}

	@Override
	public void setServiceHelper() {
		super.serviceHelper = BeesServiceHelper.getInstance();
	}

	public void onEventSelected(int index) {
	}

	public void loadView(LinearLayout internalWrapper) {
		int width = Utils.getDisplayWidth(this);
		int itemWidth = (Utils.isPortrait(this) ? width
				: ((int) (width * 0.65)));
		ArrayList<EventItem> events = BeesServiceHelper.getInstance()
				.getEvents();

		LayoutParams p = new LayoutParams(itemWidth, LayoutParams.FILL_PARENT);
		StringBuffer strFormat = new StringBuffer();
		strFormat.append("%s").append(" %d ");
		strFormat.append("%s").append(" %d");

		for (int i = 0; i < events.size(); i++) {
			RelativeLayout featureLayout = (RelativeLayout) View.inflate(this,
					R.layout.eventweb, null);
			EventItem event = events.get(i);

			if (!Utils.isPortrait(this)) {
				LinearLayout layoutHoneyCombYellow = (LinearLayout) featureLayout
						.findViewById(R.id.linearLayoutHoneyComb);

				layoutHoneyCombYellow
						.setBackgroundResource(R.drawable.left_tile_repeat);
			}

			WebView w = (WebView) featureLayout.findViewById(R.id.webViewEvent);
			final ProgressBar progress = (ProgressBar) featureLayout
					.findViewById(R.id.progressBarLoading);

			w.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					if (newProgress >= 100) {
						progress.setVisibility(ProgressBar.INVISIBLE);
					}
				}
			});

			w.setWebViewClient(new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					progress.setVisibility(ProgressBar.INVISIBLE);
				}
			});

			w.loadUrl(event.getUrl());

			TextView txtEventFooter = (TextView) featureLayout
					.findViewById(R.id.textViewEventFooter);

			String txt = String.format(strFormat.toString(), getResources()
					.getText(R.string.event), (i + 1),
					getResources().getText(R.string.of), events.size());
			txtEventFooter.setText(txt);

			txtEventFooter
					.setBackgroundResource(R.drawable.greycomb_tile_repeat);

			internalWrapper.addView(featureLayout, p);
		}
	}

	@Override
	public void onBackPressed() {
		if (snapList.getActiveIndex() == 0) {
			finish();
		} else {
			snapList.setActiveIndex(snapList.getActiveIndex() - 1);
		}
	}

	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		snapList.setActiveIndex(position - 1);
	}

	public void onLastItemReached() {
		Utils.showToastShort(this, getString(R.string.no_more_events));
	}

	public void onFirstItemReached() {
		Utils.showToastShort(this, getString(R.string.first_event_reached));
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
					.append(Utils.getPrefValue(this,
							Constants.SHARED_PREF_KEYWORD_KEY))
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
												EventsActivity.this,
												getString(R.string.enter_event_keyword));
									} else {
										EventsActivity.this
												.fetchEvents(txtSearch
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
		}
		}
		return dialog;
	}

	private void insertCalendarEntry() {
		if (BeesServiceHelper.getInstance().getEvents() != null
				&& !BeesServiceHelper.getInstance().getEvents().isEmpty()) {
			int index = snapList.getActiveIndex();
			EventItem event = BeesServiceHelper.getInstance().getEvents()
					.get(index);

			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			if (!Utils.isEmpty(event.getStartTime())) {
				intent.putExtra("beginTime",
						Utils.getDateToMilliSeconds(event.getStartTime()));
			}
			intent.putExtra("allDay", event.isAllDay());

			if (!Utils.isEmpty(event.getStopTime())) {
				intent.putExtra("endTime",
						Utils.getDateToMilliSeconds(event.getStopTime()));
			}
			intent.putExtra("title", event.getTitle());
			intent.putExtra("description", event.getUrl());
			if (event.hasLocation()) {
				intent.putExtra("eventLocation", event.getLocation());
			}

			startActivity(intent);
		} else {
			Utils.showToastLong(this, getString(R.string.empty_list_option));
		}
	}

	private class EventListAdapter extends BaseAdapter {

		/** List of events to preview */
		private ArrayList<EventItem> events;

		private Context context;

		public EventListAdapter(Context context, ArrayList<EventItem> events) {
			this.context = context;
			this.events = events;
		}

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
			EventViewHolder holder;
			EventItem event = (EventItem) getItem(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.listeventpreview, null);

				holder = new EventViewHolder();
				holder.txtEventTitle = (TextView) convertView
						.findViewById(R.id.textViewTitle);
				holder.txtEventVenueName = (TextView) convertView
						.findViewById(R.id.textViewVenue);
				holder.imgEvent = (ImageView) convertView
						.findViewById(R.id.imageViewEvent);
				holder.txtEventDate = (TextView) convertView
						.findViewById(R.id.textViewDate);
				holder.txtEventDistance = (TextView) convertView
						.findViewById(R.id.textViewDistance);

				convertView.setTag(holder);
			} else {
				holder = (EventViewHolder) convertView.getTag();
			}

			if (event.getImage() != null) {
				Bitmap bitmap = event.getImage().getBitmapImageBlock();
				if (bitmap != null) {
					holder.imgEvent.setImageBitmap(bitmap);
				} else {
					holder.imgEvent.setImageResource(R.drawable.bee_loading);
				}
			} else {
				holder.imgEvent.setImageResource(R.drawable.bee_loading);
			}

			holder.txtEventDate.setText(com.bees.utils.Utils
					.getFormattedDate(event));

			holder.txtEventTitle.setText(event.getTitle());
			holder.txtEventVenueName.setText(com.bees.utils.Utils
					.getPreviewEventAddress(event));

			if (event.hasLocation()) {
				String mapCenter = Utils.getPrefValue(EventsActivity.this,
						Constants.SHARED_PREF_MAP_CENTER);
				GeoPoint c = Utils.getLatLongStrToGeoPoint(mapCenter);
				holder.txtEventDistance.setText(Utils.getEventDistance(event,
						EventSearchRequest.MI_UNITS_TAG, c));
			}

			return convertView;
		}
	}

	static class EventViewHolder {
		TextView txtEventTitle;
		TextView txtEventVenueName;
		ImageView imgEvent;
		TextView txtEventDate;
		TextView txtEventDistance;
	}
}
