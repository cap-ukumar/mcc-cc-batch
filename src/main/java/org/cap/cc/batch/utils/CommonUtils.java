package org.cap.cc.batch.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

public class CommonUtils {
	
	

	protected static Properties configs;

	private static String s_UUID = null;

	protected static Logger log = LoggerFactory.getLogger(CommonUtils.class);

	static {
		try {
			configs = new Properties();
			configs.load(CommonUtils.class.getResourceAsStream("/Files/CustomChecklist.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
//		return configs.getProperty(key);
		return getParameterFromSSMByName(key);
	}

	// You can use special_instr_t CHAR(255) in ptt_task table for checklist batch.
	public static String getUUID() {
		if (s_UUID == null) {
			s_UUID = UUID.randomUUID().toString();
		}

		return s_UUID;
	}

	public static String getParameterFromSSMByName( String parameterKey) {
		
		try {
			GetParameterRequest parameterRequest = new GetParameterRequest();
			parameterRequest.withName(parameterKey).setWithDecryption(Boolean.valueOf(true));
			log.info("ParameterKey: " + parameterKey);
			AWSSimpleSystemsManagement simpleSystemClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
			GetParameterResult parameterResult = simpleSystemClient.getParameter(parameterRequest);
			return parameterResult.getParameter().getValue();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parameterKey;

	}
	
	





}
