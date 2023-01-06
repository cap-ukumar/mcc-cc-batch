package org.cap.cc.batch.model;

public class ChecklistJobInfo {
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
	private Integer criticalQuestCnt;
	private String finishTime;
	private String message;
	private Integer phase1Cnt;
	private Integer phase2Cnt;
	private String startTime;

	public boolean isBatchJobCompleted() {
		return batchJobCompleted;
	}

	public void setBatchJobCompleted(boolean batchJobCompleted) {
		this.batchJobCompleted = batchJobCompleted;
	}

	public int getBatchJobId() {
		return batchJobId;
	}

	public void setBatchJobId(int batchJobId) {
		this.batchJobId = batchJobId;
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

	public Integer getCriticalQuestCnt() {
		return criticalQuestCnt;
	}

	public void setCriticalQuestCnt(Integer criticalQuestCnt) {
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Integer getPhase1Cnt() {
		return phase1Cnt;
	}

	public void setPhase1Cnt(Integer phase1Cnt) {
		this.phase1Cnt = phase1Cnt;
	}

	public Integer getPhase2Cnt() {
		return phase2Cnt;
	}

	public void setPhase2Cnt(Integer phase2Cnt) {
		this.phase2Cnt = phase2Cnt;
	}

	@Override
	public String toString() {
		return "ChecklistJobInfo [batchJobCompleted=" + batchJobCompleted + ", batchJobId=" + batchJobId
				+ ", batchJobName=" + batchJobName + ", batchJobStatus=" + batchJobStatus + ", batchJobSuccessful="
				+ batchJobSuccessful + ", batchTransactionsCompleted=" + batchTransactionsCompleted
				+ ", batchTransactionsCount=" + batchTransactionsCount + ", batchTransactionsErrored="
				+ batchTransactionsErrored + ", batchTransactionsStopped=" + batchTransactionsStopped
				+ ", batchTransactionsSuccessful=" + batchTransactionsSuccessful + ", criticalQuestCnt="
				+ criticalQuestCnt + ", finishTime=" + finishTime + ", message=" + message + ", phase1Cnt=" + phase1Cnt
				+ ", phase2Cnt=" + phase2Cnt + ", startTime=" + startTime + "]";
	}

}
