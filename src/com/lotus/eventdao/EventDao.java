package com.lotus.eventdao;

import java.util.List;

import com.lotus.event.Event;

public interface EventDao {
	List<Event> listEvents();
	Event getEventByCode(String eventCode);
	void createEvent(Event newEvent);
	void updateEvent(Event existingEvent);
	Event getEventById(Long id);
	List<Event> getResultedEvents();
	
	
}
