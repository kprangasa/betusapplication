package com.lotus.eventdao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.lotus.event.Event;
import com.lotus.event.Outcome;
import com.lotus.event.Result;
import com.lotus.users.BetStatus;

public class OutcomeOJDBCDAO implements OutcomeDao{

	
	private static OutcomeOJDBCDAO instance = null;

	public static OutcomeOJDBCDAO getInstance() {
		if (instance == null) {
			instance = new OutcomeOJDBCDAO();
		}
		return instance;
	}
	
	private OutcomeOJDBCDAO() {
		 
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
	public List<Outcome> listOutcomes() {
		Connection connection = null;
		Statement statement = null;	
		List<Outcome> outcomes = new ArrayList<Outcome>();
		
		try {
			connection = getConnection();
			statement = connection.createStatement();
			String sql = "SELECT * FROM outcomes";
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
				outcomes.add(extractOutcomeFromResult(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error.");
		}finally{
			if(connection!=null){
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close connection.");
				}
			}
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close statement");
				}
			}
		}
		
		return outcomes;
	}

	@Override
	public void createOutcome(Outcome outcome) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement("INSERT INTO OUTCOMES(id,description,eventid,result) VALUES(outcomes_seq.nextval, ?, ? ,?)");
			statement.setString(1, outcome.getDescription());
			statement.setLong(2, outcome.getEventId());
			statement.setString(3, outcome.getResult().toString());
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
	public List<Outcome> getListOfOutcome(String eventCode) {
		Connection connection = null;
		PreparedStatement statement = null;	
		List<Outcome> outcomes = new ArrayList<Outcome>();
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("SELECT outcomes.id, outcomes.description, outcomes.EVENTID,outcomes.RESULT FROM outcomes INNER JOIN events on events.id = outcomes.EVENTID where EventCode = ?");
			statement.setString(1, eventCode);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()){
				outcomes.add(extractOutcomeFromResult(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error.");
		}finally{
			if(connection!=null){
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close connection.");
				}
			}
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close statement");
				}
			}
		}
		
		return outcomes;
	}

	@Override
	public void setOutcomeResult(Outcome outcome, String result) {
		if (outcome == null || outcome.getId() == 0) {
			throw new IllegalArgumentException("Resulting a non existing event.");
		}
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("UPDATE outcomes set result = ? where id = ?");
			statement.setLong(2, outcome.getId());
			statement.setString(1, result);

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
	private static Outcome extractOutcomeFromResult(ResultSet rs) throws SQLException {
		Long id = rs.getLong("id");
		String description = rs.getString("description");
		Long eventId = rs.getLong("eventId");
		String result = rs.getString("result");
		Outcome outcome = new Outcome(id, description, eventId, Result.valueOf(result));
		return outcome;
	}

	@Override
	public Outcome getOutcomeByDescription(String description, Long id) {
		if(id == null || id == 0){
			throw new IllegalArgumentException("Id cannot be empty.");
		}
		Outcome outcome = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = getConnection();
			statement = connection.prepareStatement("select * from outcomes where eventid =? and  description = ?");
			statement.setLong(1, id);
			statement.setString(2, description);
			ResultSet rs = statement.executeQuery();
		
			if(rs.next()){
				outcome = extractOutcomeFromResult(rs);
				
			}
			
		}catch(SQLException e){
			e.printStackTrace();
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
		return  outcome;
	}

	@Override
	public Outcome getOutcomeById(Long id) {
		if(id == null || id == 0){
			throw new IllegalArgumentException("Id cannot be empty.");
		}
		Outcome outcome = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = getConnection();
			statement = connection.prepareStatement("select * from outcomes where id = ?");
			statement.setLong(1, id);
			ResultSet rs = statement.executeQuery();
		
			if(rs.next()){
				outcome = extractOutcomeFromResult(rs);
				
			}
			
		}catch(SQLException e){
			e.printStackTrace();
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
		return  outcome;
	}

	@Override
	public List<Outcome> getListOfOutcomeById(Long id) {
		Connection connection = null;
		PreparedStatement statement = null;	
		List<Outcome> outcomes = new ArrayList<Outcome>();
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("SELECT * FROM outcomes WHERE eventId = ?");
			statement.setLong(1, id);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()){
				outcomes.add(extractOutcomeFromResult(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error.");
		}finally{
			if(connection!=null){
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close connection.");
				}
			}
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Unable to close statement");
				}
			}
		}
		
		return outcomes;
	}

}
