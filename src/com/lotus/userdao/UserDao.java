package com.lotus.userdao;

import java.math.BigDecimal;
import java.util.List;

import com.lotus.users.User;



public interface UserDao {
	void createUser(User newUser);
	List<User> listUsers();
	void addBalance(User user, BigDecimal balance);
	User getUserByName(String name);
	User getUserById(Long userId);
	
	
}
