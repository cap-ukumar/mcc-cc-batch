package org.cap.cc.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.cap.cc.batch.dao.CustomChecklistConstants;
import org.cap.cc.batch.service.CustomChecklistBatch;
import org.cap.cc.batch.utils.CapConfigConstants;
import org.cap.cc.batch.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MccCCBatchService {

	private static Logger logger = LoggerFactory.getLogger(MccCCBatchService.class);

	private static Semaphore semaphore = new Semaphore(1);

	private static List<Integer> list = new ArrayList<>();

	private static Connection connection;

	static {
		try {
			connection = DriverManager.getConnection(CommonUtils.getProperty(CapConfigConstants.INFORMIX_URL),
					CommonUtils.getProperty(CapConfigConstants.INFORMIX_USERNAME),
					CommonUtils.getProperty(CapConfigConstants.INFORMIX_PASSWORD));
			list.add(4);
			list.add(3);
			list.add(2);
			list.add(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Integer getAvailableTaskId() {
		Integer taskId = null;
		ResultSet rs = null;
		try (Statement st = connection.createStatement();) {
			rs = st.executeQuery(CustomChecklistConstants.GET_TASK_ID);
			if (null != rs && rs.next()) {
				taskId = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.debug("Exception in getAvailableTaskId():: {}", e.getMessage());
		}
		return taskId;
	}

	public static int updateUserForTaskId(int taskId) {
		int result = -1;
		String specialInstrT = CommonUtils.getUUID();
		try (PreparedStatement st = connection.prepareStatement(CustomChecklistConstants.UPDATE_USER_U);) {
			st.setString(1, specialInstrT);
			st.setInt(2, CustomChecklistConstants.PROGRAM_ID);
			st.setInt(3, taskId);
			result = st.executeUpdate();
		} catch (Exception e) {
			logger.error("Error in updateUserForTaskId():: {}", e.getMessage());
		}
		return result;
	}

	public static int fetchTask() {
		int task = 0;
		try {
			semaphore.acquire();
			task=getAvailableTaskId();
//			task = list.remove(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return task;
	}

	public static void main(String[] args) {
		Thread t1 = new Thread(new Runner(), "Task-1");
		Thread t2 = new Thread(new Runner(), "Task-2");
		Thread t3 = new Thread(new Runner(), "Task-3");
		Thread t4 = new Thread(new Runner(), "Task-4");

//		ExecutorService service = Executors.newFixedThreadPool(4);
//		for(int i=0; i<5; i++) {
//			Thread t = new Thread(new Runner(), "Task-"+i+1);
//			service.submit(t);
//		}

		t1.start();
		t2.start();
		t3.start();
		t4.start();

		try {
			t1.join();
			t2.join();
			t3.join();
			t4.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

	}

	public static void releaseLock() {
		semaphore.release();
	}

}

class Runner implements Runnable {
	private Logger logger = LoggerFactory.getLogger(Thread.currentThread().getName());

	@Override
	public void run() {
		try {
			Thread.sleep(100);
			int taskId = MccCCBatchService.fetchTask();
			if (taskId != 0 && taskId > 0) {
				int update = MccCCBatchService.updateUserForTaskId(taskId);
				if (update > 0) {
					logger.info("{} Thread acquired Task: {}", Thread.currentThread().getName(), taskId);

					// Release permit only after updating task table
					MccCCBatchService.releaseLock();

					// Process Data
					try (CustomChecklistBatch customChecklistBatch = new CustomChecklistBatch(taskId);) {
						customChecklistBatch.processData();
					} catch (Exception ex) {
						logger.error("Exception in main():: {}", ex.getMessage());
					}
				} else {
					logger.error("{} Thread failed to acquired Task: {}, stopping.", Thread.currentThread().getName(),
							taskId);
					// Release permit
					MccCCBatchService.releaseLock();
				}
			} else {
				logger.error("{} Thread unable to fetch a Task, stopping", Thread.currentThread().getName());
				// Release permit
				MccCCBatchService.releaseLock();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
