package org.cap.cc.batch.utils;

public class CapConfigConstants {
	
	
	public static final String INFORMIX_URL  = "INFORMIX_URL";
	public static final String INFORMIX_USERNAME  = "INFORMIX_USERNAME"; 
	public static final String INFORMIX_PASSWORD  = "INFORMIX_PASSWORD";
	public static final String INFORMIX_DRIVER_CLASS = "INFORMIX_DRIVER_CLASS";
	
	
	
	public static final String POSTGRES_URL = "POSTGRES_URL";
	public static final String POSTGRES_USERNAME = "POSTGRES_USERNAME"; 
	public static final String POSTGRES_PASSWORD = "POSTGRES_PASSWORD";
	public static final String POSTGRES_DRIVER_CLASS = "POSTGRES_DRIVER_CLASS"; 
	
	
	public static final String ORACLE_URL = "ORACLE_URL";
	public static final String ORACLE_USERNAME = "ORACLE_USERNAME"; 
	public static final String ORACLE_PASSWORD = "ORACLE_PASSWORD";
	
	public static final String ORACLE_DRIVER_CLASS = "ORACLE_DRIVER_CLASS";
	public static final String COMMON_SERVICE_BASE_URL = "COMMON_SERVICE_BASE_URL"; 
	
	public static final String FILE_PATH = "FILE_PATH"; 
	
	public static final String TRACKING_END_POINT = "/tr-tracking";
	public static final String CUSTOMER_DATA_END_POINT = "/customer-data-details";

	
	public static final String CUSTOMER_NAME = "CUSTOMER_NAME";
	public static final String CUSTOMER_NUMBER = "CUSTOMER_NUMBER"; 
	public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
	public static final String LBN2 = "LBN2";
	public static final String CONTACT_PARTY_ID = "CONTACT_PARTY_ID"; 
	public static final String CONTACT_FNAME = "CONTACT_FNAME";
	public static final String CONTACT_LNAME = "CONTACT_LNAME"; 
	public static final String PREFIX = "PREFIX";
	public static final String SUFFIX = "SUFFIX"; 
	public static final String CREDENTIALS = "CREDENTIALS";
	public static final String CONTACT_ROLE_NAME = "CONTACT_ROLE_NAME";
	public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS"; 
	public static final String CONTACT_ROLE_CODE = "CONTACT_ROLE_CODE";
	public static final String REQUEST_DETAILS_ENDPOINT = "/request-details";
	public static final String SCHEDULE_DETAILS_ENDPOINT = "/scheduler-process";
	public static final String UPDATE_SCHEDULE_ENDPOINT = "/scheduler-process-details";
	public static final String CUSTOMER_DETAILS_PROCEDURE = "{call APPS.XXCAP_QUADIENT_DATA_PKG.get_contact_data(?, ?, 'PARTY','ODR_DEF',? )}";
	public static final String BATCH = "Batch";
	public static final String DOC = "DOC";
	public static final String JSON_FILE_RESPONSE_TYPE = "JSON_FILE_RESPONSE";
}
