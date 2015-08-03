package com.lotus.event;

import java.util.Date;

import com.lotus.users.BetStatus;

public class Event {
	private Long id;
	private String eventCode;
	private SportsCategory sportsCategoryCode;
	private Date eventStartDate;
	private BetStatus betStatus;
	
	
	
	
	
	
	
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
	
	
}
