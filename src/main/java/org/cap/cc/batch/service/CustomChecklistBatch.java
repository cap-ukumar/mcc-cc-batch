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
import org.cap.cc.batch.model.ChecklistEntity;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomChecklistBatch implements AutoCloseable {

	private Logger logger = LoggerFactory.getLogger(CustomChecklistBatch.class);

	private Connection informixConnection;

	private Connection postgresConnection;

	public void processData() {

		// Get Available TaskId
		Integer ccTaskId = null;
		while (null != getAvailableTaskId()) {
			ccTaskId = getAvailableTaskId();
			logger.info("taskId: {}", ccTaskId);
			processData(ccTaskId);
		}
		/*
		 * Outside while
		 */
		logger.info("No TaskId returned");

	}

	public void processData(int ccTaskId) {
		try {

			// Get CustomChecklist FilePath
			final String ccFilePath = Optional.ofNullable(getCustomChecklistFilePath())
					.orElseThrow(() -> new Exception("Filepath isn't fetched"));
			logger.info("filePath: {}", ccFilePath);

			// Interrupt
			System.exit(0);
			/*
			 * Update User_u of ptt_task
			 */
			int updateRow = updateUser_u_column(ccTaskId);
			logger.info("Updating ptt_task Table for TaskId: {} and Status: {}", ccTaskId, updateRow);

			if (updateRow > 0) {
				// Get CAP Domain
				final String CAP_DOMAIN = Optional.ofNullable(getCapDomain())
						.orElseThrow(() -> new Exception("CAP Domain isn't fetched")) + "\\pullere";
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
//					saveAuditRecords(checklistRequests);
				} else {
					// Log Error in DB
					logger.error("One or more jobs was not completed in the allocated time");
				}
			} else {
				logger.error("Unable to update ptt_task:: {}", updateRow);
			}

		} catch (Exception ex) {
			logger.error("{}", ex.getMessage());
		}
	}

