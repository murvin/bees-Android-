/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.widgets;

import android.graphics.Bitmap;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * EventOverlayItem.java
 * 
 * @author MURVIN BHANTOOA
 * @date 09/05/2012
 */
public class EventOverlayItem extends OverlayItem {

	private Bitmap image;

	public EventOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	public EventOverlayItem(GeoPoint point, String title, String snippet,
			Bitmap image) {
		super(point, title, snippet);
		this.image = image;
	}

	public Bitmap getImage() {
		return this.image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}
}
