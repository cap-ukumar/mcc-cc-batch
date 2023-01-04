package org.cap.cc.batch.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ChecklistRequest {

	// ChecklistResponse
	@JsonIgnore
	ChecklistResponse checklistResponse;

	private String userName;
	private final String password = "";

	// edition
	private String editionId;

	// module
	private String moduleId;

	// au_id
	private String auId;

	// su_id
	private String suId;

	// chklst_dt
	private String actEffectiveDt;
	private String outputOptions;
	private String channelData;
	private PrinterData printerData;

	// task_u
	@JsonIgnore
	private Integer taskU;

	// item_seq_no
	@JsonIgnore
	private Integer itemSeqNo;

	// cycle_seq_no
	@JsonIgnore
	private Integer cycleSeqNo;

	// packet_type
	@JsonIgnore
	private String packetType;

	@JsonIgnore
	private String printSetDetailC;

	// total_crit
	@JsonIgnore
	private int criticalQuestCnt;

	// total_ph1
	@JsonIgnore
	private int phase1Cnt;

	// total_ph2
	@JsonIgnore
	private int phase2Cnt;

	public ChecklistResponse getChecklistResponse() {
		return checklistResponse;
	}

	public void setChecklistResponse(ChecklistResponse checklistResponse) {
		this.checklistResponse = checklistResponse;
	}

	public int getCriticalQuestCnt() {
		return criticalQuestCnt;
	}

	public void setCriticalQuestCnt(int criticalQuestCnt) {
		this.criticalQuestCnt = criticalQuestCnt;
	}

	public int getPhase1Cnt() {
		return phase1Cnt;
	}

	public void setPhase1Cnt(int phase1Cnt) {
		this.phase1Cnt = phase1Cnt;
	}

	public int getPhase2Cnt() {
		return phase2Cnt;
	}

	public void setPhase2Cnt(int phase2Cnt) {
		this.phase2Cnt = phase2Cnt;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public String getEditionId() {
		return editionId;
	}

	public void setEditionId(String editionId) {
		this.editionId = editionId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getAuId() {
		return auId;
	}

	public void setAuId(String auId) {
		this.auId = auId;
	}

	public String getSuId() {
		return suId;
	}

	public void setSuId(String suId) {
		this.suId = suId;
	}

	public String getActEffectiveDt() {
		return actEffectiveDt;
	}

	public void setActEffectiveDt(String actEffectiveDt) {
		this.actEffectiveDt = actEffectiveDt;
	}

	public String getOutputOptions() {
		return outputOptions;
	}

	public void setOutputOptions(String outputOptions) {
		this.outputOptions = outputOptions;
	}

	public String getChannelData() {
		return channelData;
	}

	public void setChannelData(String channelData) {
		this.channelData = channelData;
	}

	public PrinterData getPrinterData() {
		return printerData;
	}

	public void setPrinterData(PrinterData printerData) {
		this.printerData = printerData;
	}

	public Integer getTaskU() {
		return taskU;
	}

	public void setTaskU(Integer taskU) {
		this.taskU = taskU;
	}

	public Integer getItemSeqNo() {
		return itemSeqNo;
	}

	public void setItemSeqNo(Integer itemSeqNo) {
		this.itemSeqNo = itemSeqNo;
	}

	public Integer getCycleSeqNo() {
		return cycleSeqNo;
	}

	public void setCycleSeqNo(Integer cycleSeqNo) {
		this.cycleSeqNo = cycleSeqNo;
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public String getPrintSetDetailC() {
		return printSetDetailC;
	}

	public void setPrintSetDetailC(String printSetDetailC) {
		this.printSetDetailC = printSetDetailC;
	}

	@Override
	public String toString() {
		return "ChecklistRequest [userName=" + userName + ", password=" + password + ", editionId=" + editionId
				+ ", moduleId=" + moduleId + ", auId=" + auId + ", suId=" + suId + ", actEffectiveDt=" + actEffectiveDt
				+ ", outputOptions=" + outputOptions + ", channelData=" + channelData + ", printerData=" + printerData
				+ ", taskU=" + taskU + ", itemSeqNo=" + itemSeqNo + ", cycleSeqNo=" + cycleSeqNo + ", packetType="
				+ packetType + ", printSetDetailC=" + printSetDetailC + ", criticalQuestCnt=" + criticalQuestCnt
				+ ", phase1Cnt=" + phase1Cnt + ", phase2Cnt=" + phase2Cnt + "]";
	}

	public String toJsonString() {
		return "\r\n{\r\n" + "\"userName\":\"" + this.getUserName() + "\",\r\n" + "\"password\":\"" + this.password
				+ "\",\r\n" + "\"editionId\":\"" + getEditionId() + "\",\r\n" + "\"moduleId\":\"" + getModuleId()
				+ "\",\r\n" + "\"auId\":\"" + getAuId() + "\",\r\n" + "\"suId\":\"" + getSuId() + "\",\r\n"
				+ "\"actEffectiveDt\":\"" + getActEffectiveDt() + "\",\r\n" + "\"outputOptions\":\""
				+ getOutputOptions() + "\",\r\n" + "\"channelData\":\"" + getChannelData() + "\",\r\n" + " "
				+ getPrinterData() + "}";
	}

}
