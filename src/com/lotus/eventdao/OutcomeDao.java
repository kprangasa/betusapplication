package com.lotus.eventdao;

import java.util.List;

import com.lotus.event.Outcome;

public interface OutcomeDao {
	List<Outcome> listOutcomes();
	void createOutcome(Outcome outcome);
	List<Outcome> getListOfOutcome(String eventCode);
	void setOutcomeResult(String result);
	
}
