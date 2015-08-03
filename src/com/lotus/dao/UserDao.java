package com.lotus.dao;

import java.util.List;

import com.lotus.event.Event;
import com.lotus.users.User;



public interface UserDao {
	void createUser(User newUser);
	List<User> listUsers();
	void addBalance(User user);
	User getUserByName(String name);
	
	
}
