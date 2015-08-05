package com.lotus.eventdao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.lotus.event.Event;
import com.lotus.event.SportsCategory;
import com.lotus.users.BetStatus;
import com.lotus.users.User;
import com.lotus.users.UserType;

public class EventOJDBCDAO implements EventDao{

	private static EventOJDBCDAO instance = null;

	public static EventOJDBCDAO getInstance() {
		if (instance == null) {
			instance = new EventOJDBCDAO();
		}
		return instance;
	}
	
	private EventOJDBCDAO() {
		 
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to establish database connection");
		}
	}
	private static Connection getConnection() throws SQLException{
		Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "BETUS_APPLICATION","password");
		connection.setAutoCommit(false);
	
	return connection;
}
	@Override
	public List<Event> listEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Event getEventByCode(String eventCode) {
		if(eventCode == null || eventCode.isEmpty()){
			throw new IllegalArgumentException("Event code cannot be empty.");
		}
		Event event = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = getConnection();
			statement = connection.prepareStatement("SELECT id, eventCode, sportsCode, startDate, betStatus FROM events WHERE eventCode = ?");
			statement.setString(1, eventCode);
			ResultSet rs = statement.executeQuery();
		
			if(rs.next()){
				event = extractEventFromResult(rs);
				
			}
			
		}catch(SQLException e){
			
			throw new IllegalStateException("Database error.");
		}
		finally {

			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new RuntimeException("Unable to close statement");
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException("Unable to close connection");
				}
			}
		}
		return  event;
	}
	private static Event extractEventFromResult(ResultSet rs) throws SQLException {
		long id = rs.getLong("id");
		String eventCode = rs.getString("eventCode");
		String sportsCode = rs.getString("sportsCode");
		Date startDate = rs.getDate("startDate");
		String betStatus = rs.getString("type");
		
		Event event = new Event(id, eventCode, SportsCategory.valueOf(sportsCode), startDate, BetStatus.valueOf(betStatus));
		
		return event;
	}

	@Override
	public void createEvent(Event newEvent) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO EVENTS(id,eventCode,sportsCode,startDate,betStatus) VALUES(events_seq.nextval, ?, ?, ? ,TO_DATE(?,'MM/dd/yyyy HH24:mi:SS'))");
			statement.setString(1, newEvent.getEventCode());
			statement.setString(2, newEvent.getSportsCategoryCode().toString());
			Date startDate = new Date(newEvent.getEventStartDate().getTime());
			statement.setDate(3, startDate);
			statement.setString(4, BetStatus.OPEN.toString());

			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException("Database Error");
			}
			throw new RuntimeException("Error: event already exists.");
		} finally {

			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new RuntimeException("Unable to close statement");
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException("Unable to close connection");
				}
			}
			
		}
		
		
	}

	@Override
	public void updateEvent(Event existingEvent) {
		if (existingEvent == null || existingEvent.getId() == 0) {
			throw new IllegalArgumentException("Attempt to update a non-existing or non persisted event");
		}
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(
					"UPDATE events set sportsCode = ?, startDate = ? where id = ?");
			Date eventDate = new Date(existingEvent.getEventStartDate().getTime());

			statement.setString(1, existingEvent.getSportsCategoryCode().toString());
			statement.setDate(2, eventDate);
			statement.setLong(3, existingEvent.getId());

			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();

			throw new RuntimeException("Data error: " + e.getMessage());
		} finally {

			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new RuntimeException("Unable to close statement");
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException("Unable to close connection");
				}
			}
			
		}
		
	}

}
