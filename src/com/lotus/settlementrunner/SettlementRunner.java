package com.lotus.settlementrunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import com.lotus.event.Event;
import com.lotus.event.Outcome;
import com.lotus.event.Result;
import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.userdao.BetDao;
import com.lotus.userdao.BetOJDBCDAO;
import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
import com.lotus.users.Bet;
import com.lotus.users.BetStatus;

public class SettlementRunner implements Runnable{

	
	
	
//	@Override
//	public void run() {
//		super.run();
//	}

	public static void main(String[] args) throws ParseException {
			Runnable settlementRunner = new SettlementRunner();
			Thread thread1 = new Thread(settlementRunner);
			thread1.start();
			
			
			
			
			
			
			
	}
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(10000);
				settleEvents();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void settleEvents(){
		EventDao eventDAO = EventOJDBCDAO.getInstance();
		BetDao betDAO = BetOJDBCDAO.getInstance();
		OutcomeDao outcomeDAO = OutcomeOJDBCDAO.getInstance();
		UserDao userDAO = UserOJDBCDAO.getInstance();
		if(eventDAO.getResultedEvents()== null || eventDAO.getResultedEvents().isEmpty()){
			return;
		}
		for(Event event: eventDAO.getResultedEvents()){
			for(Bet bet: betDAO.getBetsByEvent(event.getId())){
				Outcome betOutcome = outcomeDAO.getOutcomeById(bet.getOutcomeId());
				if(betOutcome.getResult().equals(Result.WIN)){
					userDAO.addBalance(userDAO.getUserById(bet.getCustomerId()), bet.getAmount().multiply(new BigDecimal("2")));
				}
				else if(betOutcome.getResult().equals(Result.DRAW)){
					userDAO.addBalance(userDAO.getUserById(bet.getCustomerId()), bet.getAmount());
				}
				betDAO.settleBetById(bet.getId());
			}
			event.setBetStatus(BetStatus.SETTLED);
			eventDAO.updateEvent(event);
		}
	}

}
