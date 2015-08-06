package com.lotus.userdao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.lotus.users.User;
import com.lotus.users.UserType;

public class UserOJDBCDAO implements UserDao{
	
	
	private static UserOJDBCDAO instance = null;

	public static UserOJDBCDAO getInstance() {
		if (instance == null) {
			instance = new UserOJDBCDAO();
		}
		return instance;
	}
	
	private UserOJDBCDAO() {
		 
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
	public void createUser(User newUser) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO USERS(id,username,balance,password,type) VALUES(users_seq.nextval, ?, ?, ? ,?)");
			statement.setString(1, newUser.getUsername());
			statement.setBigDecimal(2, newUser.getBalance());
			statement.setString(3, newUser.getPassword());
			statement.setString(4, newUser.getType().toString());

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
	public List<User> listUsers() {
		Connection connection = null;
		PreparedStatement statement = null;	
		List<User> users = new ArrayList<User>();
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("SELECT * FROM users WHERE type = ?");
			statement.setString(1, UserType.CUSTOMER.toString());
			
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()){
				users.add(extractUserFromResult(resultSet));
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
		
		
		return users;
	}

	@Override
	public void addBalance(User user, BigDecimal balance) {
		if(user == null){
			throw new IllegalArgumentException("Adding balance to non existing user.");
		}
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("UPDATE USERS set balance = ? WHERE username = ?");
			statement.setString(2, user.getUsername());
			statement.setBigDecimal(1, user.getBalance().add(balance));
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

	@Override
	public User getUserByName(String username) {
		if(username == null || username.isEmpty()){
			throw new IllegalArgumentException("Name cannot be empty.");
		}
		User user = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = getConnection();
			statement = connection.prepareStatement("SELECT id, username, balance, password, type from users where username = ?");
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
		
			if(rs.next()){
				user = extractUserFromResult(rs);
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
		return  user;
	}
	@Override
	public User getUserById(Long userId) {
		if(userId == null || userId == 0){
			throw new IllegalArgumentException("Non existing users.");
		}
		User user = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = getConnection();
			statement = connection.prepareStatement("SELECT id, username, balance, password, type from users where id = ?");
			statement.setLong(1, userId);
			ResultSet rs = statement.executeQuery();
		
			if(rs.next()){
				user = extractUserFromResult(rs);
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
		return  user;
	}
	private static User extractUserFromResult(ResultSet rs) throws SQLException {
		long id = rs.getLong("id");
		String username = rs.getString("username");
		BigDecimal balance = rs.getBigDecimal("balance");
		String password = rs.getString("password");
		String type = rs.getString("type");
		
		User user = new User(id, username, password, balance, UserType.valueOf(type));
		
		return user;
	}

}
