package com.lotus.event;

import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.users.BetStatus;

public class Outcome {
	private Long id;
	private String description;
	private Long eventId;
	private Result result;

	public Outcome(String description, Long eventId, Result result) {
		super();
		this.description = description;
		this.eventId = eventId;
		this.result = result;
	}

	public Outcome(Long id, String description, Long eventId, Result result) {
		this(description, eventId, result);
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public boolean persist() {
		OutcomeDao outcomeDAO = OutcomeOJDBCDAO.getInstance();
		Outcome outcome = outcomeDAO.getOutcomeByDescription(description, eventId);
		if (outcome == null) {
			outcomeDAO.createOutcome(this);
			System.out.println("Outcome created.");
			return true;
		}

		return false;

	}
}
