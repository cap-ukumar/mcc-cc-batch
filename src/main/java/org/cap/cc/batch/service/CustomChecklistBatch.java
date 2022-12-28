package org.cap.cc.batch.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cap.cc.batch.dao.CustomChecklistDAO;
import org.cap.cc.batch.model.CheckListChannelEntity;
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

		// Make DB Connection
		createInformixDbConnection();

		String duplexvalue= getDuplexValue();
		logger.info(duplexvalue);
		

		
		
		//Get CustomChecklist FilePath
		final String ccFilePath = getCustomChecklistFilePath();
		if(null!=ccFilePath && !ccFilePath.isBlank())
			logger.info("filePath: {}",ccFilePath);
		
		//Get Available TaskId
		final Integer ccTaskId = getAvailableTaskId();
		if(null!=ccTaskId)
			logger.info("taskId: {}",ccTaskId);
		
		//Update User_u of ptt_task
		//...
		
		//Get Basic Checklist Details
		final List<BasicChecklistEntity> checklists = BasicChecklistEntity.getBasicChecklistDetails(INFORMIX_CONNECTION, ccTaskId);
		if(null!=checklists)
			checklists.forEach(item->logger.info("{}",item));
		
		//Get CAP Domain
		final String CAP_DOMAIN = getCapDomain();
		if(null!=CAP_DOMAIN && !CAP_DOMAIN.isBlank())
			logger.info("CAP-Domain: {}",CAP_DOMAIN);
		
		//Get Checklist Webservice Url
		final String ccWebServiceUrl = getCustomChecklistWebServiceUrl();
		if(null!=ccWebServiceUrl && !ccWebServiceUrl.isBlank())
			logger.info("WebService-Url: {}",ccWebServiceUrl);

		
		String staplevalue=getStapleValue();
		logger.info(staplevalue);
		
		
		String color=getMediaColor();
		logger.info(color);
		
		
		String media=getMediaType();
		logger.info(media);
		
		String packettype="SELFEVLPKT";
		CheckListChannelEntity channel=getContentChannel(packettype);
		logger.info(channel.toString());
		
		//Get Job Status Polling Interval
		Integer pollingInterval = getPollingInterval();
		logger.info("pollingInterval: {}",pollingInterval);
		
		
		// remove DB connections
		removeConnections();
	}
		/*
		 * if(restTemplate==null) { getRestTemplate(); }
		 */


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
	
	private static String getDuplexValue() {
		String dupvalue = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_DUPLEX_VALUE);) {
			st.setString(1, "CHECKLSTSE");
			rs = st.executeQuery();
			while (rs.next()) {
				dupvalue = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getDuplexValue");
		}
		return dupvalue;
	}
	
	private static String getStapleValue() {
		String stapvalue = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_STAPLE_VALUE);) {
			st.setString(1, "CHECKLSTSE");
			rs = st.executeQuery();
			while (rs.next()) {
				stapvalue = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getStapleValue");
		}
		return stapvalue;
	}

	
	private static String getMediaColor() {
		String medcolour = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_MEDIA_COLOR);) {
			st.setString(1, "CHECKLSTSE");
			rs = st.executeQuery();
			while (rs.next()) {
				medcolour = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getMediaColor");
		}
		return medcolour;
	}
	
	private static String getMediaType() {
		String mediatype = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_MEDIA_TYPE);) {
			st.setString(1, "CHECKLSTSE");
			rs = st.executeQuery();
			while (rs.next()) {
				mediatype = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getMediaType");
		}
		return mediatype;
	}
	
	public static CheckListChannelEntity getContentChannel(String packettype) {
		ResultSet rs = null;
		CheckListChannelEntity chetity = null;
		try (PreparedStatement ps = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_CHECKLIST_CONTENT);) {
			ps.setString(1, packettype);
			rs = ps.executeQuery();

			while (null != rs && rs.next()) {
				chetity =new CheckListChannelEntity();
				chetity.setContent(rs.getString(1));
				chetity.setChannel(rs.getString(2));
				logger.info(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chetity;

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
			logger.debug("Exception in getAvailableTaskId");
		}
		return taskId;
	}

	private static String getCapDomain() {
		String capDomain = null;
		ResultSet rs=null;
		try(Statement st=INFORMIX_CONNECTION.createStatement();) {			
			rs=st.executeQuery(CustomChecklistDAO.GET_CAP_DOMAIN);
			while(null!=rs && rs.next()) {
				capDomain=rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getCapDomain");
		}
		return capDomain;
	}

	private static String getCustomChecklistWebServiceUrl() {
		String url = null;
		ResultSet rs=null;
		try(Statement st=INFORMIX_CONNECTION.createStatement();) {			
			rs=st.executeQuery(CustomChecklistDAO.GET_CHECKLIST_WEBSERVICE_URL);
			while(null!=rs && rs.next()) {
				url=rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getCapDomain");
		}
		return url;
	}

	/*
		 * if(restTemplate==null) { getRestTemplate(); }
		 */
	
	
	private static Integer getPollingInterval() {
		Integer pollingInterval = null;
		ResultSet rs=null;
		try(Statement st=INFORMIX_CONNECTION.createStatement();) {			
			rs=st.executeQuery(CustomChecklistDAO.GET_JOB_STATUS_POLLING_INTERVAL);
			while(null!=rs && rs.next()) {
				pollingInterval=rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getPollingInterval");
			e.printStackTrace();
		}
		return pollingInterval;
	}
	
	private static int updateUser_u_column(int programId, int taskId) {
		Integer result = null;
		try(PreparedStatement st=INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.UPDATE_USER_U);){
			st.setInt(1, programId);
			st.setInt(2, taskId);
			result=st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
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