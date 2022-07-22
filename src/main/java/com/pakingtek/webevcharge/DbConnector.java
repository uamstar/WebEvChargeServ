package com.pakingtek.webevcharge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * 用來連結資料庫的物件。預設會使用tomcat提供的DataSource，若沒有時，讀取設定中給junit用的DB連線設定來用
 * @author laihioh
 *
 */
public class DbConnector {

	private static DataSource DATA_SOURCE;
	static final Logger LOG = LoggerFactory.getLogger(DbConnector.class);
	static final Marker SERVER_MARKER = MarkerFactory.getMarker("SERVER");
	
	static{
		
		// initial data source
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		
		try{
			InitialContext initCtx = new InitialContext(env);
			DATA_SOURCE = (DataSource) initCtx.lookup("java:comp/env/jdbc/myDs");
			LOG.info(SERVER_MARKER, "DATA_SOURCE initialized.");
		}catch(NamingException ne){
			ne.printStackTrace();
			LOG.info(SERVER_MARKER, "DATA_SOURCE initialization failed, get connection from DriverManager afterward.");
		}
		
	}
	
	/**
	 * 提供ＤＢ的connection物件
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException{
		if(DATA_SOURCE != null)
			return DATA_SOURCE.getConnection();
		else 
			throw new SQLException("Tomcat datasource not set!");
	}
}
