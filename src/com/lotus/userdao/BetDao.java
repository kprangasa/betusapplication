package com.lotus.userdao;

import java.util.List;

import com.lotus.users.Bet;

public interface BetDao {
	void create(Bet bet);
	List<Bet> listAllBets();
	List<Bet> listBetsOfCustomer(Long id);
	List<Bet> getBetsByEvent(Long eventId);
	Bet getBetById(Long customerId, Long eventId);
	void settleBetById(Long id);
	
}
