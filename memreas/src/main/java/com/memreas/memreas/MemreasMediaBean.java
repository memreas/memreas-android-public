package com.memreas.memreas;


public abstract class MemreasMediaBean {
	
	private String eventId;
	private boolean addedToQueue=false;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public boolean isAddedToQueue() {
		return addedToQueue;
	}

	public void setAddedToQueue(boolean addedToQueue) {
		this.addedToQueue = addedToQueue;
	}

}
