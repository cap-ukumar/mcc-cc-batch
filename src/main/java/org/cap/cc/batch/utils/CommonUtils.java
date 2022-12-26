package org.cap.cc.batch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonUtils {
	
	
	public static Properties configs ;
	
	
	public static void loadProperties(InputStream inputStream) {
		try {
			configs = new Properties();
			configs.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key) {
		return configs.getProperty(key);
	}
	

}
