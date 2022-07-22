package com.pakingtek.webevcharge;


import java.sql.SQLException;
import java.util.List;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.pakingtek.webevcharge.bean.DataCache;
import com.pakingtek.webevcharge.bean.DataModel;
import com.pakingtek.webevcharge.bean.User;
import com.pakingtek.webevcharge.bean.UserStatus;

/**
 * 作為帳號相關操作的介面。所有對 user 的CRUD都要經過這個class
 * @author laihioh
 */
public class AccountManager {
	static final Logger LOG = LoggerFactory.getLogger(AccountManager.class);
	static final Marker SERVER_MARKER = MarkerFactory.getMarker("SERVER");
	static final Marker USER_MARKER = MarkerFactory.getMarker("USER");
	private static final int RESET_WAITING_TIME = 180000;	// 等待重置產品的時間，180秒
	
	private DataModel dataModel;
	private BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
	
	public AccountManager() {
		this.dataModel = new DataModel();
	}
	
	public AccountManager(DataModel dm) 
	{
		this.dataModel = dm;
	}
	
	/**
	 * 根據ID從快取中取得使用者物件
	 * @param userId 使用者ID
	 * @return 使用者物件
	 */
	public User getUserById(String userId) {
		return DataCache.INSTANCE.getUserById(userId);
	}
	
	/**
	 * 根據 phoneNum 從快取中取得使用者物件
	 * @param phoneNum
	 * @return
	 */
	public User getUserByPhoneNum(String phone) {
		return DataCache.INSTANCE.getUserByPhoneNum(phone);
	}
	
	/**
	 * 取得所有使用者物件
	 * @return
	 */
	public List<User> getAllUsers() {
		return DataCache.INSTANCE.getAllUsers();
	}
	
	/**
	 * 建立使用者帳號。在註冊程序開始會呼叫這個method
	 * @param phoneNum
	 * @param verfCode	驗證手機號碼的驗證碼。使用者在APP填上這個驗證碼
	 * @return	代表使用者的物件
	 * @throws WebEvException
	 */
	public User createUser(String phoneNum, String verfCode) throws WebEvException{
		User user = null;
		try {
			user = dataModel.createUser(phoneNum, verfCode, UserStatus.NEW);
			DataCache.INSTANCE.addUser(user);
			
		}catch(SQLException se) {
			se.printStackTrace();
			throw new WebEvException(Msg.ERR9999);
		}
		return user;
	}
	public User updateUser(String userId, String name, String email, String passwd, UserStatus status) throws WebEvException
	{
		// 加密密碼
		String encryptPasswd = this.passwordEncryptor.encryptPassword(passwd);
		
		try {
			java.sql.Timestamp now = this.dataModel.updateUser(userId, name, email, encryptPasswd, status);
			User user = DataCache.INSTANCE.getUserById(userId);
			user.setEmail(email);
			user.setUserName(name);
			user.setPwd(encryptPasswd);
			user.setLastUpdateDate(now);
			user.setStatus(status);
			
			return user;
		}catch(SQLException se) {
			se.printStackTrace();
			throw new WebEvException(Msg.ERR9999);
		}
	}
	public User updateVerCodeOfUser(String userId, String newVerfCode) throws WebEvException
	{
		try {
			java.sql.Timestamp now = this.dataModel.updateVerCodeOfUser(userId, newVerfCode);
			User user = DataCache.INSTANCE.getUserById(userId);
			user.setVerfCode(newVerfCode);
			user.setLastUpdateDate(now);
			
			return user;
		}catch(SQLException se) {
			se.printStackTrace();
			throw new WebEvException(Msg.ERR9999);
		}
	}
	public User updateUserStatus(String userId, UserStatus newStatus) throws WebEvException
	{
		try {
			this.dataModel.updateUserStatus(userId, newStatus);
			User user = DataCache.INSTANCE.getUserById(userId);
			user.setStatus(newStatus);
			
			return user;
		}catch(SQLException se) {
			se.printStackTrace();
			throw new WebEvException(Msg.ERR9999);
		}
	}
}
