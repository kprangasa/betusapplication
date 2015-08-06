package com.lotus.users;

import java.math.BigDecimal;

import com.lotus.event.Outcome;
import com.lotus.userdao.BetDao;
import com.lotus.userdao.BetOJDBCDAO;

public class Bet {
	private Long id;
	private Long eventId;
	private Long customerId;
	private BigDecimal amount;
	private Long outcomeId;
	
	
	public Bet(Long eventId, Long customerId, BigDecimal amount,
			Long outcomeId, boolean isSettled) {
		super();
		this.eventId = eventId;
		this.customerId = customerId;
		this.amount = amount;
		this.outcomeId = outcomeId;
		this.isSettled = isSettled;
	}
	public Bet(Long id, Long eventId, Long customerId, BigDecimal amount,
			Long outcomeId, boolean isSettled) {
		this(eventId, customerId, amount, outcomeId, isSettled);
		this.id = id;
	}
	public Long getOutcomeId() {
		return outcomeId;
	}
	public void setOutcomeId(Long outcomeId) {
		this.outcomeId = outcomeId;
	}
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
	public boolean persist() {
		BetDao betDao = BetOJDBCDAO.getInstance();
		Bet bet = betDao.getBetById(customerId, eventId);
		System.out.println(customerId + eventId + ""+bet);
		if (bet == null) {
			betDao.create(this);
			System.out.println("Bet created.");
			return true;
		}
		System.out.println("Cannot bet on the same event twice.");
		return false;

	}
	@Override
	public String toString() {
		return "Bet [id=" + id + ", eventId=" + eventId + ", customerId="
				+ customerId + ", amount=" + amount + ", outcomeId="
				+ outcomeId + ", isSettled=" + isSettled + "]";
	}

	
}
