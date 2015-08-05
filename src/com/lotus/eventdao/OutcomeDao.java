package com.lotus.eventdao;

import java.util.List;

import com.lotus.event.Outcome;

public interface OutcomeDao {
	List<Outcome> listOutcomes();
	void createOutcome(Outcome outcome);
	List<Outcome> getListOfOutcome(String eventCode);
	List<Outcome> getListOfOutcomeById(Long id);
	void setOutcomeResult(Outcome outcome, String result);
	Outcome getOutcomeByDescription(String description,Long id);
	Outcome getOutcomeById(Long id);
	
}
