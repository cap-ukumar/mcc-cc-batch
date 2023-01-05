package org.cap.cc.batch.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cap.cc.batch.dao.CustomChecklistConstants;
import org.cap.cc.batch.model.AuditChecklistEntity;
import org.cap.cc.batch.model.ChecklistJobInfo;
import org.cap.cc.batch.model.ChecklistJobInfoRequest;
import org.cap.cc.batch.model.ChecklistRequest;
import org.cap.cc.batch.model.ChecklistResponse;
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

	private Logger logger = LoggerFactory.getLogger(CustomChecklistBatch.class);

	private Connection informixConnection;

	public void processData() {
		try {

			// Get CustomChecklist FilePath
			final String ccFilePath = Optional.ofNullable(getCustomChecklistFilePath())
					.orElseThrow(() -> new Exception("Filepath isn't fetched"));
			logger.info("filePath: {}", ccFilePath);

			// Get Available TaskId
			final Integer ccTaskId = Optional.ofNullable(getAvailableTaskId())
					.orElseThrow(() -> new Exception("TaskId isn't fetched"));
			logger.info("taskId: {}", ccTaskId);

			// Update User_u of ptt_task
			// ...
//			updateUser_u_column(ccTaskId);

			// Get CAP Domain
			final String CAP_DOMAIN = Optional.ofNullable(getCapDomain())
					.orElseThrow(() -> new Exception("CAP Domain isn't fetched"));
			logger.info("CAP-Domain: {}", CAP_DOMAIN);

			// Get Checklist Webservice Url
			final String ccWebServiceUrl = Optional.ofNullable(getCustomChecklistWebServiceUrl())
					.orElseThrow(() -> new Exception("WebService Url isn't fetched"));
			logger.info("WebService-Url: {}", ccWebServiceUrl);

			// Get Job Status Polling Interval
			Integer pollingInterval = Optional.ofNullable(getPollingInterval())
					.orElseThrow(() -> new Exception("Polling Interval isn't fetched"));
			logger.info("pollingInterval: {}", pollingInterval);

			// Get Job Status Polling Iterations
			Integer iterations = Optional.ofNullable(getJobIterations())
					.orElseThrow(() -> new Exception("Iteration are unknown"));
			logger.info("Iterations: {}", iterations);

			// Get Basic Checklist Details
			final List<ChecklistRequest> checklistRequests = Optional.ofNullable(getBasicChecklistDetails(ccTaskId))
					.orElseThrow(() -> new Exception("Checklists are empty for given Taskid: " + ccTaskId));

			/*
			 * Generate Custom Checklists & Get Thunderhead Batchjob Status
			 */
			boolean jobStatus = generateCustomChecklists(ccFilePath, ccTaskId, CAP_DOMAIN, ccWebServiceUrl,
					pollingInterval, iterations, checklistRequests);

			logger.info("Thunderhead BatchJob Status:: {}", jobStatus);
			if (jobStatus) {
				// Prepare and Insert Records in Audit Table
				logger.info("Checklists are generated. Inserting records in Audit Table");

				/*
				 * Save Audit Records
				 */
				saveAuditRecords(checklistRequests);
			} else {
				// Log Error in DB
				logger.error("One or more jobs was not completed in the allocated time");
			}

		} catch (Exception ex) {
			logger.error("{}", ex.getMessage());
		}
	}

	private void saveAuditRecords(List<ChecklistRequest> checklistRequests) {
		for (int i = 0; i < checklistRequests.size(); i++) {
			try {
				String abe_au_u = Optional.ofNullable(checklistRequests.get(i).getAuId()).orElseThrow(()->new Exception("Auid can't be null"));
				String print_us_reg_qst_f = CustomChecklistConstants.US_REG_FLAG;
				String abe_su_u = Optional.ofNullable(checklistRequests.get(i).getSuId()).orElseThrow(()->new Exception("Suid can't be null"));
				String module_key_c = checklistRequests.get(i).getModuleId();
				String chklst_edition_u = checklistRequests.get(i).getEditionId();
				String lap_packet_type_c = checklistRequests.get(i).getPacketType();
				String chklst_type_c = CustomChecklistConstants.CHKLST_TYPE_U;
				Timestamp supl_from_dt = null;
				Integer supl_from_audit_u = null;
				Timestamp chklst_eff_dt = Timestamp.valueOf(LocalDateTime.parse(
						checklistRequests.get(i).getActEffectiveDt(), CustomChecklistConstants.DATE_TIME_FORMATTER));
				Integer seq_no_u = checklistRequests.get(i).getCycleSeqNo();
				Integer tot_qst_cust_ph1_q = checklistRequests.get(i).getChecklistResponse().getChecklistJobInfo()
						.getPhase1Cnt();
				Integer tot_qst_cust_ph2_q = checklistRequests.get(i).getChecklistResponse().getChecklistJobInfo()
						.getPhase2Cnt();
				Integer tot_qst_cust_cri_q = checklistRequests.get(i).getChecklistResponse().getChecklistJobInfo()
						.getCriticalQuestCnt();
				Integer tot_qst_supl_ph1_q = null;
				Integer tot_qst_supl_ph2_q = null;
				Integer tot_qst_supl_cri_q = null;
				// Current Timestamp
				Timestamp currentTimeStamp = Timestamp.valueOf(
						LocalDateTime.parse(LocalDateTime.now().format(CustomChecklistConstants.DATE_TIME_FORMATTER),
								CustomChecklistConstants.DATE_TIME_FORMATTER));
				Timestamp chklst_creation_dt = currentTimeStamp;
				Timestamp last_update_dt = currentTimeStamp;
				String update_user_u = CustomChecklistConstants.UPDATE_USER_U;
				Integer invoking_pgm_c = CustomChecklistConstants.PROGRAM_ID;
				Integer update_pgm_c = CustomChecklistConstants.PROGRAM_ID;

				/*
				 * Create New Audit Pojo to be inserted
				 */

				AuditChecklistEntity auditEntity = new AuditChecklistEntity();
				auditEntity.setAbe_au_u(Integer.parseInt(abe_au_u));
				auditEntity.setAbe_su_u(Integer.parseInt(abe_su_u));
//			insertaudit(null)
//		
			} catch (Exception ex) {
				logger.error("Error Inserting Audit Record for Checklist: {}", checklistRequests.get(i));
				logger.error("Reason: {}", ex.getMessage());
			}
		}
	}

	private boolean generateCustomChecklists(final String ccFilePath, final Integer ccTaskId, final String CAP_DOMAIN,
			final String ccWebServiceUrl, Integer pollingInterval, Integer iterations,
			final List<ChecklistRequest> checklistRequests) {
		boolean jobStatus = false;

		/*
		 * Submit ChecklistRequest Jobs
		 */
		logger.info("\nStart Submit ChecklistRequest Jobs at {}\n", System.currentTimeMillis());
		if (null != checklistRequests)
			for (int i = 0; i < checklistRequests.size(); i++) {
				try {
					ChecklistRequest checklistRequest = checklistRequests.get(i);

					// Fill required details for each checklist
					fetchChecklistDetails(ccFilePath, ccTaskId, CAP_DOMAIN, checklistRequest);

					// Dummy the request
					checklistRequest = dummyRequest();
					checklistRequests.set(i, checklistRequest);

					String request = parsePojoToJsonString(checklistRequest);
					logger.info("\n\t({}) Checklist Job Request::\n \t{}\n", i + 1, request);

					// Submit new ChecklistRequest Job
					ChecklistResponse checklistResponse = submitChecklistJobRequest(ccWebServiceUrl, checklistRequest);
					checklistRequest.setChecklistResponse(checklistResponse);

					String response = parsePojoToJsonString(checklistResponse);
					logger.info("\n\t({}) Checklist Job Response::\n \t{}\n", i + 1, response);
				} catch (Exception e) {
					logger.error("Exception submitting checklistRequest:: {}", e.getMessage());
				}
			}
		logger.info("\nEnd Submit ChecklistRequest Jobs at {}\n", System.currentTimeMillis());

		/*
		 * Prepare ChecklistJobInfoRequests Pojos
		 */
		List<ChecklistJobInfoRequest> checklistJobInfoRequests = checklistRequests.stream().map(request -> {
			ChecklistJobInfo jobInfo = request.getChecklistResponse().getChecklistJobInfo();
			return new ChecklistJobInfoRequest(request.getUserName(), "", jobInfo.isBatchJobCompleted(),
					jobInfo.getBatchJobId(), jobInfo.getBatchJobName(), jobInfo.getBatchJobStatus(),
					jobInfo.isBatchJobSuccessful(), jobInfo.getBatchTransactionsCompleted(),
					jobInfo.getBatchTransactionsCount(), jobInfo.getBatchTransactionsErrored(),
					jobInfo.getBatchTransactionsStopped(), jobInfo.getBatchTransactionsSuccessful(),
					jobInfo.getCriticalQuestCnt(), jobInfo.getFinishTime(), jobInfo.getMessage(),
					jobInfo.getPhase1Cnt(), jobInfo.getPhase2Cnt(), jobInfo.getStartTime());
		}).collect(Collectors.toList());
		if (null != checklistJobInfoRequests && !checklistJobInfoRequests.isEmpty())
			logger.info("{}", checklistJobInfoRequests);

		/*
		 * Get Thunderhead BatchJob Status
		 */
		logger.info("\nStart Get Thunderhead BatchJob Status at {}\n", System.currentTimeMillis());
		try {
			jobStatus = getThunderheadBatchJobStatus(ccWebServiceUrl, pollingInterval, iterations,
					checklistJobInfoRequests);
		} catch (Exception e) {
			logger.info("Exception{}", e.getMessage());
		}
		logger.info("\n\nEnd Get Thunderhead BatchJob Status at {}\n", System.currentTimeMillis());
		return jobStatus;

	}

	private boolean getThunderheadBatchJobStatus(final String ccWebServiceUrl, Integer pollingInterval,
			Integer iterations, List<ChecklistJobInfoRequest> checklistJobInfoRequests) {
		final int size = checklistJobInfoRequests.size();
		boolean[] status = new boolean[size];
		boolean allJobsComplete = true;
		for (int i = 0; i < size; i++) {
			status[i] = false;
		}
		/*
		 * getUpdatedJobInfo
		 */
		for (int i = 0; i < size; i++) {
			int counter = 0;
			try {
				while (counter < iterations) {
					logger.info("({}) Get Updated Job Info for ({})th time. For JobId: ({})", i + 1, counter + 1,
							checklistJobInfoRequests.get(i).getBatchJobId());
					Thread.sleep(pollingInterval);
					status[i] = getUpdatedJobInfo(ccWebServiceUrl, checklistJobInfoRequests.get(i));
					if (status[i]) { // Dummy True
						break;
					}
					counter++;
					// Dummy Exception
//					throw new RuntimeException("");
				}
			} catch (Exception e) {
				status[i] = false;
			}
			allJobsComplete = Boolean.logicalAnd(allJobsComplete, status[i]);
//			logger.error("allJobsComplete: {}",allJobsComplete);
		}
		return allJobsComplete;
	}

	private Boolean getUpdatedJobInfo(String ccWebServiceUrl, ChecklistJobInfoRequest checklistJobInfoRequest) {
		try {
			boolean flag = false;
			HttpPost request = new HttpPost(ccWebServiceUrl + "job" + "?type=info");

			// Add request headers
			request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
			request.addHeader(HttpHeaders.ACCEPT, "*/*");
			request.addHeader(HttpHeaders.ACCEPT_ENCODING, "EncodingUTF8!");
			request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");
			request.addHeader(HttpHeaders.CONNECTION, "keep-alive");
			request.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
			request.addHeader(HttpHeaders.TIMEOUT, "3000");

			// Set json Entity
			request.setEntity(new StringEntity(parsePojoToJsonString(checklistJobInfoRequest)));

			// Execute HttpPost Request
			String response = null;
			response = executeHttpPostRequest(request);

			// Parse Json to Pojo
			ChecklistJobInfo jobInfo = (ChecklistJobInfo) parseJsonStringToPojo(response, ChecklistJobInfo.class);
			logger.info("\t{}", parsePojoToJsonString(jobInfo));

//			 Is Batch Job completed and successful
			if (null != jobInfo && Boolean.logicalAnd(jobInfo.isBatchJobCompleted(), jobInfo.isBatchJobSuccessful()))
				flag = true;
			else
				flag = false;
			return flag;

		} catch (Exception ex) {
			logger.info("Exception in submitChecklistJobRequest():: {}", ex.getMessage());
			return false;
		}
	}

	private void fetchChecklistDetails(final String ccFilePath, final Integer ccTaskId, final String CAP_DOMAIN,
			ChecklistRequest checklist) {
		try {
			// Set UserName for BasicChecklist Class
			checklist.setUserName(CAP_DOMAIN);

			String printSetDetailC = checklist.getPrintSetDetailC();
			String packetType = checklist.getPacketType();
			String editionId = checklist.getEditionId();

			// Get Duplex Value
			String duplexvalue = Optional.ofNullable(getDuplexValue(printSetDetailC))
					.orElseThrow(() -> new Exception("Duplex not fetched"));

			// Get Staple Value
//			String staplevalue = Optional.ofNullable(getStapleValue(printSetDetailC))
//					.orElseThrow(() -> new Exception("Staple not fetched"));
			String staplevalue = CustomChecklistConstants.STAPLE_VALUE;

			// Get Media Color
			String mediaColor = Optional.ofNullable(getMediaColor(printSetDetailC))
					.orElseThrow(() -> new Exception("Color not fetched"));

			// Get Media Type
			String mediaType = Optional.ofNullable(getMediaType(printSetDetailC))
					.orElseThrow(() -> new Exception("Media not fetched"));

			// Get Updated Content & Channel
			ContentChannel contentChannel = Optional.ofNullable(getUpdatedContentChannel(packetType, editionId))
					.orElseThrow(() -> new Exception("ContentChannel not fetched"));

			// Set Checklist
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

			// Set PrinterData
			checklist.setPrinterData(printerData);

		} catch (Exception ex) {
			logger.error("{}", ex.getMessage());
		}
	}

	private ChecklistResponse submitChecklistJobRequest(String ccWebServiceUrl, ChecklistRequest checklistRequest) {
		ChecklistResponse checklistResponse = null;
		try {
			parsePojoToJsonString(checklistRequest);

			HttpPost request = new HttpPost(ccWebServiceUrl + "checklist" + "?type=custom&response=file");

			// Add request headers
			request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
			request.addHeader(HttpHeaders.ACCEPT, "*/*");
			request.addHeader(HttpHeaders.ACCEPT_ENCODING, "EncodingUTF8!");
			request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");
			request.addHeader(HttpHeaders.CONNECTION, "keep-alive");
			request.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
			request.addHeader(HttpHeaders.TIMEOUT, "3000");

			// Set json Entity
			request.setEntity(new StringEntity(parsePojoToJsonString(checklistRequest)));

			// Execute HttpPost Request
			String response = null;
//			response = executeHttpPostRequest(request);

			response = "{\n" + "    \"checklistJobInfo\": {\n" + "        \"batchJobCompleted\": false,\n"
					+ "        \"batchJobId\": 12962,\n" + "        \"batchJobName\": \"BATCH-12962\",\n"
					+ "        \"batchJobStatus\": \"D\",\n" + "        \"batchJobSuccessful\": false,\n"
					+ "        \"batchTransactionsCompleted\": 0,\n" + "        \"batchTransactionsCount\": 0,\n"
					+ "        \"batchTransactionsErrored\": 0,\n" + "        \"batchTransactionsStopped\": 0,\n"
					+ "        \"batchTransactionsSuccessful\": 0,\n" + "        \"criticalQuestCnt\": 3,\n"
					+ "        \"finishTime\": null,\n"
					+ "        \"message\": \"submitBatch: Thunderhead batchJobId: 12962, completion status:D\",\n"
					+ "        \"phase1Cnt\": 6,\n" + "        \"phase2Cnt\": 74,\n"
					+ "        \"startTime\": \"Jan 3, 2023 1:56:36 AM\"\n" + "    },\n"
					+ "    \"checklistCsvInfo\": null,\n" + "    \"chklstPreviewInfo\": null\n" + "}";

			// Parse JsonResponse to Pojo
			checklistResponse = (ChecklistResponse) parseJsonStringToPojo(response, ChecklistResponse.class);

			// Dummy interval
			Thread.sleep(1000);

		} catch (Exception ex) {
			logger.info("Exception in submitChecklistJobRequest():: {}", ex.getMessage());
		}
		return checklistResponse;
	}

	private Integer insertaudit(AuditChecklistEntity etity) {
		Integer audit = null;
		try (PreparedStatement st = getInformixConnection()
				.prepareStatement(CustomChecklistConstants.INSERT_AUDIT_CHECKLIST);) {

			st.setInt(1, etity.getAbe_au_u());
			st.setString(2, etity.getPrint_us_reg_qst_f());
			st.setInt(3, etity.getAbe_su_u());
			st.setString(4, etity.getModule_key_c());
			st.setString(5, etity.getChklst_edition_u());
			st.setString(6, etity.getLap_packet_type_c());
			st.setString(7, etity.getChklst_type_c());
			st.setTimestamp(8, etity.getSupl_from_dt());
			st.setInt(9, etity.getSupl_from_audit_u());
			st.setTimestamp(10, etity.getChklst_eff_dt());
			st.setInt(11, etity.getTot_qst_cust_ph1_q());
			st.setInt(12, etity.getTot_qst_cust_ph2_q());
			st.setInt(13, etity.getTot_qst_cust_cri_q());
			st.setInt(14, etity.getTot_qst_supl_ph1_q());
			st.setInt(15, etity.getTot_qst_supl_ph2_q());
			st.setInt(16, etity.getTot_qst_supl_cri_q());
			st.setTimestamp(17, etity.getChklst_creation_dt());
			st.setTimestamp(18, etity.getLast_update_dt());
			st.setString(19, etity.getUpdate_user_u());
			st.setInt(20, etity.getInvoking_pgm_c());
			st.setInt(21, etity.getUpdate_pgm_c());

			audit = st.executeUpdate();

		} catch (Exception e) {
			logger.debug("Exception in insert audit table(): {}", e.getMessage());
		}
		return audit;
	}

	private String executeHttpPostRequest(HttpPost request) {
		String result = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(request)) {

			// Get HttpResponse Status
			int statusCode = response.getStatusLine().getStatusCode();
//			logger.info("{}", statusCode); // 200

			HttpEntity entity = response.getEntity();
			if (statusCode == 200 && null != entity) {
				result = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			logger.error("Exception in executeHttpPostRequest():: {}", e.getMessage());
		}
		return result;
	}

	private ChecklistRequest dummyRequest() {
		ChecklistRequest dummy = new ChecklistRequest();
		dummy.setUserName("webrw");
		dummy.setEditionId("06042020");
		dummy.setModuleId("COM");
		dummy.setAuId("1186464");
		dummy.setSuId("1319526");
		dummy.setActEffectiveDt("02/09/2021 00:00:00");
		dummy.setOutputOptions("CUSTOMINSR");
		dummy.setChannelData("IPDFFINAL");
		PrinterData printerData = new PrinterData("NA", "", "", false, false);
		dummy.setPrinterData(printerData);
		return dummy;

	}

	private String getCustomChecklistFilePath() {
		String path = null;
		ResultSet rs = null;
		try (Statement st = getInformixConnection().createStatement();) {
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
		try (Statement st = getInformixConnection().createStatement();) {
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
		try (PreparedStatement st = getInformixConnection().prepareStatement(CustomChecklistConstants.UPDATE_USER_U);) {
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
		try (Statement st = getInformixConnection().createStatement();) {
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
		try (Statement st = getInformixConnection().createStatement();) {
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
		try (Statement st = getInformixConnection().createStatement();) {
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
		try (Statement st = getInformixConnection().createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_JOB_COMPLETION_ITERATIONS);
			if (null != rs && rs.next()) {
				iteration = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getJobIterations():: {}", e.getMessage());
		}
		return iteration;
	}

	private List<ChecklistRequest> getBasicChecklistDetails(int taskId) {
		ResultSet rs = null;
		List<ChecklistRequest> list = null;
		try (PreparedStatement ps = getInformixConnection()
				.prepareStatement(CustomChecklistConstants.GET_BASIC_CHECKLIST_DETAILS);) {
			ps.setInt(1, taskId);
			rs = ps.executeQuery();
			if (null != rs)
				list = new ArrayList<>();
			while (null != rs && rs.next()) {
				ChecklistRequest obj = new ChecklistRequest();
				obj.setItemSeqNo(rs.getInt(CustomChecklistConstants.ITEM_SEQ_NO));
				obj.setModuleId(rs.getString(CustomChecklistConstants.MODULE_ID));
				obj.setAuId(rs.getString(CustomChecklistConstants.AU_ID));
				obj.setSuId(rs.getString(CustomChecklistConstants.SU_ID));
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
		try (PreparedStatement st = getInformixConnection()
				.prepareStatement(CustomChecklistConstants.GET_DUPLEX_VALUE);) {
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
		try (PreparedStatement st = getInformixConnection()
				.prepareStatement(CustomChecklistConstants.GET_STAPLE_VALUE);) {
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
		try (PreparedStatement st = getInformixConnection()
				.prepareStatement(CustomChecklistConstants.GET_MEDIA_COLOR);) {
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
		try (PreparedStatement st = getInformixConnection()
				.prepareStatement(CustomChecklistConstants.GET_MEDIA_TYPE);) {
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
			contentChannel = Optional.ofNullable(getContentChannel(packetType))
					.orElseThrow(() -> new Exception("ContentChannel not fetched"));

			// Get Inspector-Channel-Flag & Update ContentChannel
			String chkInsp = Optional.ofNullable(getChecklistInspectorFlag(editionId))
					.orElseThrow(() -> new Exception("Inspector not fetched"));

			if (chkInsp.equalsIgnoreCase(CustomChecklistConstants.CHECKLIST_INSPECTOR_FLAG)) {
				contentChannel.setContent(CustomChecklistConstants.CHECKLIST_INSPECTOR_CONTENT);
				contentChannel.setChannel(CustomChecklistConstants.CHECKLIST_INSPECTOR_CHANNEL);
			}

		} catch (Exception e) {
			logger.error("Exception in getUpdatedContentChannel():: {}", e.getMessage());
		}
		return contentChannel;
	}

	private ContentChannel getContentChannel(String packetType) {
		ResultSet rs = null;
		ContentChannel chetity = null;
		try (PreparedStatement ps = getInformixConnection()
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
		try (PreparedStatement st = getInformixConnection()
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

	private String parsePojoToJsonString(Object object) {
		String jsonString = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
//			jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			jsonString = mapper.writeValueAsString(object);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	private Object parseJsonStringToPojo(String string, Class<?> class1) {
		Object object = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			object = mapper.readValue(string, class1);
		} catch (Exception ex) {
			logger.info("Exception in parseJsonStringToPojo():: {}", ex.getMessage());
		}
		return object;
	}

	public static void getRestTemplate() {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();

		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		restTemplate.setMessageConverters(messageConverters);
	}

	public void createInformixDbConnection() {
		try {
			this.informixConnection = DriverManager.getConnection(
					CommonUtils.getProperty(CapConfigConstants.INFORMIX_URL),
					CommonUtils.getProperty(CapConfigConstants.INFORMIX_USERNAME),
					CommonUtils.getProperty(CapConfigConstants.INFORMIX_PASSWORD));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getInformixConnection() {
		return this.informixConnection;
	}

	private void removeConnections() {

		try {
			if (null != getInformixConnection())
				this.informixConnection.close();
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