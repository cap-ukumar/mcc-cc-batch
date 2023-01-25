package org.cap.cc.batch.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import org.cap.cc.batch.dao.CustomChecklistConstants;
import org.cap.cc.batch.dao.CustomLoggingEvents;
import org.cap.cc.batch.utils.CapConfigConstants;
import org.cap.cc.batch.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomChecklistBatchRepository implements AutoCloseable {
	
	private Logger logger;
	
	private Connection informixConnection;

	private Connection postgresConnection;	
	
	public CustomChecklistBatchRepository() {
		// Create Database Connections
		createInformixDbConnection();
		createPostgresDbConnection();
		logger = LoggerFactory.getLogger(Thread.currentThread().getName());
	}

	private void createInformixDbConnection() {
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

	private void createPostgresDbConnection() {
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

	private void removeConnections() {

		try {
			if (null != getInformixConnection())
				this.informixConnection.close();
			if (null != getPostgresConnection())
				this.postgresConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws Exception {
		// Release Database Connections
		removeConnections();
	}
	
	public synchronized int getAvailableTaskId() {
		int taskId = 0;
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
	
	public synchronized int updateUserForTaskId(int taskId) {
		int result = -1;
		String specialInstrT = CommonUtils.getUUID();
		try (PreparedStatement st = getInformixConnection().prepareStatement(CustomChecklistConstants.UPDATE_USER_U);) {
			st.setString(1, specialInstrT);
			st.setInt(2, CustomChecklistConstants.PROGRAM_ID);
			st.setInt(3, taskId);
			result = st.executeUpdate();
		} catch (Exception e) {
			logger.error("Error in updateUserForTaskId():: {}", e.getMessage());
		}
		return result;
	}
	

	/**
	 * 
	 * @param event
	 * @param taskId
	 * @param strings...
	 * 
	 * @category
	 *           <h1>BATCH_STARTED:</h1>
	 *           logEventInMccDB(CustomLoggingEvents.BATCH_STARTED, ccTaskId); <br>
	 *           </br>
	 *           <h1>STARTED_PROCESSING_TASK:</h1>
	 *           logEventInMccDB(CustomLoggingEvents.STARTED_PROCESSING_TASK,
	 *           ccTaskId); <br>
	 *           </br>
	 *           <h1>SUBMIT_CHECKLIST:</h1>
	 *           logEventInMccDB(CustomLoggingEvents.SUBMIT_CHECKLIST, taskId,
	 *           obj.getModuleId(), obj.getEditionId(), obj.getAuId(),
	 *           obj.getSuId()); <br>
	 *           </br>
	 *           <h1>STARTED_CHECKING_THUNDERHEAD_JOB_STATUS:</h1>
	 *           logEventInMccDB(CustomLoggingEvents.STARTED_CHECKING_THUNDERHEAD_JOB_STATUS,
	 *           ccTaskId); <br>
	 *           </br>
	 *           <h1>FINISHED_CHECKING_THUNDERHEAD_JOB_STATUS:</h1>
	 *           logEventInMccDB(CustomLoggingEvents.FINISHED_CHECKING_THUNDERHEAD_JOB_STATUS,
	 *           ccTaskId, logJobStatus); <br>
	 *           </br>
	 *           <h1>BATCH_FINISHED:</h1>
	 *           logEventInMccDB(CustomLoggingEvents.BATCH_FINISHED, ccTaskId,
	 *           String.valueOf(totalTasks), String.valueOf(totalTasks -
	 *           processedTasks)); <br>
	 *           </br>
	 *           <h1>BATCH_ERROR</h1>
	 *           logEventInMccDB(CustomLoggingEvents.BATCH_ERROR,
	 *           ccTaskId,"getThunderheadBatchJobStatus()", e.getMessage()); <br>
	 *           </br>
	 *           <h1>BATCH_EXCEPTION
	 *           logEventInMccDB(CustomLoggingEvents.BATCH_EXCEPTION, ccTaskId,
	 *           "getThunderheadBatchJobStatus()", e.getMessage());
	 *
	 *
	 */
	public void logEventInMccDB(CustomLoggingEvents event, int taskId, String... strings) {
		try {
			String timeInstant = LocalDateTime.now().format(CustomChecklistConstants.DATE_TIME_FORMATTER);
			switch (event) {
			case BATCH_STARTED:
				logger.info(CustomChecklistConstants.LOG_DIVIDER);
				logger.info("{}{}", timeInstant, CustomChecklistConstants.LOG_CC_BATCH_STARTED);
				logger.info(CustomChecklistConstants.LOG_DIVIDER);

				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						CustomChecklistConstants.LOG_DIVIDER);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						timeInstant + CustomChecklistConstants.LOG_CC_BATCH_STARTED);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						CustomChecklistConstants.LOG_DIVIDER);
				break;
			case BATCH_FINISHED:
				logger.info(CustomChecklistConstants.LOG_DIVIDER);
				logger.info("{}{}", timeInstant, CustomChecklistConstants.LOG_CC_BATCH_FINISHED);
				logger.info(CustomChecklistConstants.LOG_DIVIDER);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						timeInstant + CustomChecklistConstants.LOG_TOTAL_PROCESSED_TASKS + strings[0]);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						timeInstant + CustomChecklistConstants.LOG_TOTAL_FAILED_TASKS + strings[1]);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						CustomChecklistConstants.LOG_DIVIDER);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						timeInstant + CustomChecklistConstants.LOG_CC_BATCH_FINISHED);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						CustomChecklistConstants.LOG_DIVIDER);
				break;
			case STARTED_PROCESSING_TASK:
				String message = String.format(CustomChecklistConstants.LOG_STARTED_PROCESSING_TASK, taskId);
				logger.info("{}{}", timeInstant, message);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL, timeInstant + message);
				break;
			case TASK_EXCEPTION:
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL, timeInstant + strings[0]+taskId);
				break;
			case SUBMIT_CHECKLIST:
				String checklist = String.format(CustomChecklistConstants.LOG_CHECKLIST_DETAILS, strings[0], strings[1],
						strings[2], strings[3]);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL,
						timeInstant + checklist);
				break;
			case STARTED_CHECKING_THUNDERHEAD_JOB_STATUS:
				String message1 = String.format(CustomChecklistConstants.LOG_STARTED_CHECKING_THUNDERHEAD_JOB_STATUS,
						taskId);
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL, timeInstant + message1);
				break;
			case FINISHED_CHECKING_THUNDERHEAD_JOB_STATUS:
				String status = String.format(CustomChecklistConstants.LOG_FINISHED_CHECKING_THUNDERHEAD_JOB_STATUS,
						strings[0], taskId);
				String msgType = strings[0].equals("SUCCESSFUL") ? CustomChecklistConstants.LOG_MSG_TYPE_INFORMATIONAL
						: CustomChecklistConstants.LOG_MSG_TYPE_WARNING;
				insertChecklistLog(taskId, msgType, timeInstant + status);
				break;
			case BATCH_ERROR:
				StringBuilder builder = new StringBuilder();
				builder.append(String.format(CustomChecklistConstants.LOG_ERROR_DATE_TIME, timeInstant));
				builder.append("\n");
				builder.append(String.format(CustomChecklistConstants.LOG_ERROR_INSTANCE_NAME, this.getClass()));
				builder.append("\n");
				builder.append(String.format(CustomChecklistConstants.LOG_ERROR_CLASS_NAME, this.getClass()));
				builder.append("\n");
				builder.append(String.format(CustomChecklistConstants.LOG_ERROR_FUNCTION_NAME, strings[0]));
				builder.append("\n");
				builder.append(String.format(CustomChecklistConstants.LOG_ERROR_CODE, -1));
				builder.append("\n");
				builder.append(String.format(CustomChecklistConstants.LOG_ERROR_TEXT, strings[1]));
				builder.append("\n");
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_FAILED, builder.toString());
				break;
			case BATCH_EXCEPTION:
				insertChecklistLog(taskId, CustomChecklistConstants.LOG_MSG_TYPE_WARNING,
						timeInstant + " " + strings[0] + "\n" + strings[1]);
				break;
			default:
				logger.info("");
			}
		} catch (Exception ex) {
			logger.error("Error in logEventInMccDB():: {}", ex.getMessage());

		}
	}
	
	public Integer insertChecklistLog(int taskId, String messageType, String message) {
		Integer chklst = null;
		try (PreparedStatement st = getPostgresConnection()
				.prepareStatement(CustomChecklistConstants.INSERT_LOG_MCC_DB);) {

			st.setInt(1, taskId);
			st.setString(2, messageType);
			st.setString(3, message);
			st.setString(4, CustomChecklistConstants.UPDATE_USER_U_VALUE);
			st.setString(5, CustomChecklistConstants.UPDATE_USER_U_VALUE);
			st.setInt(6, CustomChecklistConstants.PROGRAM_ID);
			st.setInt(7, CustomChecklistConstants.PROGRAM_ID);
			st.setString(8, CustomChecklistConstants.SOURCE);

			chklst = st.executeUpdate();

		} catch (Exception e) {
			logger.debug("Exception in insert chklst log table(): {}", e.getMessage());
		}
		return chklst;
	}


}
