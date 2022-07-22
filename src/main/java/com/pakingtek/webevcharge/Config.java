package com.pakingtek.webevcharge;

import java.io.IOException;
import java.util.Properties;

public class Config {
	public static boolean SMS_ENABLE = false;
	public static boolean DEV_MODE = false;
	
	static{
		Properties prop = new Properties();
	 
		try {
	 
			// load a properties file
			prop.load(Config.class.getResourceAsStream("config.properties"));
	 
			// get the property value and print it out
			SMS_ENABLE = ("1".equals(prop.getProperty("sms_enable")));
			DEV_MODE = ("1".equals(prop.getProperty("dev_mode")));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
