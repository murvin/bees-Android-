/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.db;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import android.net.Uri;
import android.text.TextUtils;

/**
 * AppServiceProvider.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class AppServiceProvider extends ContentProvider {

	/** The sql database */
	private AppServiceDatabase db;

	// FOR IDENTIFYING ALL INCOMING URI PATTERNS
	private static final UriMatcher uriMatcher;

	private static final int INCOMING_TRANSACTIONS_COLLECTION_URI_INDICATOR = 0x001;

	private static final int INCOMING_SINGLE_TRANSACTION_URI_INDICATOR = 0x002;

	// COLUMN NAMES SUPPORTED BY PROVIDER //
	private static HashMap<String, String> transactionProjectionMap;

	static {
		transactionProjectionMap = new HashMap<String, String>();
		transactionProjectionMap.put(
				AppServiceProviderMetaData.Transaction._ID,
				AppServiceProviderMetaData.Transaction._ID);
		transactionProjectionMap.put(
				AppServiceProviderMetaData.Transaction.COMMAND,
				AppServiceProviderMetaData.Transaction.COMMAND);
		transactionProjectionMap.put(
				AppServiceProviderMetaData.Transaction.URL,
				AppServiceProviderMetaData.Transaction.URL);
		transactionProjectionMap.put(
				AppServiceProviderMetaData.Transaction.STATUS,
				AppServiceProviderMetaData.Transaction.STATUS);
		transactionProjectionMap.put(
				AppServiceProviderMetaData.Transaction.RESPONSE,
				AppServiceProviderMetaData.Transaction.RESPONSE);
	}

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AppServiceProviderMetaData.AUTHORITY,
				AppServiceProviderMetaData.Transaction.PATH,
				INCOMING_TRANSACTIONS_COLLECTION_URI_INDICATOR);
		uriMatcher.addURI(AppServiceProviderMetaData.AUTHORITY,
				AppServiceProviderMetaData.Transaction.PATH + "/#",
				INCOMING_SINGLE_TRANSACTION_URI_INDICATOR);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = db.getWritableDatabase();
		int count;

		switch (uriMatcher.match(uri)) {
		case INCOMING_TRANSACTIONS_COLLECTION_URI_INDICATOR:
			count = database.delete(
					AppServiceProviderMetaData.Transaction.TABLE_NAME,
					selection, selectionArgs);
			break;
		case INCOMING_SINGLE_TRANSACTION_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = database.delete(
					AppServiceProviderMetaData.Transaction.TABLE_NAME,
					AppServiceProviderMetaData.Transaction._ID
							+ "="
							+ rowId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new SQLException("UNKOWN 	URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case INCOMING_TRANSACTIONS_COLLECTION_URI_INDICATOR:
			return AppServiceProviderMetaData.Transaction.CONTENT_TYPE;
		case INCOMING_SINGLE_TRANSACTION_URI_INDICATOR:
			return AppServiceProviderMetaData.Transaction.CONTENT_ITEM_TYPE;
		default:
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		ContentValues newValues = (values == null ? new ContentValues()
				: new ContentValues(values));

		switch (uriMatcher.match(uri)) {
		case INCOMING_TRANSACTIONS_COLLECTION_URI_INDICATOR:
			// ENSURING ALL FIELDS ARE SET //

			if (newValues
					.containsKey(AppServiceProviderMetaData.Transaction.COMMAND) == false) {
				newValues.put(AppServiceProviderMetaData.Transaction.COMMAND,
						"");
			}

			if (newValues
					.containsKey(AppServiceProviderMetaData.Transaction.URL) == false) {
				newValues.put(AppServiceProviderMetaData.Transaction.URL, "");
			}

			if (newValues
					.containsKey(AppServiceProviderMetaData.Transaction.STATUS) == false) {
				newValues
						.put(AppServiceProviderMetaData.Transaction.STATUS, "");
			}

			if (newValues
					.containsKey(AppServiceProviderMetaData.Transaction.RESPONSE) == false) {
				newValues.put(AppServiceProviderMetaData.Transaction.RESPONSE,
						"");
			}

			SQLiteDatabase database = db.getWritableDatabase();
			
			long rowId = database.insert(
					AppServiceProviderMetaData.Transaction.TABLE_NAME, null,
					newValues);

			if (rowId > 0) {
				Uri insertedEventUri = ContentUris.withAppendedId(
						AppServiceProviderMetaData.Transaction.CONTENT_URI,
						rowId);
				getContext().getContentResolver().notifyChange(
						insertedEventUri, null);
				return insertedEventUri;
			}
			
			getContext().getContentResolver().notifyChange(uri, null);
			break;
		}

		return null;
	}

	@Override
	public boolean onCreate() {
		db = new AppServiceDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {
		case INCOMING_TRANSACTIONS_COLLECTION_URI_INDICATOR:
			qb.setTables(AppServiceProviderMetaData.Transaction.TABLE_NAME);
			qb.setProjectionMap(transactionProjectionMap);
			break;
		case INCOMING_SINGLE_TRANSACTION_URI_INDICATOR:
			qb.setTables(AppServiceProviderMetaData.Transaction.TABLE_NAME);
			qb.setProjectionMap(transactionProjectionMap);
			qb.appendWhere(AppServiceProviderMetaData.Transaction._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}

		// Get the database and run the query
		SQLiteDatabase database = db.getReadableDatabase();

		Cursor c = qb.query(database, projection, selection, selectionArgs,
				null, null, sortOrder);

		// Tell the cursor what uri to watch
		// so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase database = db.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case INCOMING_TRANSACTIONS_COLLECTION_URI_INDICATOR:
			count = database.update(
					AppServiceProviderMetaData.Transaction.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case INCOMING_SINGLE_TRANSACTION_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = database.update(
					AppServiceProviderMetaData.Transaction.TABLE_NAME, values,
					AppServiceProviderMetaData.Transaction._ID
							+ "="
							+ rowId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}
}
