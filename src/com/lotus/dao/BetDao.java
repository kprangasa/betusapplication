package com.lotus.dao;

import java.util.List;

import com.lotus.users.Bet;

public interface BetDao {
	void create(Bet bet);
	List<Bet> list();
	
}
