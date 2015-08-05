package com.lotus.event;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.users.BetStatus;

public class Event {
	private Long id;
	private String eventCode;
	private SportsCategory sportsCode;
	private Date eventStartDate;
	private BetStatus betStatus;

	public Event(String eventCode, SportsCategory sportsCode,
			Date eventStartDate) {
		super();
		this.eventCode = eventCode;
		this.sportsCode = sportsCode;
		this.eventStartDate = eventStartDate;
	}
	
	
	

	public Event(Long id, String eventCode, SportsCategory sportsCode,
			Date eventStartDate, BetStatus betStatus) {
		this(eventCode, sportsCode, eventStartDate);
		this.id = id;
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
	public SportsCategory getSportsCode() {
		return sportsCode;
	}
	public void setSportsCategoryCode(SportsCategory sportsCode) {
		this.sportsCode = sportsCode;
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
	
	
	
	
	
	
	@Override
	public String toString() {
		

		return "Event [id=" + id + ", eventCode=" + eventCode
				+ ", sportsCategoryCode=" + sportsCode
				+ ", eventStartDate=" +eventStartDate + ", betStatus="
				+ betStatus + "]";
	}

	public void persist() {
		EventDao eventDao = EventOJDBCDAO.getInstance();
		Event event  = eventDao.getEventByCode(eventCode);
		if(event == null) {
			eventDao.createEvent(this);
			System.out.println("Event created.");
		} else {
			this.setId(event.getId());
			
			boolean hasChanged = !event.getSportsCode().equals(this.getSportsCode()) || event.getEventStartDate().compareTo(this.getEventStartDate())!=0;
			if(hasChanged&&this.getBetStatus() == BetStatus.OPEN) {
				System.out.println("Event to update :" + this);
				eventDao.updateEvent(this);
				System.out.println("Event updated :"+this);
			} else {
				System.out.println("Ignored persistence, nothing changed for event "+this);
			}
		}
	}
	
}
