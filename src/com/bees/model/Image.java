/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.model;

import java.io.ByteArrayInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * ImageType.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public class Image implements ISerializable {

	/** Image url */
	String url, blockUrl;

	/** Image width */
	int width;

	/** Image height */
	int height;

	/** Image binary data */
	byte[] imgDataBlock;

	/** Image bitmap representation */
	Bitmap bitmapBlock;

	public void deserialise(JSONObject source) throws JSONException {
		url = source.getString("url");
		width = source.getInt("width");
		height = source.getInt("height");
	}

	public String getUrl() {
		return this.url;
	}

	public String getBlockUrl() {
		if (this.blockUrl == null) {
			if (url != null) {
				int smallIdx = url.indexOf("small", 0);
				if (smallIdx != -1) {
					blockUrl = url.replace("small", "block");
				}
			}
		}
		return this.blockUrl;
	}

	public int geWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setImgDataBlock(byte[] imgDataBlock) {
		this.imgDataBlock = imgDataBlock;
	}

	public Bitmap getBitmapImageBlock() {
		if (imgDataBlock != null) {
			Bitmap bitmap = null;
			ByteArrayInputStream bytes = new ByteArrayInputStream(imgDataBlock);
			BitmapDrawable bmd = new BitmapDrawable(bytes);
			bitmap = bmd.getBitmap();
			return bitmap;
		} else
			return null;
	}
}