package com.lotus.eventdao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lotus.event.Event;
import com.lotus.event.SportsCategory;
import com.lotus.users.BetStatus;
import com.lotus.users.UserType;

public class EventOJDBCDAO implements EventDao {

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
			throw new RuntimeException(
					"Unable to establish database connection");
		}
	}

	private static Connection getConnection() throws SQLException {
		Connection connection = DriverManager.getConnection(
				"jdbc:oracle:thin:@localhost:1521:xe", "BETUS_APPLICATION",
				"password");
		connection.setAutoCommit(false);

		return connection;
	}

	@Override
	public List<Event> listEvents() {
		Connection connection = null;
		Statement statement = null;
		List<Event> events = new ArrayList<Event>();

		try {
			connection = getConnection();
			statement = connection.createStatement();
			String sql = "SELECT * FROM events";
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				events.add(extractEventFromResult(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error.");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close connection.");
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close statement");
				}
			}
		}

		return events;
	}

	@Override
	public Event getEventByCode(String eventCode) {
		if (eventCode == null || eventCode.isEmpty()) {
			throw new IllegalArgumentException("Event code cannot be empty.");
		}
		Event event = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("SELECT id, eventCode, sportsCategory, eventStartDate, betStatus FROM events WHERE eventCode = ?");
			statement.setString(1, eventCode);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				event = extractEventFromResult(rs);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException("Database error.");
		} finally {

			if (statement != null) {
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
		return event;
	}

	private static Event extractEventFromResult(ResultSet rs)
			throws SQLException {
		long id = rs.getLong("id");
		String eventCode = rs.getString("eventCode");
		String sportsCode = rs.getString("sportsCategory");
		Date startDate = rs.getTimestamp("eventStartDate");
		String betStatus = rs.getString("betStatus");

		Event event = new Event(id, eventCode,
				SportsCategory.valueOf(sportsCode), startDate,
				BetStatus.valueOf(betStatus));

		return event;
	}

	@Override
	public void createEvent(Event newEvent) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("INSERT INTO EVENTS(id,eventCode,sportsCategory,eventStartDate,betStatus) VALUES(events_seq.nextval, ?, ? ,?, ?)");
			statement.setString(1, newEvent.getEventCode());
			statement.setString(2, newEvent.getSportsCode().toString());
			statement.setTimestamp(3, new Timestamp(newEvent
					.getEventStartDate().getTime()));
			statement.setString(4, BetStatus.OPEN.toString());
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException("Database Error");
			}
			e.printStackTrace();
		} finally {

			if (statement != null) {
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
			throw new IllegalArgumentException(
					"Attempt to update a non-existing or non persisted event");
		}

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("UPDATE events set sportsCategory = ?, eventStartDate = ?, betStatus = ? where id = ?");
			statement.setString(1, existingEvent.getSportsCode().toString());
			statement.setTimestamp(2, new Timestamp(existingEvent
					.getEventStartDate().getTime()));
			statement.setString(3, existingEvent.getBetStatus().toString());
			statement.setLong(4, existingEvent.getId());

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

			if (statement != null) {
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
	public Event getEventById(Long id) {
		if (id == null || id==0) {
			throw new IllegalArgumentException("Event id cannot be empty.");
		}
		Event event = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("SELECT id, eventCode, sportsCategory, eventStartDate, betStatus FROM events WHERE id = ?");
			statement.setLong(1, id);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				event = extractEventFromResult(rs);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException("Database error.");
		} finally {

			if (statement != null) {
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
		return event;
	}

}
