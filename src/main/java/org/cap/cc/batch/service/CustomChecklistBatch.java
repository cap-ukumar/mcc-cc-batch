package org.cap.cc.batch.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cap.cc.batch.utils.CapConfigConstants;
import org.cap.cc.batch.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class CustomChecklistBatch {
	private static  RestTemplate restTemplate;
	
	Logger logger = LoggerFactory.getLogger(CustomChecklistBatch.class);
	
	public static void processData() {

		
		if(restTemplate==null) {
			getRestTemplate();
		}	
	}
	
	
	public static void getRestTemplate() {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();

		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));        
		messageConverters.add(converter);  
		restTemplate.setMessageConverters(messageConverters); 
	}
	
	
	public static ResultSet getInformixDbResults(String query)  {

		ResultSet rs = null;
		 try (Connection connection = DriverManager.getConnection(CommonUtils.getProperty(CapConfigConstants.INFORMIX_URL),
				 CommonUtils.getProperty(CapConfigConstants.INFORMIX_USERNAME),
				 CommonUtils.getProperty(CapConfigConstants.INFORMIX_PASSWORD));
	                Statement statement = connection.createStatement();) {
	            rs = statement.executeQuery(query);
	           if(rs==null) {
	        	   // add logger or do something 
	           }
	        	   	
	        }
	        catch (Exception e) {
	        	
	        }
		return rs;
	}
	
	
	public static ResultSet getOracleDbResults(String query)  {

		ResultSet rs = null;
		 try (Connection connection = DriverManager.getConnection(CommonUtils.getProperty(CapConfigConstants.ORACLE_URL),
				 CommonUtils.getProperty(CapConfigConstants.ORACLE_USERNAME),
				 CommonUtils.getProperty(CapConfigConstants.ORACLE_PASSWORD));
	                Statement statement = connection.createStatement();) {
	            rs = statement.executeQuery(query);
	           if(rs==null) {
	        	   // add logger or do something 
	           }
	        	   	
	        }
	        catch (Exception e) {
	        	
	        }
		return rs;
	}
	

}