//	public void saveAuditRecords(List<ChecklistRequest> checklistRequests) {
//
//		/*
//		 * Insert Logic
//		 */
//		for (int i = 0; i < checklistRequests.size(); i++) {
//			try {
//				String abId = Optional.ofNullable(checklistRequests.get(i).getAuId())
//						.orElseThrow(() -> new Exception("Auid can't be null"));
//				String uSRegFlag = CustomChecklistConstants.US_REG_FLAG;
//				String suId = Optional.ofNullable(checklistRequests.get(i).getSuId())
//						.orElseThrow(() -> new Exception("Suid can't be null"));
//				String moduleId = checklistRequests.get(i).getModuleId();
//				String editionId = checklistRequests.get(i).getEditionId();
//				String lapPacketType = checklistRequests.get(i).getPacketType();
//				String chklstType = CustomChecklistConstants.CHKLST_TYPE_U;
//				Timestamp suplFromDate = null;
//				Integer suplFromAuditU = null;
//				Timestamp chklstEffDt = Timestamp.valueOf(LocalDateTime.parse(
//						checklistRequests.get(i).getActEffectiveDt(), CustomChecklistConstants.DATE_TIME_FORMATTER));
//				Integer seqNo = checklistRequests.get(i).getCycleSeqNo();
//				Integer totQstPh1Q = checklistRequests.get(i).getChecklistResponse().getChecklistJobInfo()
//						.getPhase1Cnt();
//				Integer totQstPh2Q = checklistRequests.get(i).getChecklistResponse().getChecklistJobInfo()
//						.getPhase2Cnt();
//				Integer totQstCriQ = checklistRequests.get(i).getChecklistResponse().getChecklistJobInfo()
//						.getCriticalQuestCnt();
//				Integer totQstSuplPh1Q = null;
//				Integer totQstSuplPh2Q = null;
//				Integer totQstSuplCriQ = null;
//				// Current Timestamp
//				Timestamp currentTimeStamp = Timestamp.valueOf(
//						LocalDateTime.parse(LocalDateTime.now().format(CustomChecklistConstants.DATE_TIME_FORMATTER),
//								CustomChecklistConstants.DATE_TIME_FORMATTER));
//				Timestamp chklstCreationDt = currentTimeStamp;
//				Timestamp lastUpdateDt = currentTimeStamp;
//				String updateUserU = CustomChecklistConstants.UPDATE_USER_U_VALUE;
//				Integer invokingPgmC = CustomChecklistConstants.PROGRAM_ID;
//				Integer updatePgmC = CustomChecklistConstants.PROGRAM_ID;
//
//				/*
//				 * Create New Audit Pojo to be inserted
//				 */
//
//				AuditChecklistEntity auditEntity = new AuditChecklistEntity();
//				auditEntity.setAbe_au_u(Integer.parseInt(abId));
//				auditEntity.setAbe_su_u(Integer.parseInt(suId));
//				auditEntity.setPrint_us_reg_qst_f(uSRegFlag);
//				auditEntity.setModule_key_c(moduleId);
//				auditEntity.setChklst_edition_u(editionId);
//				auditEntity.setLap_packet_type_c(lapPacketType);
//				auditEntity.setChklst_type_c(chklstType);
//				auditEntity.setSupl_from_dt(suplFromDate);
//				auditEntity.setSupl_from_audit_u(suplFromAuditU);
//				auditEntity.setChklst_eff_dt(chklstEffDt);
//				auditEntity.setSeq_no_u(seqNo);
//				auditEntity.setTot_qst_cust_ph1_q(totQstPh1Q);
//				auditEntity.setTot_qst_cust_ph2_q(totQstPh2Q);
//				auditEntity.setTot_qst_cust_cri_q(totQstCriQ);
//				auditEntity.setTot_qst_supl_cri_q(totQstSuplCriQ);
//				auditEntity.setTot_qst_supl_ph1_q(totQstSuplPh1Q);
//				auditEntity.setTot_qst_supl_ph2_q(totQstSuplPh2Q);
//				auditEntity.setChklst_creation_dt(chklstCreationDt);
//				auditEntity.setLast_update_dt(lastUpdateDt);
//				auditEntity.setUpdate_user_u(updateUserU);
//				auditEntity.setInvoking_pgm_c(invokingPgmC);
//				auditEntity.setUpdate_pgm_c(updatePgmC);
//
//				logger.info("\nInserting Audit Record\n({})\t{}\n", i, parsePojoToJsonString(auditEntity));
//				/*
//				 * Inserting Audit Record
//				 */
////				insertAuditRecord(auditEntity);
//
//			} catch (Exception ex) {
//				logger.error("Error Inserting Audit Record for Checklist: {}", checklistRequests.get(i));
//				logger.error("Reason: {}", ex.getMessage());
//			}
//		}
//	}

	public boolean generateCustomChecklists(final String ccFilePath, final Integer ccTaskId, final String CAP_DOMAIN,
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

					String request = parsePojoToJsonString(checklistRequest);
					logger.info("\n\t({}) Checklist Job Request::\n \t{}\n", i + 1, request);

					// Submit new ChecklistRequest Job
					ChecklistResponse checklistResponse = submitChecklistJobRequest(ccWebServiceUrl, checklistRequest);

					// Set checklistResponse
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
					checklistJobInfoRequests, checklistRequests);
		} catch (Exception e) {
			logger.info("Exception{}", e.getMessage());
		}
		logger.info("\n\nEnd Get Thunderhead BatchJob Status at {}\n", System.currentTimeMillis());
		return jobStatus;

	}

	public boolean getThunderheadBatchJobStatus(final String ccWebServiceUrl, Integer pollingInterval,
			Integer iterations, List<ChecklistJobInfoRequest> checklistJobInfoRequests,
			List<ChecklistRequest> checklistRequests) {
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
					logger.info("Waiting before getting Job Status: {}", pollingInterval * 1000);
					Thread.sleep(pollingInterval * 1000);
					status[i] = getUpdatedJobInfo(ccWebServiceUrl, checklistJobInfoRequests.get(i));
					if (status[i]) {
						break;
					}
					counter++;
				}
			} catch (Exception e) {
				status[i] = false;
				// log exception in db
			}

			/*
			 * Insert Audit Record if Success orElse log error
			 */
			if (status[i]) {
				logger.info("Inserting a new Audit record for Batch Job: {}",
						checklistJobInfoRequests.get(i).getBatchJobId());
				saveAuditRecord(checklistRequests.get(i));
			} else {
				logger.info("s");
			}
			allJobsComplete = Boolean.logicalAnd(allJobsComplete, status[i]);
		}
		return allJobsComplete;
	}

	private void saveAuditRecord(ChecklistRequest checklistRequest) {

		/*
		 * Insert Logic
		 */
		try {
			String abId = Optional.ofNullable(checklistRequest.getAuId())
					.orElseThrow(() -> new Exception("Auid can't be null"));
			String uSRegFlag = CustomChecklistConstants.US_REG_FLAG;
			String suId = Optional.ofNullable(checklistRequest.getSuId())
					.orElseThrow(() -> new Exception("Suid can't be null"));
			String moduleId = checklistRequest.getModuleId();
			String editionId = checklistRequest.getEditionId();
			String lapPacketType = checklistRequest.getPacketType();
			String chklstType = CustomChecklistConstants.CHKLST_TYPE_U;
			Timestamp suplFromDate = null;
			Integer suplFromAuditU = null;
			Timestamp chklstEffDt = Timestamp.valueOf(LocalDateTime.parse(checklistRequest.getActEffectiveDt(),
					CustomChecklistConstants.DATE_TIME_FORMATTER));
			Integer seqNo = checklistRequest.getCycleSeqNo();
			Integer totQstPh1Q = checklistRequest.getChecklistResponse().getChecklistJobInfo().getPhase1Cnt();
			Integer totQstPh2Q = checklistRequest.getChecklistResponse().getChecklistJobInfo().getPhase2Cnt();
			Integer totQstCriQ = checklistRequest.getChecklistResponse().getChecklistJobInfo().getCriticalQuestCnt();
			Integer totQstSuplPh1Q = null;
			Integer totQstSuplPh2Q = null;
			Integer totQstSuplCriQ = null;
			// Current Timestamp
			Timestamp currentTimeStamp = Timestamp.valueOf(
					LocalDateTime.parse(LocalDateTime.now().format(CustomChecklistConstants.DATE_TIME_FORMATTER),
							CustomChecklistConstants.DATE_TIME_FORMATTER));
			Timestamp chklstCreationDt = currentTimeStamp;
			Timestamp lastUpdateDt = currentTimeStamp;
			String updateUserU = CustomChecklistConstants.UPDATE_USER_U_VALUE;
			Integer invokingPgmC = CustomChecklistConstants.PROGRAM_ID;
			Integer updatePgmC = CustomChecklistConstants.PROGRAM_ID;

			/*
			 * Create New Audit Pojo to be inserted
			 */

			AuditChecklistEntity auditEntity = new AuditChecklistEntity();
			auditEntity.setAbe_au_u(Integer.parseInt(abId));
			auditEntity.setAbe_su_u(Integer.parseInt(suId));
			auditEntity.setPrint_us_reg_qst_f(uSRegFlag);
			auditEntity.setModule_key_c(moduleId);
			auditEntity.setChklst_edition_u(editionId);
			auditEntity.setLap_packet_type_c(lapPacketType);
			auditEntity.setChklst_type_c(chklstType);
			auditEntity.setSupl_from_dt(suplFromDate);
			auditEntity.setSupl_from_audit_u(suplFromAuditU);
			auditEntity.setChklst_eff_dt(chklstEffDt);
			auditEntity.setSeq_no_u(seqNo);
			auditEntity.setTot_qst_cust_ph1_q(totQstPh1Q);
			auditEntity.setTot_qst_cust_ph2_q(totQstPh2Q);
			auditEntity.setTot_qst_cust_cri_q(totQstCriQ);
			auditEntity.setTot_qst_supl_cri_q(totQstSuplCriQ);
			auditEntity.setTot_qst_supl_ph1_q(totQstSuplPh1Q);
			auditEntity.setTot_qst_supl_ph2_q(totQstSuplPh2Q);
			auditEntity.setChklst_creation_dt(chklstCreationDt);
			auditEntity.setLast_update_dt(lastUpdateDt);
			auditEntity.setUpdate_user_u(updateUserU);
			auditEntity.setInvoking_pgm_c(invokingPgmC);
			auditEntity.setUpdate_pgm_c(updatePgmC);

			logger.info("\nInserting Audit Record\n({})\t{}\n", parsePojoToJsonString(auditEntity));
			/*
			 * Inserting Audit Record
			 */
			insertAuditRecord(auditEntity);

		} catch (Exception ex) {
			logger.error("Error Inserting Audit Record for Checklist: {}", checklistRequest);
			logger.error("Reason: {}", ex.getMessage());
		}

	}

	public Boolean getUpdatedJobInfo(String ccWebServiceUrl, ChecklistJobInfoRequest checklistJobInfoRequest) {
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

	public void fetchChecklistDetails(final String ccFilePath, final Integer ccTaskId, final String CAP_DOMAIN,
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
//				checklist.setChannelData(contentChannel.getChannel());
				checklist.setChannelData(CustomChecklistConstants.CHANNEL_DATA);
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

	public ChecklistResponse submitChecklistJobRequest(String ccWebServiceUrl, ChecklistRequest checklistRequest) {
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
			String response = executeHttpPostRequest(request);
			logger.info("\nJson Response: \n{}\n", response);

			// Parse JsonResponse to Pojo
			checklistResponse = (ChecklistResponse) parseJsonStringToPojo(response, ChecklistResponse.class);

			/*
			 * Dummy interval
			 */
//			Thread.sleep(1000);

		} catch (Exception ex) {
			logger.info("Exception in submitChecklistJobRequest():: {}", ex.getMessage());
		}
		return checklistResponse;
	}

	public Integer insertAuditRecord(AuditChecklistEntity etity) {
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
			st.setInt(9, null != etity.getSupl_from_audit_u() ? etity.getSupl_from_audit_u() : 0);
			st.setTimestamp(10, etity.getChklst_eff_dt());
			// Seqno
			st.setInt(11, null != etity.getSeq_no_u() ? etity.getSeq_no_u() : 0);
			st.setInt(12, null != etity.getTot_qst_cust_ph1_q() ? etity.getTot_qst_cust_ph1_q() : 0);
			st.setInt(13, null != etity.getTot_qst_cust_ph2_q() ? etity.getTot_qst_cust_ph2_q() : 0);
			st.setInt(14, null != etity.getTot_qst_cust_cri_q() ? etity.getTot_qst_cust_cri_q() : 0);
			st.setInt(15, null != etity.getTot_qst_supl_ph1_q() ? etity.getTot_qst_supl_ph1_q() : 0);
			st.setInt(16, null != etity.getTot_qst_supl_ph2_q() ? etity.getTot_qst_supl_ph2_q() : 0);
			st.setInt(17, null != etity.getTot_qst_supl_cri_q() ? etity.getTot_qst_supl_cri_q() : 0);
			st.setTimestamp(18, etity.getChklst_creation_dt());
			st.setTimestamp(19, etity.getLast_update_dt());
			st.setString(20, etity.getUpdate_user_u());
			st.setInt(21, null != etity.getInvoking_pgm_c() ? etity.getInvoking_pgm_c() : 0);
			st.setInt(22, null != etity.getUpdate_pgm_c() ? etity.getUpdate_pgm_c() : 0);

			audit = st.executeUpdate();

		} catch (Exception e) {
			logger.debug("Exception in insert audit table(): {}", e.getMessage());
		}
		return audit;
	}

	public String executeHttpPostRequest(HttpPost request) {
		String result = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(request)) {

			// Get HttpResponse Status
			int statusCode = response.getStatusLine().getStatusCode();
//			logger.info("{}", statusCode); // 200

			HttpEntity entity = response.getEntity();
			if (statusCode == 200 && null != entity) {
			result = EntityUtils.toString(entity);
			}else {
				throw new Exception("Post request is not successful: \n"+EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			logger.error("Exception in executeHttpPostRequest():: {}", e.getMessage());
		}
		return result;
	}

	public ChecklistRequest dummyRequest() {
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

	public String getCustomChecklistFilePath() {
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

	public Integer getAvailableTaskId() {
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

	public int updateUser_u_column(int taskId) {
		int result = -1;
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

	public String getCapDomain() {
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

	public String getCustomChecklistWebServiceUrl() {
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

	public Integer getPollingInterval() {
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

	public Integer getJobIterations() {
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

	public List<ChecklistRequest> getBasicChecklistDetails(int taskId) {
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

	public String getDuplexValue(String printSetDetailC) {
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

	public String getStapleValue(String printSetDetailC) {
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

	public String getMediaColor(String printSetDetailC) {
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

	public String getMediaType(String printSetDetailC) {
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

	public ContentChannel getUpdatedContentChannel(String packetType, String editionId) {
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

	public ContentChannel getContentChannel(String packetType) {
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

	public String getChecklistInspectorFlag(String edition) {
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

	public List<AuditChecklistEntity> getAuditRecordsOfPacket(int auId, int suId) {
		List<AuditChecklistEntity> auditList = new ArrayList<>();
		ResultSet rs = null;
		try (PreparedStatement st = getInformixConnection()
				.prepareStatement(CustomChecklistConstants.GET_AUDIT_RECORDS_OF_PACKET);) {
			st.setInt(1, auId);
			st.setInt(2, suId);
			rs = st.executeQuery();
			while (null != rs && rs.next()) {
				AuditChecklistEntity auditRecord = new AuditChecklistEntity();
				auditRecord.setChklst_audit_u(rs.getInt("chklst_audit_u"));
				auditRecord.setAbe_au_u(rs.getInt("abe_au_u"));
				auditRecord.setAbe_su_u(rs.getInt("abe_su_u"));
				auditRecord.setPrint_us_reg_qst_f(rs.getString("print_us_reg_qst_f"));
				auditRecord.setModule_key_c(rs.getString("module_key_c"));
				auditRecord.setChklst_edition_u(rs.getString("chklst_edition_u"));
				auditRecord.setLap_packet_type_c(rs.getString("lap_packet_type_c"));
				auditRecord.setChklst_type_c(rs.getString("chklst_type_c"));
				auditRecord.setSupl_from_dt(rs.getTimestamp("supl_from_dt"));
				auditRecord.setSupl_from_audit_u(rs.getInt("supl_from_audit_u"));
				auditRecord.setChklst_eff_dt(rs.getTimestamp("chklst_eff_dt"));
				auditRecord.setSeq_no_u(rs.getInt("seq_no_u"));
				auditRecord.setTot_qst_cust_ph1_q(rs.getInt("tot_qst_cust_ph1_q"));
				auditRecord.setTot_qst_cust_ph2_q(rs.getInt("tot_qst_cust_ph2_q"));
				auditRecord.setTot_qst_cust_cri_q(rs.getInt("tot_qst_cust_cri_q"));
				auditRecord.setTot_qst_supl_cri_q(rs.getInt("tot_qst_supl_cri_q"));
				auditRecord.setTot_qst_supl_ph1_q(rs.getInt("tot_qst_supl_ph1_q"));
				auditRecord.setTot_qst_supl_ph2_q(rs.getInt("tot_qst_supl_ph2_q"));
				auditRecord.setChklst_creation_dt(rs.getTimestamp("chklst_creation_dt"));
				auditRecord.setLast_update_dt(rs.getTimestamp("last_update_dt"));
				auditRecord.setUpdate_user_u(rs.getString("update_user_u"));
				auditRecord.setInvoking_pgm_c(rs.getInt("invoking_pgm_c"));
				auditRecord.setUpdate_pgm_c(rs.getInt("update_pgm_c"));

				auditList.add(auditRecord);
			}
		} catch (Exception e) {
			logger.debug("Exception in getchecklistinspectorchannel():: {}", e.getMessage());
		}
		return auditList;
	}

	public String parsePojoToJsonString(Object object) {
		String jsonString = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
//			jsonString = mapper.writeValueAsString(object);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	public Object parseJsonStringToPojo(String string, Class<?> class1) {
		Object object = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			/*
			 * Unrecognized Property Exception
			 */
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			object = mapper.readValue(string, class1);
		} catch (Exception ex) {
			logger.info("Exception in parseJsonStringToPojo():: {}", ex.getMessage());
		}
		return object;
	}

	public Integer insertchklst(ChecklistEntity checklistEntity) {
		Integer chklst = null;
		try (PreparedStatement st = getPostgresConnection()
				.prepareStatement(CustomChecklistConstants.INSERT_LOG_MCC_DB);) {

			st.setInt(1, checklistEntity.getChklst_log_u());
			st.setInt(2, checklistEntity.getTask_u());
			st.setString(3, checklistEntity.getChk_msg_type_c());
			st.setString(4, checklistEntity.getChk_msg_t());
			st.setTimestamp(5, checklistEntity.getCreated_dt());
			st.setString(6, checklistEntity.getCreated_user());
			st.setTimestamp(7, checklistEntity.getLastupdate_dt());
			st.setString(8, checklistEntity.getLastupdate_user());
			st.setInt(9, checklistEntity.getCreated_pgm_c());
			st.setInt(10, checklistEntity.getUpdated_pgm_c());
			st.setString(11, checklistEntity.getRecord_source());

			chklst = st.executeUpdate();

		} catch (Exception e) {
			logger.debug("Exception in insert chklst log table(): {}", e.getMessage());
		}
		return chklst;
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

	public void createPostgresDbConnection() {
		try {
			this.postgresConnection = DriverManager.getConnection(
					CommonUtils.getProperty(CapConfigConstants.POSTGRES_URL),
					CommonUtils.getProperty(CapConfigConstants.POSTGRES_USERNAME),
					CommonUtils.getProperty(CapConfigConstants.POSTGRES_PASSWORD));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getPostgresConnection() {
		return this.postgresConnection;
	}

	public void removeConnections() {

		try {
			if (null != getInformixConnection())
				this.informixConnection.close();
			if (null != getPostgresConnection())
				this.postgresConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public CustomChecklistBatch() {
		// Create Database Connections
		createInformixDbConnection();
		createPostgresDbConnection();

	}

	@Override
	public void close() throws Exception {
		// Release Database Connections
		removeConnections();
	}

}