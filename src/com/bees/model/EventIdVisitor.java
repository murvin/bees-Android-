/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */
package com.bees.model;

/**
 * EventIdVisitor.java
 * 
 * @author MURVIN BHANTOOA
 * @date 09/05/2012
 */
public class EventIdVisitor implements IEventVisitor {

	private String eventId;
	private int index;
	private boolean hasFound;

	public EventIdVisitor(String eventId) {
		this.eventId = eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
		hasFound = false;
		index = 0;
	}

	public void visit(EventItem item) {
		if (item.getId().equals(this.eventId)) {
			hasFound = true;
		}

		if (!hasFound) {
			index++;
		}
	}

	public int getEventIndex() {
		return this.index;
	}
}
