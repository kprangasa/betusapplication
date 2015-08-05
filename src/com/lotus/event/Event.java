package com.lotus.event;

import java.util.Date;

import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.users.BetStatus;

public class Event {
	private Long id;
	private String eventCode;
	private SportsCategory sportsCategoryCode;
	private Date eventStartDate;
	private BetStatus betStatus;
	
	
	public Event(String eventCode, SportsCategory sportsCategoryCode,
			Date eventStartDate) {
		super();
		this.eventCode = eventCode;
		this.sportsCategoryCode = sportsCategoryCode;
		this.eventStartDate = eventStartDate;
	}
	public Event(){
		
	}
	
	

	public Event(Long id, String eventCode, SportsCategory sportsCategoryCode,
			Date eventStartDate, BetStatus betStatus) {
		super();
		this.id = id;
		this.eventCode = eventCode;
		this.sportsCategoryCode = sportsCategoryCode;
		this.eventStartDate = eventStartDate;
		this.betStatus = betStatus;
	}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	public SportsCategory getSportsCategoryCode() {
		return sportsCategoryCode;
	}
	public void setSportsCategoryCode(SportsCategory sportsCategoryCode) {
		this.sportsCategoryCode = sportsCategoryCode;
	}
	public Date getEventStartDate() {
		return eventStartDate;
	}
	public void setEventStartDate(Date eventStartDate) {
		this.eventStartDate = eventStartDate;
	}
	public BetStatus getBetStatus() {
		return betStatus;
	}
	public void setBetStatus(BetStatus betStatus) {
		this.betStatus = betStatus;
	}
	
	
	
	
	public void persist() {
		EventDao eventDao = EventOJDBCDAO.getInstance();
		Event event = eventDao.getEventByCode(eventCode);
		
		if(event == null) {
			eventDao.createEvent(this);
			System.out.println("Created event "+this);
		} else {
			
			this.setId(event.getId());
			
			boolean hasChanged = !event.getSportsCategoryCode().equals(this.getSportsCategoryCode()) || ! event.getEventStartDate().equals(this.getEventStartDate());
			if(hasChanged) {
				eventDao.updateEvent(this);
				System.out.println("Updated Animal "+this);
			} else {
				System.out.println("Ignored persistence, nothing changed for event "+this);
			}
		}
	}
	
}
