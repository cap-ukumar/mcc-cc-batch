package org.cap.cc.batch;

import java.io.InputStream;

import org.cap.cc.batch.service.CustomChecklistBatch;
import org.cap.cc.batch.utils.CommonUtils;

public class MccCCBatchService {
	
	public static void main(String[] args) {
		
		try {
			InputStream stream = MccCCBatchService.class.getResourceAsStream("/Files/CustomChecklist.properties");
			CommonUtils.loadProperties(stream);
			CustomChecklistBatch.processData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
