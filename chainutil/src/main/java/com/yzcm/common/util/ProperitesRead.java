package com.yzcm.common.util;
import java.util.Properties;
public class ProperitesRead {
	
	public static String getProperties(String key){
		String value="";
		try {
			Properties properties=new Properties(); 
			properties.load(ProperitesRead.class.getClassLoader().getResourceAsStream("application.properties"));
			value=properties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String getRedis(String key){
		String value="";
		try {
			Properties properties=new Properties(); 
			properties.load(ProperitesRead.class.getClassLoader().getResourceAsStream("application.properties"));
			value=properties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
