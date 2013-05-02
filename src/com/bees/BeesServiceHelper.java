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

import com.bees.model.EventItem;
import com.bees.model.IEventVisitor;
import com.bees.service.ServiceHelper;

/**
 * BeesServiceHelper.java
 * 
 * @author MURVIN BHANTOOA
 * @date 01 May 2012
 */
public class BeesServiceHelper extends ServiceHelper {

	/** Singleton instance of the service helper */
	public static BeesServiceHelper instance;

	/** Holds the current list of events retrieved */
	private ArrayList<EventItem> events;

	/** Last results current page and total pages attributes */
	private int currentPage, totalPages;

	/** Turns true when a screen has updated the events list */
	private boolean hasNewContent;

	private BeesServiceHelper() {
		currentPage = -1;
		totalPages = -1;
		hasNewContent = false;
	}

	public static BeesServiceHelper getInstance() {
		if (instance == null) {
			instance = new BeesServiceHelper();
		}
		return instance;
	}

	public ArrayList<EventItem> getEvents() {
		return this.events;
	}

	public void setEvents(ArrayList<EventItem> events) {
		this.events = events;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalPages() {
		return this.totalPages;
	}
	
	public boolean hasNextContent(){
		return this.totalPages > this.currentPage;
	}

	public void accept(IEventVisitor visitor) {
		if (events != null) {
			for (int i = 0; i < events.size(); i++) {
				visitor.visit(events.get(i));
			}
		}
	}

	public void setHasNewContent(boolean hasNewContent) {
		this.hasNewContent = hasNewContent;
	}

	public boolean hasNewContent() {
		return this.hasNewContent;
	}
}
