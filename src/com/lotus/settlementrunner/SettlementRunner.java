package com.lotus.settlementrunner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lotus.event.Event;
import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
import com.lotus.users.User;

public class SettlementRunner {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		UserDao userDAO = UserOJDBCDAO.getInstance();
		User user = userDAO.getUserByName("admin");
		user.getType();
		System.out.println(user.getType().toString());
		EventDao eventDao = EventOJDBCDAO.getInstance();
		Event event = eventDao.getEventByCode("E1234");
//		System.out.println(event);
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = df.parse(df.format(event.getEventStartDate()));
		System.out.println(event);
		Timestamp timestamp = new Timestamp(event.getEventStartDate().getTime());
		System.out.println(date.toString());
	}

}
