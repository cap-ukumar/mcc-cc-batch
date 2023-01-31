package org.cap.cc.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.cap.cc.batch.dao.CustomLoggingEvents;
import org.cap.cc.batch.repository.CustomChecklistBatchRepository;
import org.cap.cc.batch.service.CustomChecklistBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class MccCCBatchService {

	private static Logger logger = LoggerFactory.getLogger(MccCCBatchService.class);

	private Semaphore semaphore = new Semaphore(1);

	private CustomChecklistBatchRepository repository;

	private int tempTaskId = 1234;

	private boolean isFirstTask = true;

	public MccCCBatchService() {
		repository = new CustomChecklistBatchRepository();
	}

	public CustomChecklistBatchRepository getRepository() {
		return repository;
	}

	// Acquire Shared permit to get taskId
	public int fetchTask() {
		int task = 0;
		try {
			semaphore.acquire();
			task = repository.getAvailableTaskId();
			if (isFirstTask && task > 0) {
				repository.logEventInMccDB(CustomLoggingEvents.BATCH_STARTED, task);
				isFirstTask = false;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return task;
	}

	// Finally Release permit only after updating task table
	public boolean updateTask(int taskId) {
		int result = repository.updateUserForTaskId(taskId);
		releaseLock();
		return result > 0;
	}

	// Release Lock
	public void releaseLock() {
		semaphore.release();
	}

	public int getTempTaskId() {
		return tempTaskId;
	}

	public void setTempTaskId(int tempTaskId) {
		this.tempTaskId = tempTaskId;
	}

	public static void main(String[] args) {

		executeBatch();

	}

	private static void executeBatch() {
		int totalTasks = 0;
		int processedTasks = 0;

		MccCCBatchService mccCcBatch = new MccCCBatchService();

		BasicThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("Task-%d")
				.priority(Thread.MAX_PRIORITY).build();
		ExecutorService service = Executors.newFixedThreadPool(4, factory);
		List<Callable<Boolean>> callableList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Callable<Boolean> runner = new Runner(mccCcBatch);
			callableList.add(runner);
		}
		try {

			List<Future<Boolean>> futureList = service.invokeAll(callableList);
			for (Future<Boolean> future : futureList) {
				boolean isJobCompleted = false;
				try {
					while (null == future || !future.isDone()) {
						logger.info("Waiting for Future to return:");
					}
					isJobCompleted = future.get();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				if (isJobCompleted)
					processedTasks++;
				totalTasks++;
			}
			service.shutdown();
			service.awaitTermination(1L, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Finally log Cc batch Finished
		mccCcBatch.getRepository().logEventInMccDB(CustomLoggingEvents.BATCH_FINISHED, mccCcBatch.getTempTaskId(),
				String.valueOf(totalTasks), String.valueOf(totalTasks - processedTasks));
	}

}

class Runner implements Callable<Boolean> {
	private Logger logger = LoggerFactory.getLogger(Runner.class);

	MccCCBatchService mccCcBatch;

	public Runner(MccCCBatchService mccCcBatch) {
		this.mccCcBatch = mccCcBatch;
	}

	@Override
	public Boolean call() {
		MDC.put("taskId", Thread.currentThread().getName());
		boolean result = false;
		try {
			Thread.sleep(1000);
			// Acquire Permit
			int taskId = mccCcBatch.fetchTask();
			if (taskId > 0) {
				// Release Permit either true or false
				boolean isUpdated = mccCcBatch.updateTask(taskId);
				mccCcBatch.setTempTaskId(taskId);
				if (isUpdated) {
					logger.info("{} Thread acquired Task: {}", Thread.currentThread().getName(), taskId);
					/*
					 * Update User_u of ptt_task
					 */
					logger.info("{} Thread Updated ptt_task table for Task: {}.", Thread.currentThread().getName(),
							taskId);

					// Process Data
					CustomChecklistBatch customChecklistBatch = new CustomChecklistBatch(taskId, logger);
					result = customChecklistBatch.processData();

				} else {
					logger.error("{} Thread failed to update ptt_task table for Task: {}, stopping.",
							Thread.currentThread().getName(), taskId);
					mccCcBatch.getRepository().logEventInMccDB(CustomLoggingEvents.TASK_EXCEPTION, taskId,
							" Failed to update ptt_task table for Task: ");
				}
			} else {
				logger.error("{} Thread unable to fetch a Task, stopping", Thread.currentThread().getName());
				// Release permit
				mccCcBatch.releaseLock();
				mccCcBatch.getRepository().logEventInMccDB(CustomLoggingEvents.TASK_EXCEPTION,
						mccCcBatch.getTempTaskId(), " Unable to fetch a Task: ");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

}
