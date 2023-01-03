package org.cap.cc.batch;

import org.cap.cc.batch.service.CustomChecklistBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MccCCBatchService {
	
	private static Logger logger = LoggerFactory.getLogger(MccCCBatchService.class);

	public static void main(String[] args) {

		try (CustomChecklistBatch customChecklistBatch = new CustomChecklistBatch();) {
			customChecklistBatch.processData();
		} catch (Exception ex) {
			logger.error("Exception in main():: {}",ex.getMessage());
		}

	}

}
