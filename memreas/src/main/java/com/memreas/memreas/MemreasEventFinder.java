package com.memreas.memreas;

import java.util.HashMap;

import com.memreas.memreas.MemreasEventBean.EventType;


public class MemreasEventFinder {

	private static MemreasEventFinder instance;
	private HashMap<String, EventShortDetails> memreasEventFinder;

	public class EventShortDetails {
		public String event_id;
		public String user_id;
		public EventType type;
		public int position;
		public int sub_position;
	}

	protected MemreasEventFinder() {
		memreasEventFinder = new HashMap<String, EventShortDetails>();
	}

	public static MemreasEventFinder getInstance() {
		// Assumes adatpter is set
		if (instance == null) {
			instance = new MemreasEventFinder();
		}
		return instance;
	}

	public void add(String event_id, String user_id, EventType type,
			int position, int sub_position) {

		EventShortDetails eventShortDetails = new EventShortDetails();
		eventShortDetails.event_id = event_id;
		eventShortDetails.user_id = user_id;
		eventShortDetails.type = type;
		eventShortDetails.position = position;
		eventShortDetails.sub_position = sub_position;

		memreasEventFinder.put(event_id, eventShortDetails);
	}

	public EventShortDetails find(String event_id) {
		if (memreasEventFinder.containsKey(event_id)) {
			EventShortDetails eventShortDetails = memreasEventFinder.get(event_id);
			return eventShortDetails;
		} 
		return null;
	}
	
	public static void reset() {
		instance = null;
	}
}
