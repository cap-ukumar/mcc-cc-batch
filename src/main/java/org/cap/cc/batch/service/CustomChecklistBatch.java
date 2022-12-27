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

		// Get CustomChecklist FilePath
		String customChecklistFilePath = getCustomChecklistFilePath();
		if (null != customChecklistFilePath && !customChecklistFilePath.isBlank())
			logger.info(customChecklistFilePath);

		// Get Available TaskId
		int taskId;

		
		String duplexvalue= getDuplexValue();
		logger.info(duplexvalue);
		
		
		String staplevalue=getStapleValue();
		logger.info(staplevalue);
		
		
		String color=getMediaColor();
		logger.info(color);
		
		
		String media=getMediaType();
		logger.info(media);
		
		String packettype="";
		CheckListChannelEntity channel=getContentChannel(packettype);
		logger.info(channel.toString());
		// remove DB connections

		removeConnections();
		/*
		 * if(restTemplate==null) { getRestTemplate(); }
		 */
	}

	private static void removeConnections() {

		try {
			if (INFORMIX_CONNECTION != null)
				INFORMIX_CONNECTION.close();
//                System.out.println(INFORMIX_CONNECTION.createStatement());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	
	
	private static String getCustomChecklistFilePath() {
		String path = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistDAO.GET_CUSTOM_CHECKLIST_FILE_PATH);
			while (rs.next()) {
				path = rs.getString(1);
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
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chetity;
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