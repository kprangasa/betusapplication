package com.lotus.users;

import java.math.BigDecimal;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.lotus.dao.UserDao;
import com.lotus.dao.UserOJDBCDAO;

public class User {
	private Long id;
	private String username;
	private String password;
	private BigDecimal balance;
	private UserType type;
	
	public User(String username, String password, BigDecimal balance,
			UserType type) {
		super();
		this.username = username;
		this.password = password;
		this.balance = balance;
		this.type = type;
	}
	public User(Long id, String username, String password, BigDecimal balance,
			UserType type) {
		this(username, password, balance, type);
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	@JsonIgnore
	public void setPassword(String password) {
		this.password = password;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public UserType getType() {
		return type;
	}
	public void setType(UserType type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", balance=" + balance + ", type=" + type + "]";
	}
	
	public boolean persist() {
		UserDao userDAO = UserOJDBCDAO.getInstance();
		
		User existingUser = userDAO.getUserByName(username);
		
		if(existingUser == null) {
			userDAO.createUser(this);
			System.out.println("User Created");
			return true;
		} else {
			
			System.out.println("Error: User already exists");
			return false;
		}
		
	}
}
