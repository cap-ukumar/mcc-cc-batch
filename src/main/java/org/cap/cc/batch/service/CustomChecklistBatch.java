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
import java.util.Optional;

import org.cap.cc.batch.dao.CustomChecklistConstants;
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

public class CustomChecklistBatch implements AutoCloseable {
	private static RestTemplate restTemplate;

	private static Logger logger = LoggerFactory.getLogger(CustomChecklistBatch.class);

	private static Connection INFORMIX_CONNECTION;

	public void processData() {
		try {

			// Get CustomChecklist FilePath
			final String ccFilePath = Optional.ofNullable(getCustomChecklistFilePath())
					.orElseThrow(() -> new Exception("Filepath isn't fetched"));
			if (null != ccFilePath && !ccFilePath.isBlank())
				logger.info("filePath: {}", ccFilePath);

			// Get Available TaskId
			final Integer ccTaskId = Optional.ofNullable(getAvailableTaskId())
					.orElseThrow(() -> new Exception("TaskId isn't fetched"));
			if (null != ccTaskId)
				logger.info("taskId: {}", ccTaskId);

			// Update User_u of ptt_task
			// ...

			// Get CAP Domain
			final String CAP_DOMAIN = Optional.ofNullable(getCapDomain())
					.orElseThrow(() -> new Exception("CAP Domain isn't fetched"));
			if (null != CAP_DOMAIN && !CAP_DOMAIN.isBlank()) {
				logger.info("CAP-Domain: {}", CAP_DOMAIN);
			}

			// Get Checklist Webservice Url
			final String ccWebServiceUrl = Optional.ofNullable(getCustomChecklistWebServiceUrl())
					.orElseThrow(() -> new Exception("WebService Url isn't fetched"));
			if (null != ccWebServiceUrl && !ccWebServiceUrl.isBlank())
				logger.info("WebService-Url: {}", ccWebServiceUrl);

			// Get Job Status Polling Interval
			Integer pollingInterval = Optional.ofNullable(getPollingInterval())
					.orElseThrow(() -> new Exception("Polling Interval isn't fetched"));
			logger.info("pollingInterval: {}", pollingInterval);

			// Get Job Status Polling Iterations
			Integer iteration = Optional.ofNullable(getJobIterations())
					.orElseThrow(() -> new Exception("Iteration are unknown"));
			logger.info("Iterations: {}", iteration);

			// Get Basic Checklist Details
			final List<BasicChecklistEntity> checklists = Optional
					.ofNullable(getBasicChecklistDetails(INFORMIX_CONNECTION, ccTaskId))
					.orElseThrow(() -> new Exception("Checklists are empty for given Taskid: " + ccTaskId));

			// Fetch Checklist Details
			if (null != checklists)
				for (BasicChecklistEntity checklist : checklists) {
					fetchChecklistDetails(ccFilePath, ccTaskId, CAP_DOMAIN, checklist);
				}
		} catch (Exception ex) {
			logger.error("{}", ex.getMessage());
		}
	}

