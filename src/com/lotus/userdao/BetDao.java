package com.lotus.userdao;

import java.util.List;

import com.lotus.users.Bet;

public interface BetDao {
	void create(Bet bet);
	List<Bet> listAllBets();
	List<Bet> listBetsOfCustomer(Long id);
	Bet getBetById(Long customerId, Long eventId);
	
}
