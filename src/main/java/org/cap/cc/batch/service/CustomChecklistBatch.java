package org.cap.cc.batch.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cap.cc.batch.dao.CustomChecklistDAO;
import org.cap.cc.batch.model.BasicChecklistEntity;
import org.cap.cc.batch.utils.CapConfigConstants;
import org.cap.cc.batch.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class CustomChecklistBatch {
	private static RestTemplate restTemplate;

	static Logger logger = LoggerFactory.getLogger(CustomChecklistBatch.class);

	private static Connection INFORMIX_CONNECTION = null;

	public static void processData() {
		//Make DB Connection
		createInformixDbConnection();
		
		//Get CustomChecklist FilePath
		final String customChecklistFilePath = getCustomChecklistFilePath();
		if(null!=customChecklistFilePath && !customChecklistFilePath.isBlank())
			logger.info("filePath: {}",customChecklistFilePath);
		
		//Get Available TaskId
		final Integer taskId = getAvailableTaskId();
		if(null!=taskId)
			logger.info("taskId: {}",taskId);
		
		//Update User_u of ptt_task
		
		//Get Basic Checklist Details
		final List<BasicChecklistEntity> checklists = BasicChecklistEntity.getBasicChecklistDetails(INFORMIX_CONNECTION, taskId);
		if(null!=checklists)
			logger.info("BasicChecklists: {}",checklists);
		
		//
		
		//remove DB connections		
		removeConnections();
		
		/*
		 * if(restTemplate==null) { getRestTemplate(); }
		 */
	}

	private static void removeConnections() {
		
			try {
				if(INFORMIX_CONNECTION!=null)
					INFORMIX_CONNECTION.close();
//				System.out.println(INFORMIX_CONNECTION.createStatement());
			} catch (SQLException e) {
				e.printStackTrace();
			}

	}

	private static String getCustomChecklistFilePath() {
		String path = null;
		ResultSet rs=null;
		try(Statement st=INFORMIX_CONNECTION.createStatement();) {			
			rs=st.executeQuery(CustomChecklistDAO.GET_CUSTOM_CHECKLIST_FILE_PATH);
			while(null!=rs && rs.next()) {
				path=rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getCustomChecklistFilePath");
		}
		return path;
	}

	private static Integer getAvailableTaskId() {
		Integer taskId = null;
		ResultSet rs=null;
		try(Statement st=INFORMIX_CONNECTION.createStatement();) {			
			rs=st.executeQuery(CustomChecklistDAO.GET_TASK_ID);
			while(null!=rs && rs.next()) {
				taskId=rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getCustomChecklistFilePath");
		}
		return taskId;
	}

	public static void getRestTemplate() {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();

		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		restTemplate.setMessageConverters(messageConverters);
	}

	public static ResultSet getInformixDbResults(String query) {

		ResultSet rs = null;
		try (Connection connection = DriverManager.getConnection(
				CommonUtils.getProperty(CapConfigConstants.INFORMIX_URL),
				CommonUtils.getProperty(CapConfigConstants.INFORMIX_USERNAME),
				CommonUtils.getProperty(CapConfigConstants.INFORMIX_PASSWORD));
				Statement statement = connection.createStatement();) {
			rs = statement.executeQuery(query);
			if (rs == null) {
				return null;

			} else {
				return rs;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static ResultSet getOracleDbResults(String query) {

		ResultSet rs = null;
		try (Connection connection = DriverManager.getConnection(CommonUtils.getProperty(CapConfigConstants.ORACLE_URL),
				CommonUtils.getProperty(CapConfigConstants.ORACLE_USERNAME),
				CommonUtils.getProperty(CapConfigConstants.ORACLE_PASSWORD));
				Statement statement = connection.createStatement();) {
			rs = statement.executeQuery(query);
			if (rs == null) {
				// add logger or do something
			}

		} catch (Exception e) {

		}
		return rs;
	}

	public static void createInformixDbConnection() {
		try {
			INFORMIX_CONNECTION = DriverManager.getConnection(CommonUtils.getProperty(CapConfigConstants.INFORMIX_URL),
					CommonUtils.getProperty(CapConfigConstants.INFORMIX_USERNAME),
					CommonUtils.getProperty(CapConfigConstants.INFORMIX_PASSWORD));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
