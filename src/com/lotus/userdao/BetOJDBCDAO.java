package com.lotus.userdao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.lotus.event.Event;
import com.lotus.event.SportsCategory;
import com.lotus.users.Bet;
import com.lotus.users.BetStatus;
import com.lotus.users.User;
import com.lotus.users.UserType;

public class BetOJDBCDAO implements BetDao {

	private static BetOJDBCDAO instance = null;

	public static BetOJDBCDAO getInstance() {
		if (instance == null) {
			instance = new BetOJDBCDAO();
		}
		return instance;
	}

	private BetOJDBCDAO() {

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
	public void create(Bet bet) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("INSERT INTO BETS(id,eventId,customerId,amount,outcomeId, isSettled) VALUES(bets_seq.nextval, ?, ?, ? ,?,?)");
			statement.setLong(1, bet.getEventId());
			statement.setLong(2, bet.getCustomerId());
			statement.setBigDecimal(3, bet.getAmount());
			statement.setLong(4, bet.getOutcomeId());
			statement.setBoolean(5, bet.isSettled());

			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException("Database Error");
			}
			throw new RuntimeException("Error: bet already exists.");
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
	public List<Bet> listAllBets() {
		Connection connection = null;
		Statement statement = null;
		List<Bet> bets = new ArrayList<Bet>();

		try {
			connection = getConnection();
			statement = connection.createStatement();
			String sql = "SELECT * FROM bets";
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				bets.add(extractBetFromResult(resultSet));
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

		return bets;
	}

	private static Bet extractBetFromResult(ResultSet rs) throws SQLException {
		long id = rs.getLong("id");
		long eventId = rs.getLong("eventId");
		long customerId = rs.getLong("customerId");
		BigDecimal amount = rs.getBigDecimal("amount");
		long outcomeId = rs.getLong("outcomeId");
		boolean isSettled = rs.getBoolean("isSettled");

		Bet bet = new Bet(id, eventId, customerId, amount, outcomeId, isSettled);

		return bet;
	}

	@Override
	public List<Bet> listBetsOfCustomer(Long id) {
		Connection connection = null;
		PreparedStatement statement = null;
		List<Bet> bets = new ArrayList<Bet>();

		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("SELECT * FROM bets WHERE id = ?");
			statement.setLong(1, id);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				bets.add(extractBetFromResult(resultSet));
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

		return bets;
	}

	@Override
	public Bet getBetById(Long customerId, Long eventId) {
		if (customerId == null || eventId == null || customerId == 0 ||eventId == 0) {
			throw new IllegalArgumentException("Ids cannot be empty.");
		}
		Bet bet = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("SELECT * FROM bets WHERE customerId = ? AND eventId = ?");
			statement.setLong(1, customerId);
			statement.setLong(2, eventId);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				bet = extractBetFromResult(rs);

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
		return bet;
	}

	@Override
	public List<Bet> getBetsByEvent(Long eventId) {
		Connection connection = null;
		PreparedStatement statement = null;
		List<Bet> bets = new ArrayList<Bet>();

		try {
			connection = getConnection();
			statement = connection
					.prepareStatement("SELECT * FROM bets WHERE eventId = ?");
			statement.setLong(1, eventId);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				bets.add(extractBetFromResult(resultSet));
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

		return bets;
	}

	@Override
	public void settleBetById(Long id) {
		if(id == null || id == 0){
			throw new IllegalArgumentException("Updating non existing bet.");
		}
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("UPDATE BETS set isSettled = 1 WHERE id = ?");
			statement.setLong(1, id);
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Database error;");
			}
		throw new RuntimeException("Database error;");
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
		
		
	}

}
