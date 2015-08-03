package com.lotus.dao;

import java.util.List;

import com.lotus.event.Event;

public interface EventDao {
	List<Event> listEvents();
	Event getEventByCode(String eventCode);
	void createEvent(Event newEvent);
	
}
