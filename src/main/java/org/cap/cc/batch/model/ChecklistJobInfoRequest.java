package org.cap.cc.batch.model;

import java.io.Serializable;

public class ChecklistJobInfoRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8264504109429707728L;

	private String userName;
	private String password;
	private boolean batchJobCompleted;
	private int batchJobId;
	private String batchJobName;
	private String batchJobStatus;
	private boolean batchJobSuccessful;
	private int batchTransactionsCompleted;
	private int batchTransactionsCount;
	private int batchTransactionsErrored;
	private int batchTransactionsStopped;
	private int batchTransactionsSuccessful;
	private int criticalQuestCnt;
	private String finishTime;
	private String message;
	private int phase1Cnt;
	private int phase2Cnt;
	private String startTime;

	public ChecklistJobInfoRequest() {

	}

	public ChecklistJobInfoRequest(String userName, String password, boolean batchJobCompleted, int batchJobId,
			String batchJobName, String batchJobStatus, boolean batchJobSuccessful, int batchTransactionsCompleted,
			int batchTransactionsCount, int batchTransactionsErrored, int batchTransactionsStopped,
			int batchTransactionsSuccessful, int criticalQuestCnt, String finishTime, String message, int phase1Cnt,
			int phase2Cnt, String startTime) {
		super();
		this.userName = userName;
		this.password = password;
		this.batchJobCompleted = batchJobCompleted;
		this.batchJobId = batchJobId;
		this.batchJobName = batchJobName;
		this.batchJobStatus = batchJobStatus;
		this.batchJobSuccessful = batchJobSuccessful;
		this.batchTransactionsCompleted = batchTransactionsCompleted;
		this.batchTransactionsCount = batchTransactionsCount;
		this.batchTransactionsErrored = batchTransactionsErrored;
		this.batchTransactionsStopped = batchTransactionsStopped;
		this.batchTransactionsSuccessful = batchTransactionsSuccessful;
		this.criticalQuestCnt = criticalQuestCnt;
		this.finishTime = finishTime;
		this.message = message;
		this.phase1Cnt = phase1Cnt;
		this.phase2Cnt = phase2Cnt;
		this.startTime = startTime;
	}

	public int getBatchJobId() {
		return batchJobId;
	}

	public void setBatchJobId(int batchJobId) {
		this.batchJobId = batchJobId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isBatchJobCompleted() {
		return batchJobCompleted;
	}

	public void setBatchJobCompleted(boolean batchJobCompleted) {
		this.batchJobCompleted = batchJobCompleted;
	}

	public String getBatchJobName() {
		return batchJobName;
	}

	public void setBatchJobName(String batchJobName) {
		this.batchJobName = batchJobName;
	}

	public String getBatchJobStatus() {
		return batchJobStatus;
	}

	public void setBatchJobStatus(String batchJobStatus) {
		this.batchJobStatus = batchJobStatus;
	}

	public boolean isBatchJobSuccessful() {
		return batchJobSuccessful;
	}

	public void setBatchJobSuccessful(boolean batchJobSuccessful) {
		this.batchJobSuccessful = batchJobSuccessful;
	}

	public int getBatchTransactionsCompleted() {
		return batchTransactionsCompleted;
	}

	public void setBatchTransactionsCompleted(int batchTransactionsCompleted) {
		this.batchTransactionsCompleted = batchTransactionsCompleted;
	}

	public int getBatchTransactionsCount() {
		return batchTransactionsCount;
	}

	public void setBatchTransactionsCount(int batchTransactionsCount) {
		this.batchTransactionsCount = batchTransactionsCount;
	}

	public int getBatchTransactionsErrored() {
		return batchTransactionsErrored;
	}

	public void setBatchTransactionsErrored(int batchTransactionsErrored) {
		this.batchTransactionsErrored = batchTransactionsErrored;
	}

	public int getBatchTransactionsStopped() {
		return batchTransactionsStopped;
	}

	public void setBatchTransactionsStopped(int batchTransactionsStopped) {
		this.batchTransactionsStopped = batchTransactionsStopped;
	}

	public int getBatchTransactionsSuccessful() {
		return batchTransactionsSuccessful;
	}

	public void setBatchTransactionsSuccessful(int batchTransactionsSuccessful) {
		this.batchTransactionsSuccessful = batchTransactionsSuccessful;
	}

	public int getCriticalQuestCnt() {
		return criticalQuestCnt;
	}

	public void setCriticalQuestCnt(int criticalQuestCnt) {
		this.criticalQuestCnt = criticalQuestCnt;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "ChecklistJobInfoRequest [userName=" + userName + ", batchJobCompleted=" + batchJobCompleted
				+ ", batchJobId=" + batchJobId + ", batchJobName=" + batchJobName + ", batchJobStatus=" + batchJobStatus
				+ ", batchJobSuccessful=" + batchJobSuccessful + ", batchTransactionsCompleted="
				+ batchTransactionsCompleted + ", batchTransactionsCount=" + batchTransactionsCount
				+ ", batchTransactionsErrored=" + batchTransactionsErrored + ", batchTransactionsStopped="
				+ batchTransactionsStopped + ", batchTransactionsSuccessful=" + batchTransactionsSuccessful
				+ ", criticalQuestCnt=" + criticalQuestCnt + ", finishTime=" + finishTime + ", message=" + message
				+ ", phase1Cnt=" + phase1Cnt + ", phase2Cnt=" + phase2Cnt + ", startTime=" + startTime + "]";
	}

}
