package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.exceptions.BadUserException;

public class UserDAO {
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User checkLogin(String nickname, String password, String email) throws SQLException{
		String query = "SELECT * FROM user where (nickname = ? OR email = ? ) AND password = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setString(1,nickname);
			pstatement.setString(2, email);
			pstatement.setString(3,password);
			try(ResultSet result = pstatement.executeQuery();){
				if(!result.isBeforeFirst()) //user not present in DB
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("userID"));
					user.setUsername(result.getString("nickname"));
					return user;
				}
			}
		}
		
	}
	
	public User getUserByNickname(String nickname) throws SQLException {
		String query = "SELECT * FROM user where nickname = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setString(1,nickname);
			try(ResultSet result = pstatement.executeQuery();){
				if(!result.isBeforeFirst()) //user not present in DB
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("userID"));
					user.setUsername(result.getString("nickname"));
					return user;
				}
			}
		}
		
	}
	
	boolean isMailAvailable(String mail) throws SQLException {
		String query = "SELECT * FROM user where email = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setString(1,mail);
			try(ResultSet result = pstatement.executeQuery();){
				if(!result.isBeforeFirst())
					return false;
				return true;
			}
		}
	}
	boolean isNicknameAvailable(String nick) throws SQLException {
		String query = "SELECT * FROM user where nickname = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setString(1,nick);
			try(ResultSet result = pstatement.executeQuery();){
				if(!result.isBeforeFirst())
					return false;
				return true;
			}
		}
	}
	
	public void registerUser(String nickname, String email, String password, String repeatPassword) throws SQLException, BadUserException {
		if(nickname == null || nickname.equals(""))
			throw new BadUserException("Not valid nickname");
		if(email == null || email.equals(""))
			throw new BadUserException("Not valid email");
		if(password == null || password.equals(""))
			throw new BadUserException("Not valid password");
		if(!password.equals(repeatPassword))
			throw new BadUserException("Passwords don't match");
		if(!isMailAvailable(email))
			throw new BadUserException("Cannot use this mail");
		if(!isNicknameAvailable(nickname))
			throw new BadUserException("Cannot use this nickname");
			
		
		String query = "INSERT into user (username, email, password) VALUES(?, ?, ?)";
		connection.setAutoCommit(false);
		try(PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, nickname);
			pstatement.setString(2, email);
			pstatement.setString(3, password);
			pstatement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
	}
	
	
}
