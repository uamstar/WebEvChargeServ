package com.pakingtek.webevcharge;

import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.pakingtek.webevcharge.bean.DataCache;
import com.pakingtek.webevcharge.bean.DataModel;
import com.pakingtek.webevcharge.bean.User;

@WebListener
public class ServerInit implements ServletContextListener {

	static final Logger LOG = LoggerFactory.getLogger(ServerInit.class);
	static final Marker SERVER_MARKER = MarkerFactory.getMarker("SERVER");
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		// 將資料庫中與帳號有關的資料載入快取中
		DataModel dataModel = new DataModel();
		try {
			Set<User> users = dataModel.getAllUsers();
			this.loadDbDataToCache(DataCache.INSTANCE, users);
			
		}catch(Exception se) {
			se.printStackTrace();
			LOG.error(SERVER_MARKER, "can not inite cache! msg: {}", se.getMessage());
		}
		
		
	}
	
	// 將資料集合載入快取中
	private void loadDbDataToCache(DataCache dataCache, Set<User> users) {
		
		for(User user : users) {
			dataCache.addUser(user);
			LOG.debug(SERVER_MARKER, "user {} added to cache.", user.getUserId());
		}
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
}
