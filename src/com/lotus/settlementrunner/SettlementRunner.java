package com.lotus.settlementrunner;

import com.lotus.dao.UserDao;
import com.lotus.dao.UserOJDBCDAO;
import com.lotus.users.User;
import com.lotus.users.UserType;

public class SettlementRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UserDao userDAO = UserOJDBCDAO.getInstance();
		User user = userDAO.getUserByName("admin");
		user.getType();
		System.out.println(user.getType().toString());
		System.out.println(userDAO.getUserByName("admin"));

	}

}