	private String getCustomChecklistFilePath() {
		String path = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_CUSTOM_CHECKLIST_FILE_PATH);
			if (null != rs && rs.next()) {
				path = rs.getString(1);

			}
		} catch (Exception e) {
			logger.debug("Exception in getCustomChecklistFilePath():: {}", e.getMessage());
		}
		return path;

	}

	private Integer getAvailableTaskId() {
		Integer taskId = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_TASK_ID);
			if (null != rs && rs.next()) {
				taskId = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getAvailableTaskId():: {}", e.getMessage());
		}
		return taskId;
	}

	private int updateUser_u_column(int taskId) {
		Integer result = null;
		String specialInstrT = CommonUtils.getUUID();
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistConstants.UPDATE_USER_U);) {
			st.setString(1, specialInstrT);
			st.setInt(2, CustomChecklistConstants.PROGRAM_ID);
			st.setInt(3, taskId);
			result = st.executeUpdate();
		} catch (Exception e) {
			logger.error("Error in updateUser_u_column():: {}", e.getMessage());
		}
		return result;
	}

	private String getCapDomain() {
		String capDomain = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_CAP_DOMAIN);
			if (null != rs && rs.next()) {
				capDomain = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getCapDomain():: {}", e.getMessage());
		}
		return capDomain;
	}

	private String getCustomChecklistWebServiceUrl() {
		String url = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_CHECKLIST_WEBSERVICE_URL);
			if (null != rs && rs.next()) {
				url = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getCustomChecklistWebServiceUrl():: {}", e.getMessage());
		}
		return url;
	}

	private Integer getPollingInterval() {
		Integer pollingInterval = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_JOB_STATUS_POLLING_INTERVAL);
			if (null != rs && rs.next()) {
				pollingInterval = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getPollingInterval():: {}", e.getMessage());
			e.printStackTrace();
		}
		return pollingInterval;
	}

	private Integer getJobIterations() {
		Integer iteration = null;
		ResultSet rs = null;
		try (Statement st = INFORMIX_CONNECTION.createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_JOB_COMPLETION_ITERATIONS);
			if (null != rs && rs.next()) {
				iteration = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getJobIterations():: {}", e.getMessage());
		}
		return iteration;
	}

	private List<BasicChecklistEntity> getBasicChecklistDetails(Connection con, int taskId) {
		ResultSet rs = null;
		List<BasicChecklistEntity> list = null;
		try (PreparedStatement ps = con.prepareStatement(CustomChecklistConstants.GET_BASIC_CHECKLIST_DETAILS);) {
			ps.setInt(1, taskId);
			rs = ps.executeQuery();
			if (null != rs)
				list = new ArrayList<>();
			while (null != rs && rs.next()) {
				BasicChecklistEntity obj = new BasicChecklistEntity();
				obj.setItemSeqNo(rs.getInt(CustomChecklistConstants.ITEM_SEQ_NO));
				obj.setModuleId(rs.getString(CustomChecklistConstants.MODULE_ID));
				obj.setAuId(rs.getInt(CustomChecklistConstants.AU_ID));
				obj.setSuId(rs.getInt(CustomChecklistConstants.SU_ID));
				obj.setEditionId(rs.getString(CustomChecklistConstants.EDITION_ID));
				obj.setActEffectiveDt(rs.getTimestamp(CustomChecklistConstants.ACT_EFFECTIVE_DT).toLocalDateTime()
						.format(CustomChecklistConstants.DATE_TIME_FORMATTER));
				obj.setCycleSeqNo(rs.getInt(CustomChecklistConstants.CYCLE_SEQ_NO));
				obj.setPacketType(rs.getString(CustomChecklistConstants.PACKET_TYPE));
				obj.setPrintSetDetailC(rs.getString(CustomChecklistConstants.PRINT_SET_DETAIL_C));
				list.add(obj);
			}
		} catch (Exception e) {
			logger.debug("Exception in getBasicChecklistDetails():: {}", e.getMessage());
		}
		return list;
	}

	private String getDuplexValue(String printSetDetailC) {
		String dupvalue = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistConstants.GET_DUPLEX_VALUE);) {
			st.setString(1, printSetDetailC);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				dupvalue = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getDuplexValue():: {}", e.getMessage());
		}
		return dupvalue;
	}

	private String getStapleValue(String printSetDetailC) {
		String stapvalue = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistConstants.GET_STAPLE_VALUE);) {
			st.setString(1, printSetDetailC);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				stapvalue = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getStapleValue():: {}", e.getMessage());
		}
		return stapvalue;
	}

	private String getMediaColor(String printSetDetailC) {
		String medcolour = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistConstants.GET_MEDIA_COLOR);) {
			st.setString(1, printSetDetailC);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				medcolour = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getMediaColor():: {}", e.getMessage());
		}
		return medcolour;
	}

	private String getMediaType(String printSetDetailC) {
		String mediatype = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION.prepareStatement(CustomChecklistConstants.GET_MEDIA_TYPE);) {
			st.setString(1, printSetDetailC);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				mediatype = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getMediaType():: {}", e.getMessage());
		}
		return mediatype;
	}

	private ContentChannel getUpdatedContentChannel(String packetType, String editionId) {
		ContentChannel contentChannel = null;
		try {
			contentChannel = getContentChannel(packetType);

			// Get Inspector-Channel-Flag & Update ContentChannel
			String chkInsp = Optional.ofNullable(getChecklistInspectorFlag(editionId))
					.orElseThrow(() -> new Exception("Inspector not fetched"));
			if (null != chkInsp && null != contentChannel) {
				logger.info("Checklist inspector: {}", chkInsp);
				if (chkInsp.equalsIgnoreCase(CustomChecklistConstants.CHECKLIST_INSPECTOR_FLAG)) {
					contentChannel.setContent(CustomChecklistConstants.CHECKLIST_INSPECTOR_CONTENT);
					contentChannel.setChannel(CustomChecklistConstants.CHECKLIST_INSPECTOR_CHANNEL);
					logger.info("Updated channel: {}", contentChannel);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in getUpdatedContentChannel():: {}", e.getMessage());
		}
		return contentChannel;
	}

	private ContentChannel getContentChannel(String packetType) {
		ResultSet rs = null;
		ContentChannel chetity = null;
		try (PreparedStatement ps = INFORMIX_CONNECTION
				.prepareStatement(CustomChecklistConstants.GET_CONTENT_CHANNEL);) {
			ps.setString(1, packetType);
			rs = ps.executeQuery();

			if (null != rs && rs.next()) {
				chetity = new ContentChannel();
				chetity.setContent(rs.getString(CustomChecklistConstants.LS_CONTENT));
				chetity.setChannel(rs.getString(CustomChecklistConstants.LS_CHANNEL));
			}
		} catch (Exception e) {
			logger.debug("Exception in getContentChannel():: {}", e.getMessage());
		}
		return chetity;

	}

	private String getChecklistInspectorFlag(String edition) {
		String inspector = null;
		ResultSet rs = null;
		try (PreparedStatement st = INFORMIX_CONNECTION
				.prepareStatement(CustomChecklistConstants.GET_CHECKLIST_INSPECTOR_CHANNEL);) {
			st.setString(1, edition);
			rs = st.executeQuery();
			if (null != rs && rs.next()) {
				inspector = rs.getString(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getchecklistinspectorchannel():: {}", e.getMessage());
		}
		return inspector;
	}

	private void fetchChecklistDetails(final String ccFilePath, final Integer ccTaskId, final String CAP_DOMAIN,
			BasicChecklistEntity checklist) {
		try {
			// Set UserName for BasicChecklist Class
			checklist.setUserName(CAP_DOMAIN);

			String printSetDetailC = checklist.getPrintSetDetailC();
			String packetType = checklist.getPacketType();
			String editionId = checklist.getEditionId();

			// Get Duplex Value
			String duplexvalue = Optional.ofNullable(getDuplexValue(printSetDetailC))
					.orElseThrow(() -> new Exception("Duplex not fetched"));
			logger.info("Duplex value: {}", duplexvalue);

			// Get Staple Value
			String staplevalue = Optional.ofNullable(getStapleValue(printSetDetailC))
					.orElseThrow(() -> new Exception("Staple not fetched"));
			logger.info("Staple value: {}", staplevalue);

			// Get Media Color
			String mediaColor = Optional.ofNullable(getMediaColor(printSetDetailC))
					.orElseThrow(() -> new Exception("Color not fetched"));
			logger.info("Media color: {}", mediaColor);

			// Get Media Type
			String mediaType = Optional.ofNullable(getMediaType(printSetDetailC))
					.orElseThrow(() -> new Exception("Media not fetched"));
			logger.info("Media Type: {}", mediaType);

			// Get Updated Content & Channel
			ContentChannel contentChannel = Optional.ofNullable(getUpdatedContentChannel(packetType, editionId))
					.orElseThrow(() -> new Exception("ContentChannel not fetched"));
			logger.info("{}", contentChannel);

			// Update Checklist
			if (null != contentChannel) {
				checklist.setOutputOptions(contentChannel.getContent());
				checklist.setChannelData(contentChannel.getChannel());
			}

			// Create PrinterData
			PrinterData printerData = new PrinterData();
			printerData.setDuplex(duplexvalue.equalsIgnoreCase("y"));

			printerData.setStaple(staplevalue.equalsIgnoreCase("y"));
			printerData.setMediaColor(mediaColor);
			printerData.setMediaType(mediaType);
			printerData.setFilePath(ccFilePath, ccTaskId, checklist.getItemSeqNo(), checklist.getAuId(),
					checklist.getSuId(), checklist.getModuleId(), checklist.getEditionId());

			// Set PrinterData for Checklist
			checklist.setPrinterData(printerData);

			// Create Json request
			printJsonRequest(checklist);
		} catch (Exception ex) {
			logger.error("{}", ex.getMessage());
		}
	}

	private void printJsonRequest(BasicChecklistEntity checklist) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(checklist);
			logger.info("{}", jsonString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
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

	public CustomChecklistBatch() {
		// Make DB Connection
		createInformixDbConnection();
	}

	@Override
	public void close() throws Exception {
		// Release Database Connections
		removeConnections();
	}

}