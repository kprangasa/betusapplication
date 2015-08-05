package com.lotus.settlementrunner;

import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
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
		

	}

}
