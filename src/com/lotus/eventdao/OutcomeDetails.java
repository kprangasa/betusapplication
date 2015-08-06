package com.lotus.eventdao;

public class OutcomeDetails {
	String description;
	Long id;
	
	public OutcomeDetails(String description, Long id) {
		super();
		this.description = description;
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
