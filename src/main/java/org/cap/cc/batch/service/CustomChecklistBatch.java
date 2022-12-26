package org.cap.cc.batch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class CustomChecklistBatch {
	static org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(CustomChecklistBatch.class);

	private static int processId;
	private static  RestTemplate restTemplate;
	
	



	
	
	public static void processData() {

		
		if(restTemplate==null) {
			getRestTemplate();
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
	

}
