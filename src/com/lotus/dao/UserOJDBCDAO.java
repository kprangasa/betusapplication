package com.lotus.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.lotus.users.User;

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
	private static Connection getConnection() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "BETUS_APPLICATION",
				"password");
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
			statement.setString(4, newUser.getType());

			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addBalance(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getUserByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
