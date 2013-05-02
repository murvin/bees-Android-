/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * AppServiceProviderMetaData.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class AppServiceProviderMetaData {

	/** AppService provider authority */
	public static final String AUTHORITY = "com.bees.db.AppServiceProvider";

	/** The name of the database on the file system */
	public static final String DATABASE_NAME = "service.db";

	/** The version of the database that this class understand */
	public static final int DATABASE_VERSION = 1;

	public static final class Transaction implements BaseColumns {

		public static final String TABLE_NAME = "trans";

		// URI AND MIME TYPE DEFINITIONS
		public static final String PATH = "trans";

		public static final Uri CONTENT_URI = Uri.parse(new StringBuffer(
				"content://").append(AUTHORITY).append("/").append(PATH)
				.toString());

		public static final String CONTENT_TYPE = "vnd.appservice.cursor.dir/vnd.appservice.trans";

		public static final String CONTENT_ITEM_TYPE = "vnd.appservice.cursor.item/vnd.appservice.trans";

		public static final String DEFAULT_SORT_ORDER = " DESC";

		// ADDITIONAL COLUMNS //
		public static final String COMMAND = "command";

		public static final String URL = "uri";

		public static final String STATUS = "status";

		public static final String RESPONSE = "response";
	}
}
