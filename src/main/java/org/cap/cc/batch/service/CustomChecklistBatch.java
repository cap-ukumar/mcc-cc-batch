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
import org.cap.cc.batch.model.BasicChecklistEntity;
import org.cap.cc.batch.model.ContentChannel;
import org.cap.cc.batch.model.PrinterData;
import org.cap.cc.batch.utils.CapConfigConstants;
import org.cap.cc.batch.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomChecklistBatch {
	private static RestTemplate restTemplate;

	static Logger logger = LoggerFactory.getLogger(CustomChecklistBatch.class);

	private static Connection INFORMIX_CONNECTION;

	public static void processData() {

		// Make DB Connection
		createInformixDbConnection();

		// Get CustomChecklist FilePath
		final String ccFilePath = getCustomChecklistFilePath();
		if (null != ccFilePath && !ccFilePath.isBlank())
			logger.info("filePath: {}", ccFilePath);

		// Get Available TaskId
		final Integer ccTaskId = getAvailableTaskId();
		if (null != ccTaskId)
			logger.info("taskId: {}", ccTaskId);

		// Update User_u of ptt_task
		// ...

		// Get CAP Domain
		final String CAP_DOMAIN = getCapDomain();
		if (null != CAP_DOMAIN && !CAP_DOMAIN.isBlank()) {
			logger.info("CAP-Domain: {}", CAP_DOMAIN);
		}

		// Get Checklist Webservice Url
		final String ccWebServiceUrl = getCustomChecklistWebServiceUrl();
		if (null != ccWebServiceUrl && !ccWebServiceUrl.isBlank())
			logger.info("WebService-Url: {}", ccWebServiceUrl);

		// Get Job Status Polling Interval
		Integer pollingInterval = getPollingInterval();
		logger.info("pollingInterval: {}", pollingInterval);

		// Get Job Status Polling Iterations
		Integer Iteration = getJobIterations();
		logger.info("Iterations: {}", Iteration);

		// Get Basic Checklist Details
		final List<BasicChecklistEntity> checklists = BasicChecklistEntity.getBasicChecklistDetails(INFORMIX_CONNECTION,
				ccTaskId);
		if (null != checklists)
			for (BasicChecklistEntity checklist : checklists) {
				
				// Set UserName for BasicChecklist Class
				checklist.setUserName(CAP_DOMAIN);
				
				String printSetDetailC = checklist.getPrintSetDetailC();
				String packetType = checklist.getPacketType();
				String editionId = checklist.getEditionId();

				// Get Duplex Value
				String duplexvalue = getDuplexValue(printSetDetailC);
				if (null != duplexvalue)
					logger.info("Duplex value: {}", duplexvalue);

				// Get Staple Value
				String staplevalue = getStapleValue(printSetDetailC);
				if (null != staplevalue)
					logger.info("Staple value: {}", staplevalue);

				// Get Media Color
				String color = getMediaColor(printSetDetailC);
				if (null != color)
					logger.info("Media color: {}", color);

				// Get Media Type
				String media = getMediaType(printSetDetailC);
				if (null != media)
					logger.info("Media Type: {}", media);

				// Get Content & Channel
				ContentChannel contentChannel = getContentChannel(packetType);
				if (null != contentChannel)
					logger.info("{}", contentChannel);

				// Get Inspector-Channel-Flag & Update ContentChannel
				String chkInsp = getChecklistInspectorChannel(editionId);
				if (null != chkInsp && null != contentChannel) {
					logger.info("Checklist inspector: {}", chkInsp);
					if (chkInsp.equalsIgnoreCase("y")) {
						contentChannel.setContent("CUSTOM");
						contentChannel.setChannel("PRNFINAL");
						logger.info("Updated channel: {}", contentChannel);
					}
				}

				// Update Checklist
				if (null != contentChannel) {
					checklist.setOutputOptions(contentChannel.getContent());
					checklist.setChannelData(contentChannel.getChannel());
				}

				// Create PrinterData
				PrinterData printerData = new PrinterData();
				if (null != duplexvalue)
					printerData.setDuplex(duplexvalue.equalsIgnoreCase("y"));
				if (null != staplevalue)
					printerData.setStaple(staplevalue.equalsIgnoreCase("y"));
				printerData.setMediaColor(color);
				printerData.setMediaType(media);
				printerData.setFilePath(ccFilePath, ccTaskId, checklist.getItemSeqNo(), checklist.getAuId(),
						checklist.getSuId(), checklist.getModuleId(), checklist.getEditionId());

				// Set PrinterData for Checklist
				checklist.setPrinterData(printerData);

				//Create Json request
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(checklist);
					logger.info("{}",jsonString);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

		// remove DB connections
		removeConnections();
	}
	/*
	 * if(restTemplate==null) { getRestTemplate(); }
	 */

	private static String getCustomChecklistFilePath() {
		String path = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistDAO.GET_CUSTOM_CHECKLIST_FILE_PATH);
			if (null != rs && rs.next()) {
				path = rs.getString(1);

			}
		} catch (Exception e) {
			logger.debug("Exception in getCustomChecklistFilePath");
		}
		return path;

	}

	private static Integer getAvailableTaskId() {
		Integer taskId = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistDAO.GET_TASK_ID);
			if (null != rs && rs.next()) {
				taskId = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getAvailableTaskId");
		}
		return taskId;
	}

	private static int updateUser_u_column(int programId, int taskId) {
		Integer result = null;
		String specialInstrT = CommonUtils.getUUID();
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.UPDATE_USER_U);) {
			st.setString(1, specialInstrT);
			st.setInt(2, programId);
			st.setInt(3, taskId);
			result = st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String getCapDomain() {
		String capDomain = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistDAO.GET_CAP_DOMAIN);
			if (null != rs && rs.next()) {
				capDomain = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getCapDomain");
		}
		return capDomain;
	}

	private static String getCustomChecklistWebServiceUrl() {
		String url = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistDAO.GET_CHECKLIST_WEBSERVICE_URL);
			if (null != rs && rs.next()) {
				url = rs.getString(1);
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
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistDAO.GET_JOB_STATUS_POLLING_INTERVAL);
			if (null != rs && rs.next()) {
				pollingInterval = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getPollingInterval");
			e.printStackTrace();
		}
		return pollingInterval;
	}

	/*
	 * if(restTemplate==null) { getRestTemplate(); }
	 */

	private static Integer getJobIterations() {
		Integer iteration = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistDAO.GET_JOB_COMPLETION_ITERATIONS);
			if (null != rs && rs.next()) {
				iteration = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getJobIterations");
		}
		return iteration;
	}

	private static String getDuplexValue(String print_set_detail_c) {
		String dupvalue = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_DUPLEX_VALUE);) {
			st.setString(1, print_set_detail_c);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				dupvalue = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getDuplexValue");
		}
		return dupvalue;
	}

	private static String getStapleValue(String print_set_detail_c) {
		String stapvalue = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_STAPLE_VALUE);) {
			st.setString(1, print_set_detail_c);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				stapvalue = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getStapleValue");
		}
		return stapvalue;
	}

	private static String getMediaColor(String print_set_detail_c) {
		String medcolour = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_MEDIA_COLOR);) {
			st.setString(1, print_set_detail_c);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				medcolour = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getMediaColor");
		}
		return medcolour;
	}

	private static String getMediaType(String print_set_detail_c) {
		String mediatype = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_MEDIA_TYPE);) {
			st.setString(1, print_set_detail_c);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				mediatype = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getMediaType");
		}
		return mediatype;
	}

	public static ContentChannel getContentChannel(String packetType) {
		ResultSet rs = null;
		ContentChannel chetity = null;
		try (PreparedStatement ps = INFORMIX_CONNECTION.prepareStatement(CustomChecklistDAO.GET_CHECKLIST_CONTENT);) {
			ps.setString(1, packetType);
			rs = ps.executeQuery();

			if (null != rs && rs.next()) {
				chetity = new ContentChannel();
				chetity.setContent(rs.getString("ls_content"));
				chetity.setChannel(rs.getString("ls_channel"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chetity;

	}

	private static String getChecklistInspectorChannel(String edition) {
		String inspector = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION
				.prepareStatement(CustomChecklistDAO.GET_CHECKLIST_INSPECTOR_CHANNEL);) {
			st.setString(1, edition);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				inspector = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getchecklistinspectorchannel");
		}
		return inspector;
	}

	public static void getRestTemplate() {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();

		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		restTemplate.setMessageConverters(messageConverters);
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

	private static void removeConnections() {

		try {
			if (INFORMIX_CONNECTION != null)
				INFORMIX_CONNECTION.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}