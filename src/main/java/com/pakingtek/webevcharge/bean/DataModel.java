package com.pakingtek.webevcharge.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.pakingtek.webevcharge.DbConnector;
import com.pakingtek.webevcharge.Util;

public class DataModel {

	static final Logger LOG = LoggerFactory.getLogger(DataModel.class);
	static final Marker USER_MARKER = MarkerFactory.getMarker("USER");
	static final Marker SERVER_MARKER = MarkerFactory.getMarker("SERVER");

	private Connection conn = null;
	private Statement statement = null;
	private PreparedStatement pStatement = null;
	private ResultSet resultSet = null;
	private Util util;
	
	public DataModel() {
		util = new Util();
	}
	
	private static String SQL_GET_ALL_USERS = "SELECT * FROM users";
	public Set<User> getAllUsers() throws SQLException {
		Set<User> users = new HashSet<User>();
		
		conn = DbConnector.getConnection();
		pStatement = conn.prepareStatement(SQL_GET_ALL_USERS);
		
		resultSet = pStatement.executeQuery();
		
		while(resultSet.next()){
			User user = new User();
			user.setUserId(resultSet.getString(1));
			user.setStatus(UserStatus.valueOf(resultSet.getString(2)));
			user.setPhoneNum(resultSet.getString(3));
			user.setVerfCode(resultSet.getString(4));
			user.setUserName(resultSet.getString(5));
			user.setEmail(resultSet.getString(6));
			user.setMailState(resultSet.getString(7));
			user.setPwd(resultSet.getString(8));
			user.setCreditCardNum(resultSet.getString(9));
			user.setCreateDate(resultSet.getTimestamp(10));
			user.setLastUpdateDate(resultSet.getTimestamp(11));
			
			users.add(user);
		}
		
	    closeAll(resultSet, pStatement, conn);
	   	
		return users;
	}
	
	private static String SQL_CREATE_USER = "INSERT INTO users (user_id, status, phone_num, create_date, last_update_date) VALUES(?,?,?,?,?)";
	public User createUser(String phoneNum, String verfCode, UserStatus status) throws SQLException{
		conn = DbConnector.getConnection();
		// 取得不重複的user_id;
		String userId = getNewUserId(conn);
		pStatement = conn.prepareStatement(SQL_CREATE_USER);
		pStatement.setString(1, userId);
		pStatement.setString(2, status.name());
		pStatement.setString(3, phoneNum);
		java.sql.Timestamp createDate = new java.sql.Timestamp(System.currentTimeMillis());
		pStatement.setTimestamp(4, createDate);
		pStatement.setTimestamp(5, createDate);
				
		pStatement.executeUpdate();
		
	    closeAll(resultSet, pStatement, conn);
	    
	    User user = new User();
	    user.setUserId(userId);
	    user.setPhoneNum(phoneNum);
	    user.setVerfCode(verfCode);
	    user.setCreateDate(createDate);
	    user.setLastUpdateDate(createDate);
	    user.setStatus(status);
	    	   		
		LOG.info(USER_MARKER, "user {} created.", userId);
		
		return user;
	}
	private static String SQL_UPDATE_USER = "UPDATE users SET name=?, email=?, passwd=?, last_update_date=?, status=? WHERE user_id=?";
	public java.sql.Timestamp updateUser(String userId, String name, String email, String passwd, UserStatus status)  throws SQLException
	{
		conn = DbConnector.getConnection();

		pStatement = conn.prepareStatement(SQL_UPDATE_USER);
		pStatement.setString(1, name);
		pStatement.setString(2, email);
		pStatement.setString(3, passwd);
		java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
		pStatement.setTimestamp(4, now);
		pStatement.setString(5, status.name());
		pStatement.setString(6, userId);
		
		pStatement.executeUpdate();
		
		closeAll(resultSet, pStatement, conn);
		
		return now;
	}
	private static String SQL_UPDATE_USER_STATUS = "UPDATE users SET status=? WHERE user_id=?";
	public void updateUserStatus(String userId, UserStatus status) throws SQLException
	{
		conn = DbConnector.getConnection();

		pStatement = conn.prepareStatement(SQL_UPDATE_USER_STATUS);
		pStatement.setString(1, status.name());
		pStatement.setString(2, userId);
		
		pStatement.executeUpdate();
		
		closeAll(resultSet, pStatement, conn);
	}
	private static String SQL_UPDATE_USER_VERF_CODE = "UPDATE users SET verf_code=?, last_update_date=? WHERE user_id=?";
	public java.sql.Timestamp updateVerCodeOfUser(String userId, String newVerfCode)  throws SQLException
	{
		conn = DbConnector.getConnection();

		pStatement = conn.prepareStatement(SQL_UPDATE_USER_VERF_CODE);
		pStatement.setString(1, newVerfCode);
		java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
		pStatement.setTimestamp(2, now);
		pStatement.setString(3, userId);
		
		pStatement.executeUpdate();
		
		closeAll(resultSet, pStatement, conn);
		
		return now;
	}
	private static String SQL_CHECK_USER_ID_DUPLICATION = "SELECT COUNT(user_id) FROM users WHERE user_id=?";
	private String getNewUserId(Connection con) throws SQLException{
		String user_id = util.generateUserId();
		boolean gotIt = false;
		// 先檢查user_id有沒有重複
		pStatement = con.prepareStatement(SQL_CHECK_USER_ID_DUPLICATION);
		pStatement.setString(1, user_id);
		ResultSet r = pStatement.executeQuery();

		while(r.next() && !gotIt){
			if(0 == r.getInt(1)){
				gotIt = true;
			}else{
				if (r != null) {
					try {
						r.close();
					} catch (SQLException e) {
					} // nothing we can do
				}
				user_id = util.generateUserId();
				pStatement.setString(1, user_id);
				r = pStatement.executeQuery();
			}			
		}
		
		if (r != null) {
			try {
				r.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (pStatement != null) {
			try {
				pStatement.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		
		return user_id;
	}
	private static void closeAll(ResultSet resultSet, Statement statement,
			Connection connection) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
	}
	
}
