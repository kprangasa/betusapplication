package com.lotus.eventdao;

import java.util.Date;

import com.lotus.event.SportsCategory;

public class EventDetails {
	Long id;
	SportsCategory sportsCategory;
	Date startTime;
	
	public EventDetails(Long id, SportsCategory sportsCategory, Date startTime) {
		super();
		this.id = id;
		this.sportsCategory = sportsCategory;
		this.startTime = startTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public SportsCategory getSportsCategory() {
		return sportsCategory;
	}
	public void setSportsCategory(SportsCategory sportsCategory) {
		this.sportsCategory = sportsCategory;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
