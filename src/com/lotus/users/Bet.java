package com.lotus.users;

import java.math.BigDecimal;

public class Bet {
	private Long id;
	private Long eventId;
	private Long customerId;
	private BigDecimal amount;
	private boolean isSettled;
	
	
	
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public boolean isSettled() {
		return isSettled;
	}
	public void setSettled(boolean isSettled) {
		this.isSettled = isSettled;
	}

	
}
