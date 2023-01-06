package org.cap.cc.batch.model;

import java.io.Serializable;

public class ChklstPreviewInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7043338839931283853L;
	private int phase1Cnt;
	private int phase2Cnt;
	private int criticalQuestCnt;
	private byte[] chklstData;

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

	public int getCriticalQuestCnt() {
		return criticalQuestCnt;
	}

	public void setCriticalQuestCnt(int criticalQuestCnt) {
		this.criticalQuestCnt = criticalQuestCnt;
	}

	public byte[] getChklstData() {
		return chklstData;
	}

	public void setChklstData(byte[] chklstData) {
		this.chklstData = chklstData;
	}
}
