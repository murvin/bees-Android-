/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.db;

import android.content.Context;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * AppServiceDatabase.java
 * 
 * Provides access to the service transaction database. Since this is not a
 * ContentProvider no other applications will have access to the database.
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class AppServiceDatabase extends SQLiteOpenHelper {

	public AppServiceDatabase(Context context) {
		super(context, AppServiceProviderMetaData.DATABASE_NAME, null,
				AppServiceProviderMetaData.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer("CREATE TABLE ");
		sql.append(AppServiceProviderMetaData.Transaction.TABLE_NAME).append(
				" (");
		sql.append(AppServiceProviderMetaData.Transaction._ID).append(
				" INTEGER PRIMARY KEY,");
		sql.append(AppServiceProviderMetaData.Transaction.COMMAND).append(
				" INTEGER,");
		sql.append(AppServiceProviderMetaData.Transaction.URL).append(" TEXT,");
		sql.append(AppServiceProviderMetaData.Transaction.STATUS).append(
				" INTEGER,");
		sql.append(AppServiceProviderMetaData.Transaction.RESPONSE)
				.append(" BINARY").append(");");

		db.beginTransaction();
		try {
			db.execSQL(sql.toString());
			db.setTransactionSuccessful();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL("DROP TABLE IF EXISTS "
					+ AppServiceProviderMetaData.DATABASE_NAME);
			onCreate(db);
			db.setTransactionSuccessful();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
}
