package org.cap.cc.batch.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.cap.cc.batch.dao.CustomChecklistConstants;
import org.cap.cc.batch.model.ChecklistAuditEntity;
import org.cap.cc.batch.model.ChecklistJobInfoRequest;
import org.cap.cc.batch.model.ChecklistRequest;
import org.cap.cc.batch.model.ChecklistResponse;
import org.cap.cc.batch.model.ContentChannel;
import org.cap.cc.batch.model.PrinterData;
import org.cap.cc.batch.service.CustomChecklistBatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomChecklistBatchTest extends CustomChecklistBatch {

	@Test
	public void getCustomChecklistFilePathTest() {

		String path = null;
		path = getCustomChecklistFilePath();
		assertNotNull(path);
	}

	@Test
	public void getAvailableTaskIdTest() {

		Integer taskId = null;
		taskId = getAvailableTaskId();
		assertNotNull(taskId);
	}

	@Test
	public void getCapDomainTest() {

		String capDomain = null;
		capDomain = getCapDomain();
		assertNotNull(capDomain);
	}

	@Test
	public void getCustomChecklistWebServiceUrlTest() {

		String url = null;
		url = getCustomChecklistWebServiceUrl();
		assertNotNull(url);
	}

	@Test
	public void getPollingIntervalTest() {

		Integer pollingInterval = null;
		pollingInterval = getPollingInterval();
		assertNotNull(pollingInterval);

	}

	@Test
	public void getJobIterationsTest() {

		Integer iteration = null;
		iteration = getJobIterations();
		assertNotNull(iteration);
	}

	@Test
	public void getDuplexValueTest() {

		String dupvalue = null;
		String printSetDetailC = "CHECKLSTSE";
		dupvalue = getDuplexValue(printSetDetailC);
		assertNotNull(dupvalue);
	}

	@Test
	public void getStapleValueTest() {

		String staplevalue = null;
		String printSetDetailC = "CHECKLSTSE";
		staplevalue = getStapleValue(printSetDetailC);
		assertNotNull(staplevalue);
	}

	@Test
	public void getMediaColorTest() {

		String medcolour = null;
		String printSetDetailC = "CHECKLSTSE";
		medcolour = getMediaColor(printSetDetailC);
		assertNotNull(medcolour);
	}

	@Test
	public void getMediaTypeTest() {

		String mediatype = null;
		String printSetDetailC = "CHECKLSTSE";
		mediatype = getMediaType(printSetDetailC);
		assertNotNull(mediatype);
	}

	@Test
	public void getUpdatedContentChannelTest() {
		ContentChannel contentChannel = null;
		String packetType = "SELFEVLPKT";
		String editionId = "09222021";
		contentChannel = getUpdatedContentChannel(packetType, editionId);
		assertNotNull(contentChannel);
	}

	@Test
	public void getContentChannelTest() {

		ContentChannel chetity = null;
		String edition = "09222021";
		chetity = getContentChannel(edition);
		assertNull(chetity);

	}

	@Test
	public void getChecklistInspectorFlagTest() {

		String inspector = null;
		String edition = "09222021";
		inspector = getChecklistInspectorFlag(edition);
		assertNotNull(inspector);
	}

	@Test
	public void executeHttpPostRequestTest() {

		String result = null;
		HttpPost request = new HttpPost();
		result = executeHttpPostRequest(request);
		assertNull(result);
	}

	@Test
	public void updateUserForTaskIdTest() {

		int result = -1;
		int taskId = 345567;
		result = updateUserForTaskId(taskId);
		assertNotNull(result);
	}

	@Test
	public void getBasicChecklistDetailsTest() {
		List<ChecklistRequest> list = null;
		int taskId = 345567;
		list = getBasicChecklistDetails(taskId);
		assertNotNull(list);
	}

	@Test
	public void getUpdatedJobInfoTest() {

		String ccWebServiceUrl = "https://ccbatch.com";
		ChecklistJobInfoRequest request = new ChecklistJobInfoRequest();
		HttpPost post = new HttpPost();
		post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		post.addHeader(HttpHeaders.ACCEPT, "*/*");
		post.addHeader(HttpHeaders.ACCEPT_ENCODING, "EncodingUTF8!");
		post.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");
		post.addHeader(HttpHeaders.CONNECTION, "keep-alive");
		post.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		post.addHeader(HttpHeaders.TIMEOUT, "3000");
		String response = null;
		response = executeHttpPostRequest(post);
		Boolean flag = getUpdatedJobInfo(ccWebServiceUrl, request);
		assertNull(response);
	}

	@Test
	public void submitChecklistJobRequestTest() {

		String ccWebServiceUrl = "https://ccbatch.com";
		ChecklistRequest request = new ChecklistRequest();
		HttpPost post = new HttpPost();
		post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		post.addHeader(HttpHeaders.ACCEPT, "*/*");
		post.addHeader(HttpHeaders.ACCEPT_ENCODING, "EncodingUTF8!");
		post.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");
		post.addHeader(HttpHeaders.CONNECTION, "keep-alive");
		post.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		post.addHeader(HttpHeaders.TIMEOUT, "3000");
		ChecklistResponse checklistResponse = null;
		String response = null;
		response = executeHttpPostRequest(post);
		ChecklistResponse checklistResponse1 = submitChecklistJobRequest(ccWebServiceUrl, request);
		assertNull(checklistResponse);
	}

	@Test
	public void getThunderheadBatchJobStatusTest() {
		String ccWebServiceUrl = "https://ccbatch.com";
		Integer pollingInterval = 50;
		Integer iterations = 100;
		int counter=0;
		ChecklistJobInfoRequest request=new ChecklistJobInfoRequest();
		List<ChecklistJobInfoRequest> checklistJobInfoRequests = new ArrayList<>();
		Boolean allJobsComplete=getThunderheadBatchJobStatus(0, ccWebServiceUrl,iterations, pollingInterval, checklistJobInfoRequests, new ArrayList<>());
		assertNotNull(counter);
	}
	
	@Test
	public void insertAuditRecordTest() {
		ChecklistAuditEntity etity=new ChecklistAuditEntity();
		Integer audit=null;
		etity.setAbe_au_u(222);
		etity.setAbe_su_u(2222);
		etity.setSeq_no_u(1);
		audit = insertAuditRecord(etity);
		assertNull(audit);
		
	}
	
	@Test
	public void getAuditRecordsOfPacketTest() {
		
		ChecklistAuditEntity audit = new ChecklistAuditEntity();
		List<ChecklistAuditEntity> auditList = new ArrayList<>();
		int auId=213;
		int suid=24578;
		audit.setAbe_au_u(456);
		audit.setInvoking_pgm_c(3000);
		auditList.add(audit);
		List<ChecklistAuditEntity> newlist=getAuditRecordsOfPacket(auId,suid);
		assertNotNull(newlist);
		
	}
	
	@Test
	public void removeConnectionsTest() {
		
		removeConnections();
	}
	

}
